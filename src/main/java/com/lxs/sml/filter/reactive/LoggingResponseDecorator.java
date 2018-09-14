package com.lxs.sml.filter.reactive;

import com.lxs.sml.filter.LoggingFormat;
import com.lxs.sml.filter.Utils;
import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * @author liuxinsi
 * @date 2018/9/11 17:42
 */
public class LoggingResponseDecorator extends ServerHttpResponseDecorator {
    private Logger logger = LoggerFactory.getLogger(LoggingResponseDecorator.class);
    private boolean readable = true;
    private ByteArrayOutputStream baos;
    private Long id;

    public LoggingResponseDecorator(ServerHttpResponse delegate, Long id) {
        super(delegate);

        this.id = id;
        baos = new ByteArrayOutputStream();

        delegate.beforeCommit(() -> {
            logResponse(delegate);
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (readable) {
            readable = false;
            return super.writeWith(Flux.from(body).map((Function<DataBuffer, DataBuffer>) dataBuffer -> {
                try {
                    Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                } catch (IOException e) {
                    logger.debug("read response got ex：{}", e.getMessage());
                }
                return dataBuffer;
            }));
        } else {
            return super.writeWith(body);
        }
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        if (readable) {
            readable = false;
            return super.writeAndFlushWith(Flux.from(body).map(x -> Flux.from(x).map((Function<DataBuffer, DataBuffer>) dataBuffer -> {
                try {
                    Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                } catch (IOException e) {
                    logger.debug("read response got ex：{}", e.getMessage());
                }
                return dataBuffer;
            })));
        } else {
            return super.writeAndFlushWith(body);
        }
    }

    private void logResponse(ServerHttpResponse response) {
        Map<String, String> map = new HashMap<>(16);
        response.getHeaders().forEach((s, values) -> map.put(s, values.isEmpty() ? "" : values.get(0)));

        LoggingFormat lf = new LoggingFormat();
        lf.setId(String.valueOf(id))
                .setStatus(response.getStatusCode() == null ? 200 : response.getStatusCode().value())
                .setHeaders(map);

        boolean isBinaryContent = true;
        if (response.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE)) {
            isBinaryContent = response.getHeaders().getValuesAsList(HttpHeaders.CONTENT_TYPE).stream().anyMatch(Utils::isBinaryContent);
        }
        if (!isBinaryContent) {
            lf.setPayload(new String(baos.toByteArray()));
            IOUtils.closeQuietly(baos);
        }

        if (logger.isDebugEnabled()) {
            logger.debug(lf.respFormat());
        }
    }
}
