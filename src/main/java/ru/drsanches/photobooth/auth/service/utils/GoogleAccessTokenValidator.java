package ru.drsanches.photobooth.auth.service.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.drsanches.photobooth.exception.auth.GoogleAuthException;

@Component
public class GoogleAccessTokenValidator {

    //TODO: Fix injection?
    private final static String URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=%s";

    public String getEmail(String token) {
        try {
            ResponseDTO response = new RestTemplate().getForObject(String.format(URL, token), ResponseDTO.class);
            return response == null ? null : response.getEmail();
        } catch (Exception e) {
            throw new GoogleAuthException(e);
        }
    }

    @Getter
    static class ResponseDTO {

        private String email;
    }
}
