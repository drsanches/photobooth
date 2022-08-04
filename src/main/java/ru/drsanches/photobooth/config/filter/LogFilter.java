package ru.drsanches.photobooth.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.photobooth.common.token.TokenSupplier;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.function.Predicate;

public class LogFilter extends GenericFilterBean {

    private final static Logger LOG = LoggerFactory.getLogger(LogFilter.class);

    private final static String MESSAGE_PATTERN = "URL: {}, Address: {}, UserId: {}";

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
                LOG.info(MESSAGE_PATTERN, httpRequest.getRequestURL(), httpRequest.getRemoteAddr(), TOKEN_SUPPLIER.get().getUserId());
            } else {
                LOG.info(MESSAGE_PATTERN, httpRequest.getRequestURL(), httpRequest.getRemoteAddr(), "unauthorized");
            }
        }
        chain.doFilter(request, response);
    }
}