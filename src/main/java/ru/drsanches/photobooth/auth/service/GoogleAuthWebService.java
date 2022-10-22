package ru.drsanches.photobooth.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.auth.data.dto.GoogleAccessTokenDTO;
import ru.drsanches.photobooth.auth.data.dto.TokenDTO;
import ru.drsanches.photobooth.auth.data.model.UserAuth;
import ru.drsanches.photobooth.auth.service.utils.GoogleAccessTokenValidator;
import ru.drsanches.photobooth.common.integration.UserIntegrationService;
import ru.drsanches.photobooth.common.token.TokenService;
import ru.drsanches.photobooth.common.token.data.Role;
import ru.drsanches.photobooth.common.token.data.Token;
import ru.drsanches.photobooth.common.token.data.TokenMapper;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@Service
@Validated
public class GoogleAuthWebService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Autowired
    private UserIntegrationService userIntegrationService;

    @Autowired
    private GoogleAccessTokenValidator googleAccessTokenValidator;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenMapper tokenMapper;

    public TokenDTO registration(@Valid GoogleAccessTokenDTO googleAccessTokenDTO) {
        String email = googleAccessTokenValidator.getEmail(googleAccessTokenDTO.getAccessToken());
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(UUID.randomUUID().toString());
        userAuth.setEmail(email);
        userAuth.setGoogleAuth(email);
        userAuth.setEnabled(true);
        userAuth.setRole(Role.USER);
        userIntegrationService.createUser(userAuth);
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        log.info("New user with id '{}' has been created", userAuth.getId());
        return tokenMapper.convert(token);
    }

    public TokenDTO login(@Valid GoogleAccessTokenDTO googleAccessTokenDTO) {
        UserAuth userAuth;
        String email = googleAccessTokenValidator.getEmail(googleAccessTokenDTO.getAccessToken());
        userAuth = userAuthDomainService.getEnabledByGoogleAuth(email);
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return tokenMapper.convert(token);
    }
}
