package com.drsanches.photobooth.app.auth.service.integration;

import com.drsanches.photobooth.app.auth.data.google.dto.GoogleInfoDTO;
import com.drsanches.photobooth.app.common.exception.auth.GoogleAuthException;
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
        String idToken = "idToken";
        GoogleInfoDTO googleInfoDTO = new GoogleInfoDTO();
        googleInfoDTO.setEmail(UUID.randomUUID().toString());
        Mockito.when(restTemplate.getForObject(String.format(URL, idToken), GoogleInfoDTO.class))
                .thenReturn(googleInfoDTO);

        GoogleInfoDTO result = googleUserInfoService.getGoogleInfo(idToken);
        Assertions.assertEquals(googleInfoDTO, result);
    }

    @Test
    void getGoogleInfoWithIncorrectToken() {
        String wrongIdToken = "wrongIdToken";
        Mockito.when(restTemplate.getForObject(String.format(URL, wrongIdToken), GoogleInfoDTO.class))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(GoogleAuthException.class, () -> googleUserInfoService.getGoogleInfo(wrongIdToken));
    }
}
