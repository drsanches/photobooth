package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.BaseSpringTest;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationCodeGenerator;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
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
import static org.mockito.Mockito.only;
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
    private ConfirmationCodeGenerator confirmationCodeGenerator;
    @MockBean
    private EmailService emailService;

    @Test
    void getTokenForNewEmail() throws Exception {
        var googleToken = UUID.randomUUID().toString();
        var username = USERNAME.get();
        var email = EMAIL.get();
        var googleInfo = new GoogleInfoDto(
                email,
                null,
                NAME.get(),
                URL.get(),
                null,
                null,
                null
        );
        var confirmationCode = mockConfirmationCodeGenerator();
        when(googleUserInfoService.getGoogleInfo(googleToken)).thenReturn(googleInfo);
        when(googleUserInfoService.safetyGetPicture(googleInfo.getPicture())).thenReturn(AVATAR.get());

        var registrationResponse = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
                .andExpect(jsonPath("changeUsernameCode").value(confirmationCode))
                .andReturn().getResponse().getContentAsString();
        var registrationResult = objectMapper.readValue(registrationResponse, GoogleGetTokenDto.class);

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/setUsername")
                        .header("Authorization", "Bearer " + registrationResult.getToken().getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(
                                new GoogleSetUsernameDto(username, registrationResult.getChangeUsernameCode())
                        )))
                .andExpect(status().isOk());

        var loginResponse = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var loginResult = objectMapper.readValue(loginResponse, GoogleGetTokenDto.class);
        performGetInfo(loginResult.getToken().getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(false))
                .andExpect(jsonPath("googleAuth").value(email));

        verify(emailService).sendHtmlMessage(eq(email), any(), any());
        verify(googleUserInfoService, times(2)).getGoogleInfo(eq(googleToken));
    }

    @Test
    void getTokenForExistingEmail() throws Exception {
        var googleToken = UUID.randomUUID().toString();
        var username = USERNAME.get();
        var email = EMAIL.get();
        var googleInfo = new GoogleInfoDto(
                email,
                null,
                NAME.get(),
                URL.get(),
                null,
                null,
                null
        );
        createUser(username, UUID.randomUUID().toString(), email);

        when(googleUserInfoService.getGoogleInfo(googleToken)).thenReturn(googleInfo);

        var linkingResponse = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
                .andExpect(jsonPath("changeUsernameCode").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        var linkingResult = objectMapper.readValue(linkingResponse, GoogleGetTokenDto.class);

        performGetInfo(linkingResult.getToken().getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(true))
                .andExpect(jsonPath("googleAuth").value(email));

        verify(googleUserInfoService, only()).getGoogleInfo(eq(googleToken));
        verify(emailService, only()).sendHtmlMessage(any(), any(), any());
    }

    @Test
    public void linkAndUnlink() throws Exception {
        var googleToken = UUID.randomUUID().toString();
        var username = USERNAME.get();
        var email = EMAIL.get();
        var googleEmail = EMAIL.get();
        var googleInfo = new GoogleInfoDto(
                googleEmail,
                null,
                NAME.get(),
                URL.get(),
                null,
                null,
                null
        );
        var token = createUser(username, UUID.randomUUID().toString(), email);
        when(googleUserInfoService.getGoogleInfo(googleToken)).thenReturn(googleInfo);

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/link")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk());

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

        performGetInfo(token.getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("passwordExists").value(true))
                .andExpect(jsonPath("googleAuth").doesNotExist());

        verify(googleUserInfoService, only()).getGoogleInfo(eq(googleToken));
        verify(emailService, times(2)).sendHtmlMessage(any(), any(), any());
    }

    private String mockConfirmationCodeGenerator() {
        var confirmationCode = CONFIRMATION_CODE.get();
        when(confirmationCodeGenerator.generate()).thenReturn(confirmationCode);
        return confirmationCode;
    }
}
