package com.drsanches.photobooth.app.auth.service.integration;

import com.drsanches.photobooth.app.auth.data.google.dto.GoogleInfoDTO;
import com.drsanches.photobooth.app.common.exception.auth.GoogleAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class GoogleUserInfoService {

    private final static String URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s";

    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleInfoDTO getGoogleInfo(String idToken) {
        try {
            GoogleInfoDTO response = restTemplate.getForObject(String.format(URL, idToken), GoogleInfoDTO.class);
            log.info("Google user info response: {}", response);
            return response;
        } catch (Exception e) {
            throw new GoogleAuthException(e);
        }
    }
}
