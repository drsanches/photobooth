package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.BaseSpringTest;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.ChangePasswordDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.LoginDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.RegistrationDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.service.GoogleUserInfoService;
import com.drsanches.photobooth.app.notifier.service.notifier.email.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GoogleAuthControllerTest extends BaseSpringTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GoogleUserInfoService googleUserInfoService;
    @MockBean
    private EmailService emailService;

    @Test
    void createUserByGoogle_setEmail_setPassword() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        var googleToken = UUID.randomUUID().toString();
        mockGoogle(email, googleToken);

        var registrationResponse = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
                .andExpect(jsonPath("changeUsernameCode").value(confirmationCode))
                .andReturn().getResponse().getContentAsString();

        verify(googleUserInfoService).getGoogleInfo(eq(googleToken));
        verify(emailService).sendHtmlMessage(eq(email), any(), any());

        var registrationResult = objectMapper.readValue(registrationResponse, GoogleGetTokenDto.class);
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/username")
                        .header("Authorization", "Bearer " + registrationResult.getToken().getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(
                                new GoogleSetUsernameDto(username, registrationResult.getChangeUsernameCode())
                        )))
                .andExpect(status().isOk());

        var loginByGoogleResponse = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(googleUserInfoService, times(2)).getGoogleInfo(eq(googleToken));

        var loginByGoogleResult = objectMapper.readValue(loginByGoogleResponse, GoogleGetTokenDto.class);
        performGetInfo(loginByGoogleResult.getToken().getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(false))
                .andExpect(jsonPath("googleAuth").value(email));

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account/password")
                        .header("Authorization", "Bearer " + loginByGoogleResult.getToken().getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ChangePasswordDto(password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").doesNotExist())
                .andExpect(jsonPath("with2FA").value(true));

        verify(emailService, times(2)).sendHtmlMessage(eq(email), any(), any());

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/account/confirm/" + confirmationCode))
                .andExpect(status().isOk());

        verify(emailService, times(3)).sendHtmlMessage(eq(email), any(), any());

        var passwordLoginResponse = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new LoginDto(username, password))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var token = objectMapper.readValue(passwordLoginResponse, TokenDto.class);
        performGetInfo(token.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(true))
                .andExpect(jsonPath("googleAuth").value(email));
    }

    @Test
    void createUser_loginByGoogleWithTheSameEmail() throws Exception {
        var username = USERNAME.get();
        var email = EMAIL.get();
        createUser(username, PASSWORD.get(), email);

        var googleToken = UUID.randomUUID().toString();
        mockGoogle(email, googleToken);

        var linkingResponse = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
                .andExpect(jsonPath("changeUsernameCode").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        verify(googleUserInfoService).getGoogleInfo(eq(googleToken));
        verify(emailService).sendHtmlMessage(any(), any(), any());

        var linkingResult = objectMapper.readValue(linkingResponse, GoogleGetTokenDto.class);
        performGetInfo(linkingResult.getToken().getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(true))
                .andExpect(jsonPath("googleAuth").value(email));
    }

    @Test
    public void createUser_linkGoogle_unlinkGoogle() throws Exception {
        var username = USERNAME.get();
        var email = EMAIL.get();
        var token = createUser(username, PASSWORD.get(), email);

        var googleEmail = EMAIL.get();
        var googleToken = UUID.randomUUID().toString();
        mockGoogle(googleEmail, googleToken);

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/link")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk());

        verify(googleUserInfoService).getGoogleInfo(eq(googleToken));
        verify(emailService).sendHtmlMessage(any(), any(), any());

        performGetInfo(token.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(true))
                .andExpect(jsonPath("googleAuth").value(googleEmail));

        mvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/auth/google/link")
                        .header("Authorization", "Bearer " + token.getAccessToken()))
                .andExpect(status().isOk());

        verify(emailService, times(2)).sendHtmlMessage(any(), any(), any());

        performGetInfo(token.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(true))
                .andExpect(jsonPath("googleAuth").doesNotExist());
    }

    @Test
    public void createUserByGoogle_createUserWithTheSameEmail_error() throws Exception {
        var googleToken = UUID.randomUUID().toString();
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        mockGoogle(email, googleToken);

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
                .andExpect(jsonPath("changeUsernameCode").value(confirmationCode))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/account")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new RegistrationDto(username, password, email))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("message")
                        .value("createAccount.registrationDto.email: User with email '" + email + "' already exists"));
    }

    @Test
    public void createUser_createUserByGoogle_linkAccounts_error() throws Exception {
        var username1 = USERNAME.get();
        var email1 = EMAIL.get();
        var token1 = createUser(username1, PASSWORD.get(), email1);

        var googleToken = UUID.randomUUID().toString();
        var email2 = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        mockGoogle(email2, googleToken);

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
                .andExpect(jsonPath("changeUsernameCode").value(confirmationCode))
                .andReturn().getResponse().getContentAsString();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/link")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token1.getAccessToken())
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("message").value("Can not link Google account"));
    }

    @Test
    public void createTwoUsers_linkUserWithGoogle_linkAnotherUserWithTheSameGoogle_error() throws Exception {
        var username1 = USERNAME.get();
        var email1 = EMAIL.get();
        var token1 = createUser(username1, PASSWORD.get(), email1);

        var username2 = USERNAME.get();
        var email2 = EMAIL.get();
        var token2 = createUser(username2, PASSWORD.get(), email2);

        var googleToken = UUID.randomUUID().toString();
        var email3 = EMAIL.get();
        mockGoogle(email3, googleToken);

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/link")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token1.getAccessToken())
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/link")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token2.getAccessToken())
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("uuid").exists())
                .andExpect(jsonPath("message").value("Can not link Google account"));
    }

    private void mockGoogle(String email, String googleToken) {
        var googleInfo = new GoogleInfoDto(
                email,
                null,
                NAME.get(),
                URL.get(),
                null,
                null,
                null
        );
        when(googleUserInfoService.getGoogleInfo(googleToken)).thenReturn(googleInfo);
        when(googleUserInfoService.safetyGetPicture(googleInfo.getPicture())).thenReturn(AVATAR.get());
    }
}
