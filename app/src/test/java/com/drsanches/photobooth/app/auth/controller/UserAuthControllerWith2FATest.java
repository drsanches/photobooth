package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.Application;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationCodeGenerator;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeEmailDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangeUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ConfirmationCodeDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.LoginDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.RegistrationDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.service.UserAuthWebService;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.notifier.email.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
class UserAuthControllerWith2FATest {

    private static final Supplier<String> USERNAME = () -> "username-" + UUID.randomUUID().toString().substring(0, 10);
    private static final Supplier<String> PASSWORD = () -> UUID.randomUUID().toString();
    private static final Supplier<String> EMAIL = () -> UUID.randomUUID() + "@example.com";
    private static final Supplier<String> CONFIRMATION_CODE = () -> UUID.randomUUID().toString();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SecurityFilterChain filterChain;

    @Autowired
    private UserAuthWebService userAuthWebService;

    @Autowired
    private UserIntegrationDomainService userIntegrationDomainService;

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Autowired
    private CredentialsHelper credentialsHelper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ConfirmationCodeGenerator confirmationCodeGenerator;

    private MockMvc mvc;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(filterChain.getFilters().toArray(new Filter[0]))
                .build();
        ReflectionTestUtils.setField(userAuthWebService, "with2FA", true);
    }

    @Test
    void registration() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new RegistrationDto(username, password, email))))
                .andExpect(status().isOk());

        var result = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/registration/confirm")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ConfirmationCodeDto(confirmationCode))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        verify(emailService, times(2)).sendHtmlMessage(eq(email), any(), any());
        var token = objectMapper.readValue(result, TokenDto.class);
        performGetInfo(token.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email));
    }

    @Test
    void changeUsername() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var token = createUser(username, password, email);
        var newUsername = USERNAME.get();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/changeUsername")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ChangeUsernameDto(newUsername))))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/changeUsername/confirm")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ConfirmationCodeDto(confirmationCode))))
                .andExpect(status().isOk());

        verify(emailService, times(2)).sendHtmlMessage(eq(email), any(), any());
        performLogin(username, password)
                .andExpect(status().isUnauthorized());
        performGetInfo(getToken(newUsername, password).getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(newUsername))
                .andExpect(jsonPath("email").value(email));
    }

    @Test
    void changePassword() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var token = createUser(username, password, email);
        var newPassword = PASSWORD.get();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/changePassword")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ChangePasswordDto(newPassword))))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/changePassword/confirm")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ConfirmationCodeDto(confirmationCode))))
                .andExpect(status().isOk());

        verify(emailService, times(2)).sendHtmlMessage(eq(email), any(), any());
        performLogin(username, password)
                .andExpect(status().isUnauthorized());
        performLogin(username, newPassword)
                .andExpect(status().isOk());
    }

    @Test
    void changeEmail() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var token = createUser(username, password, email);
        var newEmail = EMAIL.get();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/changeEmail")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ChangeEmailDto(newEmail))))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/changeEmail/confirm")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ConfirmationCodeDto(confirmationCode))))
                .andExpect(status().isOk());

        verify(emailService).sendHtmlMessage(eq(email), any(), any());
        verify(emailService).sendHtmlMessage(eq(newEmail), any(), any());
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
                        .post("/api/v1/auth/deleteUser")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/deleteUser/confirm")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ConfirmationCodeDto(confirmationCode))))
                .andExpect(status().isOk());

        verify(emailService, times(2)).sendHtmlMessage(eq(email), any(), any());
        performLogin(username, password)
                .andExpect(status().isUnauthorized());
    }

    private String mockConfirmationCodeGenerator() {
        var confirmationCode = CONFIRMATION_CODE.get();
        when(confirmationCodeGenerator.generate()).thenReturn(confirmationCode);
        return confirmationCode;
    }

    private Token createUser(String username, String password, String email) {
        var salt = UUID.randomUUID().toString();
        var user = userIntegrationDomainService.createUser(
                username,
                email,
                credentialsHelper.encodePassword(password, salt),
                salt
        );
        return tokenService.createToken(user.getId(), user.getRole());
    }

    private Token getToken(String username, String password) {
        var userAuth = userAuthDomainService.getEnabledByUsername(username);
        credentialsHelper.checkPassword(password, userAuth.getPassword(), userAuth.getSalt());
        return tokenService.createToken(userAuth.getId(), userAuth.getRole());
    }

    private ResultActions performLogin(String username, String password) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new LoginDto(username, password))));
    }

    private ResultActions performGetInfo(String accessToken) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/auth/info")
                        .header("Authorization", "Bearer " + accessToken));
    }
}
