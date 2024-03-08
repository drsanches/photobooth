package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.common.auth.AuthInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
@AllArgsConstructor
public class LogFilter extends GenericFilterBean {

    private final AuthInfo authInfo;
    private final Predicate<String> excludeUri;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        ThreadContext.put("requestId", UUID.randomUUID().toString());
        if (!excludeUri.test(httpRequest.getRequestURI())) {
            var userId = authInfo.getUserIdOptional();
            if (userId.isPresent()) {
                ThreadContext.put("userId", userId.toString());
            } else {
                ThreadContext.remove("userId");
            }
            log.trace(
                    "{} {}, ip: {}, userId: {}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURL(),
                    httpRequest.getRemoteAddr(),
                    userId.orElse("unauthorized")
            );
        }
        chain.doFilter(request, response);
    }
}
