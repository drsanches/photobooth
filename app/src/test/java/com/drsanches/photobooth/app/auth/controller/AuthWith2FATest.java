package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.BaseSpringTest;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeEmailDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.LoginDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.RegistrationDto;
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
                        .content(objectMapper.writeValueAsString(new RegistrationDto(username, password, email))))
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
                        .content(objectMapper.writeValueAsString(new ChangeUsernameDto(newUsername))))
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
                        .content(objectMapper.writeValueAsString(new ChangePasswordDto(newPassword))))
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
                        .content(objectMapper.writeValueAsString(new ChangeEmailDto(newEmail))))
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
        var userAuth = userAuthDomainService.getEnabledByUsername(username);
        credentialsHelper.checkPassword(password, userAuth.getPassword(), userAuth.getSalt());
        return tokenService.createToken(userAuth.getId(), userAuth.getRole());
    }

    private ResultActions performLogin(String username, String password) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .post("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new LoginDto(username, password))));
    }
}
