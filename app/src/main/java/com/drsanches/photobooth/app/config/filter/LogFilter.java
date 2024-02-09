package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.auth.config.AuthInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
public class LogFilter extends GenericFilterBean {

    private final AuthInfo authInfo;

    private final Predicate<String> excludeLogUri;

    public LogFilter(AuthInfo authInfo, Predicate<String> excludeLogUri) {
        this.authInfo = authInfo;
        this.excludeLogUri = excludeLogUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        ThreadContext.put("requestId", UUID.randomUUID().toString());
        if (!excludeLogUri.test(httpRequest.getRequestURI())) {
            authInfo.getUserIdOptional().ifPresentOrElse(
                    it -> ThreadContext.put("userId", it),
                    () -> ThreadContext.remove("userId")
            );
            log.trace("{} {}, ip: {}", httpRequest.getMethod(), httpRequest.getRequestURL(), httpRequest.getRemoteAddr());
        }
        chain.doFilter(request, response);
    }
}
