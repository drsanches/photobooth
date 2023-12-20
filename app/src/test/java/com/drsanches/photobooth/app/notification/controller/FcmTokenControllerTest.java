package com.drsanches.photobooth.app.notification.controller;

import com.drsanches.photobooth.app.Application;
import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.notifier.data.fcm.repository.FcmTokenRepository;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenDto;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenExpiresDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.function.Supplier;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
class FcmTokenControllerTest {

    private static final Supplier<String> USERNAME = () -> "username-" + UUID.randomUUID().toString().substring(0, 10);
    private static final Supplier<String> PASSWORD = () -> UUID.randomUUID().toString();
    private static final Supplier<String> EMAIL = () -> UUID.randomUUID() + "@example.com";
    private static final Supplier<String> FCM_TOKEN = () -> UUID.randomUUID().toString();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SecurityFilterChain filterChain;

    @Autowired
    private UserIntegrationDomainService userIntegrationDomainService;

    @Autowired
    private CredentialsHelper credentialsHelper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    private MockMvc mvc;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(filterChain.getFilters().toArray(new Filter[0]))
                .build();
    }

    @Test
    void addToken() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var fcmToken = FCM_TOKEN.get();
        var token = createUser(username, password, email);
        var userId = getUserAuthInfo(token.getAccessToken()).getId();

        var before = new GregorianCalendar();
        var result = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/notification/fcm/token")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new FcmTokenDto(fcmToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("expires").exists())
                .andReturn().getResponse().getContentAsString();
        var after = new GregorianCalendar();

        var expires = convertToGregorianCalendar(objectMapper.readValue(result, FcmTokenExpiresDto.class).getExpires());
        before.add(Calendar.MONTH, 2);
        after.add(Calendar.MONTH, 2);
        Assertions.assertTrue(expires.after(before));
        Assertions.assertTrue(expires.before(after));

        var fcmTokenList = fcmTokenRepository.findByUserId(userId);
        Assertions.assertEquals(fcmTokenList.size(), 1);
        Assertions.assertEquals(fcmTokenList.get(0).getToken(), fcmToken);
        Assertions.assertEquals(fcmTokenList.get(0).getUserId(), userId);
        Assertions.assertTrue(fcmTokenList.get(0).getExpires().after(before));
        Assertions.assertTrue(fcmTokenList.get(0).getExpires().before(after));
    }

    @Test
    void addTokenTwice() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var fcmToken = FCM_TOKEN.get();
        var token = createUser(username, password, email);
        var userId = getUserAuthInfo(token.getAccessToken()).getId();

        var result = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/notification/fcm/token")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new FcmTokenDto(fcmToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("expires").exists())
                .andReturn().getResponse().getContentAsString();

        var expires = objectMapper.readValue(result, FcmTokenExpiresDto.class).getExpires();

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/notification/fcm/token")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new FcmTokenDto(fcmToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("expires").value(expires));

        var fcmTokenList = fcmTokenRepository.findByUserId(userId);
        Assertions.assertEquals(fcmTokenList.size(), 1);
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

    private UserAuthInfoDto getUserAuthInfo(String accessToken) throws Exception {
        var result = mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/auth/info")
                .header("Authorization", "Bearer " + accessToken))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(result, UserAuthInfoDto.class);
    }

    private GregorianCalendar convertToGregorianCalendar(String source) throws ParseException {
        DateFormat df = new SimpleDateFormat(GregorianCalendarConvertor.PATTERN);
        Date parsed = df.parse(source);
        GregorianCalendar result = new GregorianCalendar();
        result.setTime(parsed);
        return result;
    }
}
