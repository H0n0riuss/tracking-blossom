package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

class BlossomFilterImpl implements ITrackingFilter {
    private final String sessionIdHeaderName = "session_id";

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
