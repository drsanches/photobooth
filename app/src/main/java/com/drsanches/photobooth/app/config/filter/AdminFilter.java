package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.common.token.TokenSupplier;
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

    private final TokenSupplier tokenSupplier;

    private final Predicate<String> adminUri;

    public AdminFilter(TokenSupplier tokenSupplier, Predicate<String> adminUri) {
        this.tokenSupplier = tokenSupplier;
        this.adminUri = adminUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        if (adminUri.test(uri)
                && (tokenSupplier.get() == null || !Role.ADMIN.equals(tokenSupplier.get().getRole()))) {
            log.info("User has no permissions for uri. UserId: {}, uri: {}",
                    tokenSupplier.get() == null ? "unauthorized" : tokenSupplier.get().getUserId(), uri);
            httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().println("You do not have permission");
            return;
        }
        chain.doFilter(request, response);
    }
}
