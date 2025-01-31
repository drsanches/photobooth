package com.drsanches.photobooth.app.common.auth;

import com.drsanches.photobooth.app.auth.exception.WrongTokenAuthException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Component
@RequestScope //TODO: Remove @RequestScope?
public class AuthInfo {

    public void setAuthorization(String userId, String username, String tokenId, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new TokenAuthentication(
                userId,
                username,
                tokenId,
                new String[] {role}
        );
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public void cancelAuthorization() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }
    }

    public String getUserTokenId() {
        return (String) getAuthentication().getCredentials();
    }

    public String getUserId() {
        return (String) getAuthentication().getPrincipal();
    }

    public Optional<String> getUserIdOptional() {
        return getAuthenticationOptional().map(it -> (String) it.getPrincipal());
    }

    public String getUsername() {
        return (String) getAuthentication().getDetails();
    }

    public boolean isAuthorized() {
        return getAuthenticationOptional().isPresent();
    }

    private Authentication getAuthentication() {
        return getAuthenticationOptional().orElseThrow(WrongTokenAuthException::new); //TODO: Use another exception?
    }

    private Optional<Authentication> getAuthenticationOptional() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof TokenAuthentication && authentication.isAuthenticated()) {
            return Optional.of(authentication);
        }
        return Optional.empty();
    }
}
