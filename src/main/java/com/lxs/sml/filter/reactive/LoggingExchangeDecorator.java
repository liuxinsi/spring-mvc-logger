package com.lxs.sml.filter.reactive;

import com.lxs.sml.filter.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author liuxinsi
 * @date 2018/9/12 17:00
 */
public class LoggingExchangeDecorator extends ServerWebExchangeDecorator {
    private Logger logger = LoggerFactory.getLogger(LoggingExchangeDecorator.class);
    private ServerHttpRequestDecorator requestDecorator;
    private ServerHttpResponseDecorator responseDecorator;
    private static LongAdder id = new LongAdder();

    public LoggingExchangeDecorator(ServerWebExchange delegate) {
        super(delegate);

        String path = getDelegate().getRequest().getPath().pathWithinApplication().value();
        boolean match = Utils.pathMatch(path);
        if (match && logger.isTraceEnabled()) {
            logger.trace("path:{} is ignore", path);
        }

        if (!match) {
            id.increment();
            this.requestDecorator = new LoggingRequestDecorator(delegate.getRequest(), id.longValue());
            this.responseDecorator = new LoggingResponseDecorator(delegate.getResponse(), id.longValue());
        } else {
            this.requestDecorator = new ServerHttpRequestDecorator(delegate.getRequest());
            this.responseDecorator = new ServerHttpResponseDecorator(delegate.getResponse());
        }
    }

    @Override
    public ServerHttpRequest getRequest() {
        return requestDecorator;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return responseDecorator;
    }
}
