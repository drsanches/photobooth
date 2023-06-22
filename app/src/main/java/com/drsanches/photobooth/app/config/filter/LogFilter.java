package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.common.token.TokenSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
        if (!excludeLogUri.test(httpRequest.getRequestURI())) {
            if (tokenSupplier.get() != null) {
                log.trace("{} {}, address: {}, userId: {}",
                        httpRequest.getMethod(),
                        httpRequest.getRequestURL(),
                        httpRequest.getRemoteAddr(),
                        tokenSupplier.get().getUserId()
                );
            } else {
                log.trace("{} {}, address: {}, userId: {}",
                        httpRequest.getMethod(),
                        httpRequest.getRequestURL(),
                        httpRequest.getRemoteAddr(),
                        null
                );
            }
        }
        chain.doFilter(request, response);
    }
}
