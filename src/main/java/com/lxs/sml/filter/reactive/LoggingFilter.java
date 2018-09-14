package com.lxs.sml.filter.reactive;

import org.springframework.web.server.WebFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 支持<b>Reactive Stack</b>下<b>Netty</b>容器的日志记录。
 *
 * @author liuxinsi
 * @date 2018/9/12 17:01
 */
public class LoggingFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        return webFilterChain.filter(new LoggingExchangeDecorator(serverWebExchange));
    }

}