package com.drsanches.photobooth.app.common.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

@RequiredArgsConstructor
public class TokenAuthentication implements Authentication {

    private final String userId;
    private final String username;
    private final String tokenId;
    private final String[] authorities;
    private boolean authenticated = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(authorities);
    }

    @Override
    public Object getCredentials() {
        return tokenId;
    }

    @Override
    public Object getDetails() {
        return username;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return username;
    }
}
