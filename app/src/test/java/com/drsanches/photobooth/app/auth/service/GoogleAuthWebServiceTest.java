package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.dto.google.GoogleGetTokenDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.utils.ConfirmationCodeValidator;
import com.drsanches.photobooth.app.auth.utils.email.EmailNotifier;
import com.drsanches.photobooth.app.auth.exception.NoGoogleUserException;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
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

    private static final String ID_TOKEN = "idToken";
    private static final String USER_ID = "userId";
    private static final String USER_EMAIL = "userEmail";
    private static final String CONFIRMATION_ID = "confirmationId";
    private static final String CONFIRMATION_CODE = "confirmationCode";
    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();

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
        return UserAuth.builder()
                .id(USER_ID)
                .username(UUID.randomUUID().toString())
                .email(USER_EMAIL)
                .role(Role.USER)
                .build();
    }

    private Token createToken() {
        return Token.builder()
                .accessToken(ACCESS_TOKEN)
                .userId(USER_ID)
                .build();
    }

    private TokenDto createTokenDto() {
        return TokenDto.builder()
                .accessToken(ACCESS_TOKEN)
                .build();
    }

    private Confirmation createConfirmation() {
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(GregorianCalendar.MINUTE, 5);
        return Confirmation.builder()
                .id(CONFIRMATION_ID)
                .code(CONFIRMATION_CODE)
                .userId(USER_ID)
                .email(USER_EMAIL)
                .expiresAt(expiresAt)
                .operation(Operation.GOOGLE_USERNAME_CHANGE)
                .build();
    }
}