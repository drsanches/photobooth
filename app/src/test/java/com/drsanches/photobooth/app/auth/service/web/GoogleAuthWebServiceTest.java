package com.drsanches.photobooth.app.auth.service.web;

import com.drsanches.photobooth.app.auth.data.common.dto.request.GoogleTokenDTO;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDTO;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.google.dto.GoogleInfoDTO;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.service.domain.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.service.integration.GoogleUserInfoService;
import com.drsanches.photobooth.app.auth.service.utils.email.EmailNotifier;
import com.drsanches.photobooth.app.common.exception.auth.NoGoogleUserException;
import com.drsanches.photobooth.app.common.service.UserIntegrationService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.Role;
import com.drsanches.photobooth.app.common.token.data.Token;
import com.drsanches.photobooth.app.common.token.data.TokenMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GoogleAuthWebServiceTest {

    private final static String ID_TOKEN = "idToken";
    private final static String USER_ID = "userId";
    private final static String USER_EMAIL = "userEmail";
    private final static String ACCESS_TOKEN = UUID.randomUUID().toString();

    @Mock
    private GoogleUserInfoService googleUserInfoService;

    @Mock
    private UserAuthDomainService userAuthDomainService;

    @Mock
    private UserIntegrationService userIntegrationService;

    @Mock
    private EmailNotifier emailNotifier;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private GoogleAuthWebService googleAuthWebService;

    @BeforeEach
    void setUp() {
        GoogleInfoDTO googleInfo = Mockito.mock(GoogleInfoDTO.class);
        Mockito.when(googleInfo.getEmail()).thenReturn(USER_EMAIL);
        Mockito.when(googleUserInfoService.getGoogleInfo(Mockito.any())).thenReturn(googleInfo);
    }

    @Test
    void getTokenForExists() {
        UserAuth userAuth = createUserAuth();
        Token token = createToken();
        TokenDTO tokenDTO = createTokenDTO();
        Mockito.when(userAuthDomainService.getEnabledByGoogleAuth(USER_EMAIL)).thenReturn(userAuth);
        Mockito.when(tokenService.createToken(USER_ID, Role.USER)).thenReturn(token);
        Mockito.when(tokenMapper.convert(token)).thenReturn(tokenDTO);

        TokenDTO result = googleAuthWebService.getToken(new GoogleTokenDTO(ID_TOKEN));

        Assertions.assertEquals(tokenDTO, result);
    }

    @Test
    void getTokenForNonExists() {
        UserAuth userAuth = createUserAuth();
        Token token = createToken();
        TokenDTO tokenDTO = createTokenDTO();
        Mockito.when(userAuthDomainService.getEnabledByGoogleAuth(USER_EMAIL)).thenThrow(NoGoogleUserException.class);
        Mockito.when(userIntegrationService.createUserByGoogle(USER_EMAIL)).thenReturn(userAuth);
        Mockito.when(tokenService.createToken(USER_ID, Role.USER)).thenReturn(token);
        Mockito.when(tokenMapper.convert(token)).thenReturn(tokenDTO);

        TokenDTO result = googleAuthWebService.getToken(new GoogleTokenDTO(ID_TOKEN));

        Mockito.verify(emailNotifier, Mockito.times(1)).sendSuccessNotification(USER_EMAIL, Operation.REGISTRATION);
        Assertions.assertEquals(tokenDTO, result);
    }

    private UserAuth createUserAuth() {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(USER_ID);
        userAuth.setEmail(USER_EMAIL);
        userAuth.setRole(Role.USER);
        return userAuth;
    }

    private Token createToken() {
        Token token = new Token();
        token.setAccessToken(ACCESS_TOKEN);
        return token;
    }

    private TokenDTO createTokenDTO() {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(ACCESS_TOKEN);
        return tokenDTO;
    }
}
