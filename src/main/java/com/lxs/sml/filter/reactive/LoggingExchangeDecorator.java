package com.lxs.sml.filter.reactive;

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
    private ServerHttpRequestDecorator requestDecorator;
    private ServerHttpResponseDecorator responseDecorator;
    private static LongAdder id = new LongAdder();

    public LoggingExchangeDecorator(ServerWebExchange delegate) {
        super(delegate);
        id.increment();
        this.requestDecorator = new LoggingRequestDecorator(delegate.getRequest(), id.longValue());
        this.responseDecorator = new LoggingResponseDecorator(delegate.getResponse(), id.longValue());
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
