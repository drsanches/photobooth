package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.exception.GoogleAuthException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

class GoogleUserInfoServiceTest {

    private final String URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s";

    private final GoogleUserInfoService googleUserInfoService = new GoogleUserInfoService();

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(googleUserInfoService, "restTemplate", restTemplate);
    }

    @Test
    void getGoogleInfo() {
        var idToken = "idToken";
        var googleInfoDto = new GoogleInfoDto(
                UUID.randomUUID().toString(),
                null,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                null,
                null,
                null
        );
        Mockito.when(restTemplate.getForObject(String.format(URL, idToken), GoogleInfoDto.class))
                .thenReturn(googleInfoDto);

        var result = googleUserInfoService.getGoogleInfo(idToken);
        Assertions.assertEquals(googleInfoDto, result);
    }

    @Test
    void getGoogleInfoWithIncorrectToken() {
        var wrongIdToken = "wrongIdToken";
        Mockito.when(restTemplate.getForObject(String.format(URL, wrongIdToken), GoogleInfoDto.class))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(GoogleAuthException.class, () -> googleUserInfoService.getGoogleInfo(wrongIdToken));
    }
}
