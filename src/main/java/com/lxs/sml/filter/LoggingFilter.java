package com.lxs.sml.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 支持<b>Servlet Stack</b>下<b>Servlet</b>容器的日志记录。
 *
 * @author liuxinsi
 */
public class LoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String REQ_ATTR = "reqId";
    private AtomicLong id = new AtomicLong(1);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//         if (logger.isDebugEnabled()) {
            byte[] payload = logRequest(request);
            if (payload != null) {
                request = new HttpServletRequestAdapter(request, payload);
            }
            response = new HttpServletResponseAdapter(response);
//         }

        filterChain.doFilter(request, response);

//         if (logger.isDebugEnabled()) {
            logResponse(request, response);
//         }
    }

    private byte[] logRequest(HttpServletRequest request) {
        long requestId = id.incrementAndGet();
        request.setAttribute(REQ_ATTR, requestId);

        Enumeration<String> heads = request.getHeaderNames();
        Map<String, String> m = new HashMap<>(16);
        while (heads.hasMoreElements()) {
            String h = heads.nextElement();
            m.put(h, request.getHeader(h));
        }

        LoggingFormat lf = new LoggingFormat();
        lf.setId(String.valueOf(requestId))
                .setReqUrl(request.getRequestURL().toString() +
                        (request.getQueryString() == null || "".equals(request.getQueryString()) ? "" : "?" + request.getQueryString()))
                .setMethod(request.getMethod())
                .setHeaders(m);

        byte[] _payload = null;
        if (!isMultipart(request) && !isBinaryContent(request)) {
            try {
                String charEncoding = request.getCharacterEncoding() != null
                        ? request.getCharacterEncoding() :
                        "UTF-8";
                _payload = IOUtils.toByteArray(request.getInputStream());
                lf.setPayload(new String(_payload, charEncoding));
            } catch (UnsupportedEncodingException e) {
                logger.warn("UnsupportedEncodingException", e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                lf.setPayload("read payload got excetion：" + e.getMessage());
            }
        }
        logger.debug(lf.reqFormat());
        return _payload;
    }

    private boolean isBinaryContent(final HttpServletRequest request) {
        return request.getContentType() != null && (request.getContentType().startsWith("image") || request.getContentType().startsWith("video") || request.getContentType().startsWith("audio"));
    }

    private boolean isMultipart(final HttpServletRequest request) {
        return request.getContentType() != null && (request.getContentType().startsWith("multipart/form-data") || request.getContentType().startsWith("application/octet-stream"));
    }

    private boolean isBinaryContent(final HttpServletResponse response) {
        return response.getContentType() != null && (response.getContentType().startsWith("image") || response.getContentType().startsWith("video") || response.getContentType().startsWith("audio"));
    }

    private boolean isMultipart(final HttpServletResponse response) {
        return response.getContentType() != null && (response.getContentType().startsWith("multipart/form-data") || response.getContentType().startsWith("application/octet-stream"));
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> m = new HashMap<>(16);
        response.getHeaderNames().forEach(s -> m.put(s, response.getHeader(s)));
        LoggingFormat lf = new LoggingFormat();
        lf.setId(request.getAttribute(REQ_ATTR).toString())
                .setStatus(response.getStatus())
                .setHeaders(m);

        if (!isMultipart(response) && !isBinaryContent(response)) {
            try {
                String charEncoding = response.getCharacterEncoding() != null ? response.getCharacterEncoding() :
                        "UTF-8";

                byte[] _payload = null;
                if (response instanceof HttpServletResponseAdapter) {
                    _payload = ((HttpServletResponseAdapter) response).toByteArray();
                }

                lf.setPayload(_payload == null ? "" : new String(_payload, charEncoding));
            } catch (UnsupportedEncodingException e) {
                logger.warn("UnsupportedEncodingException", e);
            }
        }
        logger.debug(lf.respFormat());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (LoggingFormat.getIgnoreUrls().isEmpty()) {
            return super.shouldNotFilter(request);
        }

        String path = request.getServletPath();
        return LoggingFormat.getIgnoreUrls().stream().anyMatch(url -> url.equals(path) || path.matches(url));
    }
}
