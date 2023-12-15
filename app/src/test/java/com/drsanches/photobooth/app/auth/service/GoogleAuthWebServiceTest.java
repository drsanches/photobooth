package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.dto.userauth.request.GoogleTokenDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Confirmation;
import com.drsanches.photobooth.app.auth.data.confirmation.model.Operation;
import com.drsanches.photobooth.app.auth.dto.google.GoogleInfoDto;
import com.drsanches.photobooth.app.auth.dto.google.GoogleSetUsernameDto;
import com.drsanches.photobooth.app.auth.data.userauth.model.UserAuth;
import com.drsanches.photobooth.app.auth.data.confirmation.ConfirmationDomainService;
import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.utils.ConfirmationValidator;
import com.drsanches.photobooth.app.common.token.UserInfo;
import com.drsanches.photobooth.app.notifier.service.notifier.Action;
import com.drsanches.photobooth.app.auth.exception.NoGoogleUserException;
import com.drsanches.photobooth.app.common.service.UserIntegrationDomainService;
import com.drsanches.photobooth.app.common.token.TokenService;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
import com.drsanches.photobooth.app.notifier.service.notifier.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.GregorianCalendar;
import java.util.Map;
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
    private ConfirmationValidator confirmationValidator;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserInfo userInfo;

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private GoogleAuthWebService googleAuthWebService;

    @Test
    void getTokenForExisting() {
        var googleInfo = new GoogleInfoDto();
        googleInfo.setEmail(USER_EMAIL);
        var userAuth = createUserAuth();
        var token = createToken();
        var tokenDto = createTokenDto();
        Mockito.when(googleUserInfoService.getGoogleInfo(Mockito.any())).thenReturn(googleInfo);
        Mockito.when(userAuthDomainService.getEnabledByGoogleAuth(USER_EMAIL)).thenReturn(userAuth);
        Mockito.when(tokenService.createToken(USER_ID, Role.USER)).thenReturn(token);
        Mockito.when(tokenMapper.convert(token)).thenReturn(tokenDto);

        var result = googleAuthWebService.getToken(new GoogleTokenDto(ID_TOKEN));

        Assertions.assertEquals(tokenDto, result.getToken());
        Assertions.assertNull(result.getChangeUsernameCode());
    }

    @Test
    void getTokenForNonExisting() {
        var googleInfo = new GoogleInfoDto();
        googleInfo.setEmail(USER_EMAIL);
        var userAuth = createUserAuth();
        var token = createToken();
        var tokenDto = createTokenDto();
        Mockito.when(googleUserInfoService.getGoogleInfo(Mockito.any())).thenReturn(googleInfo);
        Mockito.when(userAuthDomainService.getEnabledByGoogleAuth(USER_EMAIL)).thenThrow(NoGoogleUserException.class);
        Mockito.when(userIntegrationDomainService.createUserByGoogle(USER_EMAIL)).thenReturn(userAuth);
        Mockito.when(confirmationDomainService.create(null, USER_ID, Operation.GOOGLE_USERNAME_CHANGE))
                .thenReturn(createConfirmation());
        Mockito.when(tokenService.createToken(USER_ID, Role.USER)).thenReturn(token);
        Mockito.when(tokenMapper.convert(token)).thenReturn(tokenDto);

        var result = googleAuthWebService.getToken(new GoogleTokenDto(ID_TOKEN));

        Mockito.verify(notificationService, Mockito.times(1)).notify(
                Action.REGISTRATION_COMPLETED,
                Map.of("email", USER_EMAIL)
        );
        Assertions.assertEquals(tokenDto, result.getToken());
        Assertions.assertEquals(CONFIRMATION_CODE, result.getChangeUsernameCode());
    }

    @Test
    void setUsername() {
        var userAuth = createUserAuth();
        var confirmation = createConfirmation();
        Mockito.when(confirmationDomainService.get(CONFIRMATION_CODE)).thenReturn(confirmation);
        Mockito.when(userInfo.getUserId()).thenReturn(USER_ID);
        Mockito.when(userAuthDomainService.getEnabledById(USER_ID)).thenReturn(userAuth);

        var newUsername = UUID.randomUUID().toString();
        var request = new GoogleSetUsernameDto();
        request.setNewUsername(newUsername);
        request.setCode(CONFIRMATION_CODE);
        googleAuthWebService.setUsername(request);

        Mockito.verify(confirmationValidator).validate(confirmation, Operation.GOOGLE_USERNAME_CHANGE);
        Mockito.verify(userIntegrationDomainService).updateUsername(USER_ID, newUsername);
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
        var expiresAt = new GregorianCalendar();
        expiresAt.add(GregorianCalendar.MINUTE, 5);
        return Confirmation.builder()
                .id(CONFIRMATION_ID)
                .code(CONFIRMATION_CODE)
                .userId(USER_ID)
                .expiresAt(expiresAt)
                .operation(Operation.GOOGLE_USERNAME_CHANGE)
                .build();
    }
}
