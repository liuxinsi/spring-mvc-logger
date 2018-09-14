package com.lxs.sml.filter.reactive;

import com.lxs.sml.filter.LoggingFormat;
import com.lxs.sml.filter.Utils;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static reactor.core.scheduler.Schedulers.single;

/**
 * @author liuxinsi
 * @date 2018/9/12 16:53
 */
public class LoggingRequestDecorator extends ServerHttpRequestDecorator {
    private Logger logger = LoggerFactory.getLogger(LoggingRequestDecorator.class);
    private DataBuffer bodyDataBuffer;
    private boolean readable = true;
    private byte[] bytes;
    private Long id;

    LoggingRequestDecorator(ServerHttpRequest delegate, Long id) {
        super(delegate);
        this.id = id;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        if (readable && logger.isDebugEnabled()) {
            readable = false;
            Flux<DataBuffer> flux = super.getBody();
            return flux
                    .publishOn(single())
                    .map(this::cache)
                    .doOnComplete(() -> loggin(getDelegate()));
        } else {
            return Flux.just(getBodyMore());
        }
    }

    private DataBuffer getBodyMore() {
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
        bodyDataBuffer = nettyDataBufferFactory.wrap(bytes);
        return bodyDataBuffer;
    }

    private DataBuffer cache(DataBuffer buffer) {
        try (InputStream dataBuffer = buffer.asInputStream()) {
            bytes = IOUtils.toByteArray(dataBuffer);
        } catch (IOException e) {
            bytes = ("cache payload got excetions：" + e.getMessage()).getBytes();
            return buffer;
        }
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
        bodyDataBuffer = nettyDataBufferFactory.wrap(bytes);
        return bodyDataBuffer;
    }

    private void loggin(ServerHttpRequest request) {
        Map<String, String> map = new HashMap<>(16);
        request.getHeaders().forEach((s, values) -> map.put(s, values.isEmpty() ? "" : values.get(0)));

        LoggingFormat lf = new LoggingFormat();
        lf.setId(String.valueOf(id))
                .setReqUrl(request.getPath().pathWithinApplication().value() +
                        (request.getQueryParams().isEmpty() ? "" : "?" + request.getQueryParams().toString()))
                .setMethod(request.getMethod().name())
                .setHeaders(map);

        boolean isBinaryContent = true;
        if (request.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE)) {
            isBinaryContent = request.getHeaders().getValuesAsList(HttpHeaders.CONTENT_TYPE).stream().anyMatch(Utils::isBinaryContent);
        }
        if (!isBinaryContent) {
            lf.setPayload(new String(bytes));
        }
        logger.debug(lf.reqFormat());
    }
}
