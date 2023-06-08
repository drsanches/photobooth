package com.drsanches.photobooth.app.auth.service.web;

import com.drsanches.photobooth.app.auth.data.common.dto.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDto;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.data.google.dto.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.data.google.dto.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.data.google.dto.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.service.domain.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.service.domain.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.service.integration.GoogleUserInfoService;
import com.drsanches.photobooth.app.auth.service.utils.ConfirmationCodeValidator;
import com.drsanches.photobooth.app.auth.service.utils.email.EmailNotifier;
import com.drsanches.photobooth.app.common.exception.auth.NoGoogleUserException;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.common.token.data.Role;
import com.drsanches.photobooth.app.common.token.data.Token;
import com.drsanches.photobooth.app.common.token.data.TokenMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.GregorianCalendar;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GoogleAuthWebServiceTest {

    private final static String ID_TOKEN = "idToken";
    private final static String USER_ID = "userId";
    private final static String USER_EMAIL = "userEmail";
    private final static String CONFIRMATION_ID = "confirmationId";
    private final static String CONFIRMATION_CODE = "confirmationCode";
    private final static String ACCESS_TOKEN = UUID.randomUUID().toString();

    @Mock
    private GoogleUserInfoService googleUserInfoService;

    @Mock
    private UserAuthDomainService userAuthDomainService;

    @Mock
    private ConfirmationDomainService confirmationDomainService;

    @Mock
    private UserIntegrationDomainService userIntegrationDomainService;

    @Mock
    private ConfirmationCodeValidator confirmationCodeValidator;

    @Mock
    private EmailNotifier emailNotifier;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenSupplier tokenSupplier;

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private GoogleAuthWebService googleAuthWebService;

    @Test
    void getTokenForExisting() {
        GoogleInfoDto googleInfo = new GoogleInfoDto();
        googleInfo.setEmail(USER_EMAIL);
        UserAuth userAuth = createUserAuth();
        Token token = createToken();
        TokenDto tokenDto = createTokenDto();
        Mockito.when(googleUserInfoService.getGoogleInfo(Mockito.any())).thenReturn(googleInfo);
        Mockito.when(userAuthDomainService.getEnabledByGoogleAuth(USER_EMAIL)).thenReturn(userAuth);
        Mockito.when(tokenService.createToken(USER_ID, Role.USER)).thenReturn(token);
        Mockito.when(tokenMapper.convert(token)).thenReturn(tokenDto);

        GoogleGetTokenDto result = googleAuthWebService.getToken(new GoogleTokenDto(ID_TOKEN));

        Assertions.assertEquals(tokenDto, result.getToken());
        Assertions.assertNull(result.getChangeUsernameCode());
    }

    @Test
    void getTokenForNonExisting() {
        GoogleInfoDto googleInfo = new GoogleInfoDto();
        googleInfo.setEmail(USER_EMAIL);
        UserAuth userAuth = createUserAuth();
        Token token = createToken();
        TokenDto tokenDto = createTokenDto();
        Mockito.when(googleUserInfoService.getGoogleInfo(Mockito.any())).thenReturn(googleInfo);
        Mockito.when(userAuthDomainService.getEnabledByGoogleAuth(USER_EMAIL)).thenThrow(NoGoogleUserException.class);
        Mockito.when(userIntegrationDomainService.createUserByGoogle(USER_EMAIL)).thenReturn(userAuth);
        Mockito.when(confirmationDomainService.create(null, USER_ID, USER_EMAIL, Operation.GOOGLE_USERNAME_CHANGE)).thenReturn(createConfirmation());
        Mockito.when(tokenService.createToken(USER_ID, Role.USER)).thenReturn(token);
        Mockito.when(tokenMapper.convert(token)).thenReturn(tokenDto);

        GoogleGetTokenDto result = googleAuthWebService.getToken(new GoogleTokenDto(ID_TOKEN));

        Mockito.verify(emailNotifier, Mockito.times(1)).sendSuccessNotification(USER_EMAIL, Operation.REGISTRATION);
        Assertions.assertEquals(tokenDto, result.getToken());
        Assertions.assertEquals(CONFIRMATION_CODE, result.getChangeUsernameCode());
    }

    @Test
    void setUsername() {
        UserAuth userAuth = createUserAuth();
        Confirmation confirmation = createConfirmation();
        Mockito.when(confirmationDomainService.get(CONFIRMATION_CODE)).thenReturn(confirmation);
        Mockito.when(tokenSupplier.get()).thenReturn(createToken());
        Mockito.when(userAuthDomainService.getEnabledById(USER_ID)).thenReturn(userAuth);

        String newUsername = UUID.randomUUID().toString();
        GoogleSetUsernameDto request = new GoogleSetUsernameDto();
        request.setNewUsername(newUsername);
        request.setCode(CONFIRMATION_CODE);
        googleAuthWebService.setUsername(request);

        userAuth.setUsername(newUsername);
        Mockito.verify(confirmationCodeValidator).validate(confirmation, Operation.GOOGLE_USERNAME_CHANGE);
        Mockito.verify(userIntegrationDomainService).updateUser(userAuth);
        Mockito.verify(confirmationDomainService).delete(confirmation.getId());
        Mockito.verify(tokenService).removeAllTokens(USER_ID);
    }

    private UserAuth createUserAuth() {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(USER_ID);
        userAuth.setUsername(UUID.randomUUID().toString());
        userAuth.setEmail(USER_EMAIL);
        userAuth.setRole(Role.USER);
        return userAuth;
    }

    private Token createToken() {
        Token token = new Token();
        token.setAccessToken(ACCESS_TOKEN);
        token.setUserId(USER_ID);
        return token;
    }

    private TokenDto createTokenDto() {
        TokenDto tokenDto = new TokenDto();
        tokenDto.setAccessToken(ACCESS_TOKEN);
        return tokenDto;
    }

    private Confirmation createConfirmation() {
        Confirmation confirmation = new Confirmation();
        confirmation.setId(CONFIRMATION_ID);
        confirmation.setCode(CONFIRMATION_CODE);
        confirmation.setUserId(USER_ID);
        confirmation.setEmail(USER_EMAIL);
        GregorianCalendar expiresAt = new GregorianCalendar();
            expiresAt.add(GregorianCalendar.MINUTE, 5);
        confirmation.setExpiresAt(expiresAt);
        confirmation.setOperation(Operation.GOOGLE_USERNAME_CHANGE);
        return confirmation;
    }
}
