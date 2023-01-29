package com.drsanches.photobooth.app.config.filter;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.common.token.TokenSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.function.Predicate;

@Slf4j
public class LogFilter extends GenericFilterBean {

    private final TokenSupplier TOKEN_SUPPLIER;

    private final Predicate<String> LOG_URI;

    public LogFilter(TokenSupplier tokenSupplier, Predicate<String> logUri) {
        this.TOKEN_SUPPLIER = tokenSupplier;
        this.LOG_URI = logUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (LOG_URI.test(httpRequest.getRequestURI())) {
            if (TOKEN_SUPPLIER.get() != null) {
                log.info("{} {}, info: {}", httpRequest.getMethod(),  httpRequest.getRequestURL(), LogInfo.builder()
                        .method(httpRequest.getMethod())
                        .url(httpRequest.getRequestURL())
                        .address(httpRequest.getRemoteAddr())
                        .userId(TOKEN_SUPPLIER.get().getUserId())
                        .build());
            } else {
                log.info("{} {}, info: {}", httpRequest.getMethod(), httpRequest.getRequestURL(), LogInfo.builder()
                        .method(httpRequest.getMethod())
                        .url(httpRequest.getRequestURL())
                        .address(httpRequest.getRemoteAddr())
                        .build());
            }
        }
        chain.doFilter(request, response);
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    private static class LogInfo {

        private static final ObjectMapper MAPPER = new ObjectMapper();

        private final String timestamp = GregorianCalendarConvertor.convert(new GregorianCalendar());

        private final String method;

        private final StringBuffer url;

        private final String address;

        private final String userId;

        @SneakyThrows
        @Override
        public String toString() {
            return MAPPER.writeValueAsString(this);
        }
    }
}
