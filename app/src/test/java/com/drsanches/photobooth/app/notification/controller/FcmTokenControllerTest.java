package com.drsanches.photobooth.app.notification.controller;

import com.drsanches.photobooth.app.BaseSpringTest;
import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.notifier.data.fcm.repository.FcmTokenRepository;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenDto;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenExpiresDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FcmTokenControllerTest extends BaseSpringTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Test
    void addToken() throws Exception {
        var username = USERNAME.get();
        var password = PASSWORD.get();
        var email = EMAIL.get();
        var fcmToken = FCM_TOKEN.get();
        var token = createUser(username, password, email);
        var userId = getUserAuthInfo(token.getAccessToken()).getId();

        var before = Instant.now().plus(60, ChronoUnit.DAYS);
        var result = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/notification/fcm/token")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new FcmTokenDto(fcmToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("expires").exists())
                .andReturn().getResponse().getContentAsString();
        var after = Instant.now().plus(60, ChronoUnit.DAYS);

        var expires = convertToInstant(objectMapper.readValue(result, FcmTokenExpiresDto.class).getExpires());
        Assertions.assertTrue(expires.isAfter(before));
        Assertions.assertTrue(expires.isBefore(after));

        var fcmTokenList = fcmTokenRepository.findByUserId(userId);
        Assertions.assertEquals(fcmTokenList.size(), 1);
        Assertions.assertEquals(fcmTokenList.get(0).getToken(), fcmToken);
        Assertions.assertEquals(fcmTokenList.get(0).getUserId(), userId);
        Assertions.assertTrue(fcmTokenList.get(0).getExpires().isAfter(before));
        Assertions.assertTrue(fcmTokenList.get(0).getExpires().isBefore(after));
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

        var expires = convertToInstant(objectMapper.readValue(result, FcmTokenExpiresDto.class).getExpires());

        var result2 = mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/notification/fcm/token")
                        .header("Authorization", "Bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new FcmTokenDto(fcmToken))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var expires2 = convertToInstant(objectMapper.readValue(result2, FcmTokenExpiresDto.class).getExpires());

        //TODO
        Assertions.assertEquals(
                DateUtils.truncate(Date.from(expires), Calendar.SECOND),
                DateUtils.truncate(Date.from(expires2), Calendar.SECOND)
        );

        var fcmTokenList = fcmTokenRepository.findByUserId(userId);
        Assertions.assertEquals(fcmTokenList.size(), 1);
    }

    private UserAuthInfoDto getUserAuthInfo(String accessToken) throws Exception {
        var result = performGetInfo(accessToken).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(result, UserAuthInfoDto.class);
    }

    private Instant convertToInstant(String source) {
        return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(source));
    }
}
