package com.drsanches.photobooth.app.auth.controller;

import com.drsanches.photobooth.app.Application;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationCodeGenerator;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.service.GoogleUserInfoService;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
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
    void registration() throws Exception {
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

        var result1 = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var token1 = objectMapper.readValue(result1, GoogleGetTokenDto.class);

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/setUsername")
                        .header("Authorization", "Bearer " + token1.getToken().getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleSetUsernameDto(username, confirmationCode))))
                .andExpect(status().isOk());

        var result2 = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/google/token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new GoogleTokenDto(googleToken))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        verify(emailService).sendHtmlMessage(eq(email), any(), any());
        verify(googleUserInfoService, times(2)).getGoogleInfo(eq(googleToken));
        var token2 = objectMapper.readValue(result2, GoogleGetTokenDto.class);
        performGetInfo(token2.getToken().getAccessToken())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(username))
                .andExpect(jsonPath("email").value(email));
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
}
