package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.common.token.UserInfo;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;
import java.util.function.Predicate;

@Slf4j
public class AdminFilter extends GenericFilterBean {

    private final UserInfo userInfo;

    private final Predicate<String> adminUri;

    public AdminFilter(UserInfo userInfo, Predicate<String> adminUri) {
        this.userInfo = userInfo;
        this.adminUri = adminUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        if (adminUri.test(uri)
                && (!userInfo.isAuthorized() || !Role.ADMIN.equals(userInfo.getRole()))) {
            log.info("User has no permissions for uri. UserId: {}, uri: {}",
                    userInfo.getUserIdOptional().orElse("unauthorized"), uri);
            httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().println("You do not have permission");
            return;
        }
        chain.doFilter(request, response);
    }
}
