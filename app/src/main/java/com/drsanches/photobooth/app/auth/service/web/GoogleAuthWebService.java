package com.drsanches.photobooth.app.auth.service.web;

import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.TokenMapper;
import com.drsanches.photobooth.app.auth.data.common.dto.request.GoogleTokenDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDTO;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.service.domain.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.service.integration.GoogleUserInfoService;
import com.drsanches.photobooth.app.auth.service.utils.email.EmailNotifier;
import com.drsanches.photobooth.app.common.exception.auth.NoGoogleUserException;
import com.drsanches.photobooth.app.common.service.UserIntegrationService;
import com.drsanches.photobooth.app.common.token.data.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
    private GoogleUserInfoService googleUserInfoService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailNotifier emailNotifier;

    @Autowired
    private TokenMapper tokenMapper;

    public TokenDTO getToken(@Valid GoogleTokenDTO googleTokenDTO) {
        String email = googleUserInfoService.getGoogleInfo(googleTokenDTO.getIdToken()).getEmail();
        UserAuth userAuth;
        try {
            userAuth = userAuthDomainService.getEnabledByGoogleAuth(email);
        } catch (NoGoogleUserException e) {
            userAuth = userIntegrationService.createUserByGoogle(email);
            log.info("New user created. Id: {}", userAuth.getId());
            emailNotifier.sendSuccessNotification(userAuth.getEmail(), Operation.REGISTRATION);
        }
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return tokenMapper.convert(token);
    }
}
