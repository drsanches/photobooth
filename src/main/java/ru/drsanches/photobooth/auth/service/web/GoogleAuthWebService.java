package ru.drsanches.photobooth.auth.service.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.photobooth.auth.data.common.dto.request.GoogleTokenDTO;
import ru.drsanches.photobooth.auth.data.common.dto.response.TokenDTO;
import ru.drsanches.photobooth.auth.data.confirmation.model.Operation;
import ru.drsanches.photobooth.auth.data.userauth.model.UserAuth;
import ru.drsanches.photobooth.auth.service.domain.UserAuthDomainService;
import ru.drsanches.photobooth.auth.service.integration.GoogleUserInfoService;
import ru.drsanches.photobooth.auth.service.utils.email.EmailNotifier;
import ru.drsanches.photobooth.common.integration.UserIntegrationService;
import ru.drsanches.photobooth.common.token.TokenService;
import ru.drsanches.photobooth.common.token.data.Token;
import ru.drsanches.photobooth.common.token.data.TokenMapper;
import ru.drsanches.photobooth.exception.auth.NoGoogleUserException;

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
            log.info("New user with id '{}' has been created", userAuth.getId());
            emailNotifier.sendSuccessNotification(userAuth.getEmail(), Operation.REGISTRATION);
        }
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return tokenMapper.convert(token);
    }
}
