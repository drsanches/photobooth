package ru.drsanches.photobooth.auth.service.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.drsanches.photobooth.auth.data.google.dto.GoogleInfoDTO;
import ru.drsanches.photobooth.exception.auth.GoogleAuthException;

@Slf4j
@Component
public class GoogleUserInfoService {

    private final static String URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s";

    public GoogleInfoDTO getGoogleInfo(String idToken) {
        try {
            GoogleInfoDTO response = new RestTemplate().getForObject(String.format(URL, idToken), GoogleInfoDTO.class);
            log.info("Google user info response: {}", response);
            return response;
        } catch (Exception e) {
            throw new GoogleAuthException(e);
        }
    }
}
