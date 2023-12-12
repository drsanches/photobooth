package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.exception.GoogleAuthException;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@MonitorTime
public class GoogleUserInfoService {

    private static final String URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s";

    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleInfoDto getGoogleInfo(String idToken) {
        try {
            GoogleInfoDto response = restTemplate.getForObject(String.format(URL, idToken), GoogleInfoDto.class);
            log.info("Google user info response: {}", response);
            return response;
        } catch (Exception e) {
            throw new GoogleAuthException(e);
        }
    }
}
