package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.BaseSpringTest;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdateEmailDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdatePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.UpdateUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GetTokenDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.CreateAccountDto;
import com.drsanches.photobooth.app.auth.service.TwoFactorAuthenticationManager;
import com.drsanches.photobooth.app.auth.service.AccountAuthWebService;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.auth.service.TokenService;
import com.drsanches.photobooth.app.auth.data.token.model.Token;
import com.drsanches.photobooth.app.common.integration.notifier.NotificationService;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthWith2FATest extends BaseSpringTest {

    @Autowired
    private AccountAuthWebService accountAuthWebService;
    @Autowired
    private UserAuthDomainService userAuthDomainService;
    @Autowired
    private CredentialsHelper credentialsHelper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;
    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    void init() {
        when(twoFactorAuthenticationManager.isEnabled(any())).thenReturn(true);
    }

    @Test
    void registration() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateAccountDto(username, password, email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.REGISTRATION_STARTED), any());

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(notificationService).notify(eq(Action.REGISTRATION_COMPLETED), any());
        performGetInfo(getToken(username, password).getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email));
    }

    @Test
    void registrationWithExistentUser() throws Exception {
        var username = USERNAME.get();
        var email = EMAIL.get();
        var googleEmail = EMAIL.get();
        createUser(username, PASSWORD.get(), email);
        createUserByGoogle(USERNAME.get(), PASSWORD.get(), EMAIL.get(), googleEmail);
        mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateAccountDto(USERNAME.get(), PASSWORD.get(), email))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("email.already.in.use"));

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateAccountDto(username, PASSWORD.get(), EMAIL.get()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("username.already.in.use"));

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateAccountDto(USERNAME.get(), PASSWORD.get(), googleEmail))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("email.already.in.use"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void registrationWithRace() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode1 = mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateAccountDto(username, password, email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.REGISTRATION_STARTED), any());
        mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateAccountDto(username, password, EMAIL.get()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("username.already.in.use"));

        verifyNoMoreInteractions(notificationService);
        mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new CreateAccountDto(USERNAME.get(), password, email))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("email.already.in.use"));

        verifyNoMoreInteractions(notificationService);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode1))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(notificationService).notify(eq(Action.REGISTRATION_COMPLETED), any());

        performGetInfo(getToken(username, password).getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email));
    }

    @Test
    void updateUsername() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var token = createUser(username, password, email);
        var newUsername = USERNAME.get();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/username")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateUsernameDto(newUsername))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.USERNAME_CHANGE_STARTED), any());

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode))
                .andExpect(status().isOk());

        verify(notificationService).notify(eq(Action.USERNAME_CHANGE_COMPLETED), any());

        performLogin(username, password)
                .andExpect(status().isUnauthorized());
        performGetInfo(getToken(newUsername, password).getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(newUsername))
                .andExpect(jsonPath("email").value(email));
    }

    @Test
    void updateUsernameWithExistent() throws Exception {
        var username = USERNAME.get();
        var token = createUser(USERNAME.get(), PASSWORD.get(), EMAIL.get());
        createUser(username, PASSWORD.get(), EMAIL.get());
        mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/username")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateUsernameDto(username))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("username.already.in.use"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void updateUsernameWithRace() throws Exception {
        var password1 = PASSWORD.get();
        var email1 = EMAIL.get();
        var newUsername = USERNAME.get();
        var token1 = createUser(USERNAME.get(), password1, email1);
        var token2 = createUser(USERNAME.get(), PASSWORD.get(), EMAIL.get());
        var confirmationCode1 = mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/username")
                        .header("Authorization", "Bearer " + token1.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateUsernameDto(newUsername))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.USERNAME_CHANGE_STARTED), any());
        mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/username")
                        .header("Authorization", "Bearer " + token2.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateUsernameDto(newUsername))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("username.already.in.use"));

        verifyNoMoreInteractions(notificationService);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode1))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(notificationService).notify(eq(Action.USERNAME_CHANGE_COMPLETED), any());

        performGetInfo(getToken(newUsername, password1).getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(newUsername))
                .andExpect(jsonPath("email").value(email1));
    }

    @Test
    void updatePassword() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var token = createUser(username, password, email);
        var newPassword = PASSWORD.get();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/password")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdatePasswordDto(newPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.PASSWORD_CHANGE_STARTED), any());

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode))
                .andExpect(status().isOk());

        verify(notificationService).notify(eq(Action.PASSWORD_CHANGE_COMPLETED), any());

        performLogin(username, password)
                .andExpect(status().isUnauthorized());
        performLogin(username, newPassword)
                .andExpect(status().isOk());
    }

    @Test
    void updateEmail() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var token = createUser(username, password, email);
        var newEmail = EMAIL.get();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/email")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateEmailDto(newEmail))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.EMAIL_CHANGE_STARTED), any());

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode))
                .andExpect(status().isOk());

        verify(notificationService).notify(eq(Action.EMAIL_CHANGE_COMPLETED), any());

        performGetInfo(getToken(username, password).getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(newEmail));
    }

    @Test
    void updateEmailWithExistent() throws Exception {
        var email = EMAIL.get();
        var googleEmail = EMAIL.get();
        var token = createUser(USERNAME.get(), PASSWORD.get(), EMAIL.get());
        createUser(USERNAME.get(), PASSWORD.get(), email);
        createUserByGoogle(USERNAME.get(), PASSWORD.get(), EMAIL.get(), googleEmail);
        mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/email")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateEmailDto(email))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("email.already.in.use"));

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/email")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateEmailDto(googleEmail))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("email.already.in.use"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void updateEmailWithRace() throws Exception {
        var username1 = USERNAME.get();
        var password1 = PASSWORD.get();
        var newEmail = EMAIL.get();
        var token1 = createUser(username1, password1, EMAIL.get());
        var token2 = createUser(USERNAME.get(), PASSWORD.get(), EMAIL.get());
        var confirmationCode1 = mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/email")
                        .header("Authorization", "Bearer " + token1.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateEmailDto(newEmail))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.EMAIL_CHANGE_STARTED), any());
        mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/email")
                        .header("Authorization", "Bearer " + token2.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateEmailDto(newEmail))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("code").value("email.already.in.use"));

        verifyNoMoreInteractions(notificationService);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode1))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(notificationService).notify(eq(Action.EMAIL_CHANGE_COMPLETED), any());

        performGetInfo(getToken(username1, password1).getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(username1))
                .andExpect(jsonPath("email").value(newEmail));
    }

    @Test
    void disableUser() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var token = createUser(username, password, email);

        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/auth/account")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(notificationService).notify(eq(Action.DISABLE_STARTED), any());

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode))
                .andExpect(status().isOk());

        verify(notificationService).notify(eq(Action.DISABLE_COMPLETED), any());

        performLogin(username, password)
                .andExpect(status().isUnauthorized());
    }

    private Token getToken(String username, String password) {
        var userAuth = userAuthDomainService.findEnabledByUsername(username).orElseThrow();
        credentialsHelper.checkPassword(password, userAuth.getPassword(), userAuth.getSalt());
        return tokenService.createToken(userAuth.getId(), userAuth.getRole());
    }

    private ResultActions performLogin(String username, String password) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .post("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new GetTokenDto(username, password))));
    }
}
