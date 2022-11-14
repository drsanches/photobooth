package ru.drsanches.photobooth.auth.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.auth.data.common.dto.request.GoogleAccessTokenDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.TokenDTO;
import ru.drsanches.photobooth.auth.data.userauth.model.UserAuth;
import ru.drsanches.photobooth.auth.service.domain.UserAuthDomainService;
import ru.drsanches.photobooth.auth.service.utils.GoogleAccessTokenValidator;
import ru.drsanches.photobooth.common.integration.UserIntegrationService;
import ru.drsanches.photobooth.common.token.TokenService;
import ru.drsanches.photobooth.common.token.data.Token;
import ru.drsanches.photobooth.common.token.data.TokenMapper;

import javax.validation.Valid;

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
        UserAuth userAuth = userIntegrationService.createUserByGoogle(email);
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
