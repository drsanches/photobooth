package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.common.token.TokenSupplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
public class LogFilter extends GenericFilterBean {

    private final TokenSupplier tokenSupplier;

    private final Predicate<String> excludeLogUri;

    public LogFilter(TokenSupplier tokenSupplier, Predicate<String> excludeLogUri) {
        this.tokenSupplier = tokenSupplier;
        this.excludeLogUri = excludeLogUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ThreadContext.put("requestId", UUID.randomUUID().toString());
        if (!excludeLogUri.test(httpRequest.getRequestURI())) {
            if (tokenSupplier.get() != null) {
                ThreadContext.put("userId", tokenSupplier.get().getUserId());
            }
            log.trace("{} {}, ip: {}", httpRequest.getMethod(), httpRequest.getRequestURL(), httpRequest.getRemoteAddr());
        }
        chain.doFilter(request, response);
    }
}
