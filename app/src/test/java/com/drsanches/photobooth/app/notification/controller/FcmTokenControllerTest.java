package com.drsanches.photobooth.app.notification.controller;

import com.drsanches.photobooth.app.BaseSpringTest;
import com.drsanches.photobooth.app.auth.dto.userauth.response.UserAuthInfoDto;
import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.notifier.data.fcm.repository.FcmTokenRepository;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenDto;
import com.drsanches.photobooth.app.notifier.dto.FcmTokenExpiresDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    private UserAuthInfoDto getUserAuthInfo(String accessToken) throws Exception {
        var result = performGetInfo(accessToken).andReturn().getResponse().getContentAsString();
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
