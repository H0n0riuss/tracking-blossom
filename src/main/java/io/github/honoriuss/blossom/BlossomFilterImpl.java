package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingFilter;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

class BlossomFilterImpl implements ITrackingFilter, ITrackingParameterProvider {
    private final String sessionIdHeaderName; //TODO add in config
    private final String timestampName; //TODO add in config

    public BlossomFilterImpl() {
        this("session_id", "timestamp");
    }

    public BlossomFilterImpl(String sessionIdHeaderName, String timestampName) {
        this.sessionIdHeaderName = sessionIdHeaderName;
        this.timestampName = timestampName;
    }

    @Override
    public HashMap<String, Object> getBaseParameters() {
        var map = new HashMap<String, Object>();
        var request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        var sessionId = getOrCreateSessionId(request);
        map.put(sessionIdHeaderName, sessionId);
        map.put(timestampName, LocalDateTime.now().toString());
        return map;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest httpServletRequest) {
            var sessionId = getOrCreateSessionId(httpServletRequest);
            servletRequest.setAttribute(sessionIdHeaderName, sessionId);
            addSessionIdToResponse(servletResponse, sessionId);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getOrCreateSessionId(HttpServletRequest httpServletRequest) {
        return Optional
                .ofNullable(httpServletRequest.getHeader(sessionIdHeaderName))
                .orElse(UUID.randomUUID().toString());
    }

    private void addSessionIdToResponse(ServletResponse servletResponse, String optSessionId) {
        if (servletResponse instanceof HttpServletResponse httpServletResponse
                && httpServletResponse.getHeader(sessionIdHeaderName) == null) {
            httpServletResponse.addHeader(sessionIdHeaderName, optSessionId);
        }
    }
}
