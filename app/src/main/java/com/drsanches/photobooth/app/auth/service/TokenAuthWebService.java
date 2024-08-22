package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.auth.dto.userauth.request.GetTokenDto;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import com.drsanches.photobooth.app.auth.exception.WrongUsernameAuthException;
import com.drsanches.photobooth.app.auth.mapper.TokenMapper;
import com.drsanches.photobooth.app.auth.utils.CredentialsHelper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class TokenAuthWebService {

    @Autowired
    private UserAuthDomainService userAuthDomainService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CredentialsHelper credentialsHelper;
    @Autowired
    private TokenMapper tokenMapper;

    public TokenDto getToken(@Valid GetTokenDto getTokenDto) {
        var userAuth = userAuthDomainService.findEnabledByUsername(getTokenDto.getUsername().toLowerCase())
                .orElseThrow(WrongUsernameAuthException::new);
        credentialsHelper.checkPassword(getTokenDto.getPassword(), userAuth.getPassword(), userAuth.getSalt());
        var token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return tokenMapper.convert(token);
    }

    public TokenDto refreshToken(String refreshToken) {
        return tokenMapper.convert(tokenService.refreshToken(refreshToken));
    }

    public void removeToken() {
        tokenService.removeCurrentToken();
    }
}
