package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.Application;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationCodeGenerator;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.service.GoogleUserInfoService;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.notifier.service.notifier.email.service.EmailService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
class GoogleAuthControllerTest {

    private static final Supplier<String> USERNAME = () -> "username-" + UUID.randomUUID().toString().substring(0, 10);
    private static final Supplier<String> EMAIL = () -> UUID.randomUUID() + "@google.com";
    private static final Supplier<String> CONFIRMATION_CODE = () -> UUID.randomUUID().toString();

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private SecurityFilterChain filterChain;
    @Autowired
    private UserIntegrationDomainService userIntegrationDomainService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CredentialsHelper credentialsHelper;
    @Autowired
    private TokenService tokenService;
    @MockBean
    private GoogleUserInfoService googleUserInfoService;
    @MockBean
    private ConfirmationCodeGenerator confirmationCodeGenerator;
    @MockBean
    private EmailService emailService;

    private MockMvc mvc;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(filterChain.getFilters().toArray(new Filter[0]))
                .build();
    }

    @Test
    void getTokenForNewEmail() throws Exception {
        var googleToken = UUID.randomUUID().toString();
        var username = USERNAME.get();
        var email = EMAIL.get();
        var confirmationCode = mockConfirmationCodeGenerator();
        when(googleUserInfoService.getGoogleInfo(googleToken)).thenReturn(new GoogleInfoDto(
                email,
                null,
                null,
                null,
                null,
                null,
                null
        ));

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
        createUser(username, UUID.randomUUID().toString(), email);

        when(googleUserInfoService.getGoogleInfo(googleToken)).thenReturn(new GoogleInfoDto(
                email,
                null,
                null,
                null,
                null,
                null,
                null
        ));

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
        var token = createUser(username, UUID.randomUUID().toString(), email);
        when(googleUserInfoService.getGoogleInfo(googleToken)).thenReturn(new GoogleInfoDto(
                googleEmail,
                null,
                null,
                null,
                null,
                null,
                null
        ));

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

    private ResultActions performGetInfo(String accessToken) throws Exception {
        return mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/auth/info")
                .header("Authorization", "Bearer " + accessToken));
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
}
