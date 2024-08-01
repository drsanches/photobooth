package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.auth.data.token.model.Role;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
@AllArgsConstructor
public class AdminFilter extends GenericFilterBean {

    private final AuthInfo authInfo;
    private final Predicate<String> excludeUri;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var uri = httpRequest.getRequestURI();
        if (!excludeUri.test(uri) && (!authInfo.isAuthorized() || !authInfo.getRole().equals(Role.ADMIN))) {
            log.info(
                    "User has no permissions for uri. UserId: {}, uri: {}",
                    authInfo.getUserIdOptional().orElse("unauthorized"),
                    uri
            );
            httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().println("You do not have permission");
            return;
        }
        chain.doFilter(request, response);
    }
}
