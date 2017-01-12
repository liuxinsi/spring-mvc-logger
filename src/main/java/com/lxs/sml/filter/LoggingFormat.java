package com.lxs.sml.filter;


import java.util.HashMap;
import java.util.Map;

/**
 * @author liuxinsi
 */
public class LoggingFormat {
    private static String DEFAULT_REQ_LAYOUT = "Request：[%r][%flag] [%r]ID：[%id] [%r]URL：[%url] [%r]Method：[%method] [%r]Headers：[%header] [%r]Payload：[%payload] [%r][%flag]";
    private static String DEFAULT_RESP_LAYOUT = "Response：[%r][%flag] [%r]ID：[%id] [%r]RespCode：[%status] [%r]Headers：[%header] [%r]Payload：[%payload] [%r][%flag]";
    private String flag = "---------------------------------------------------------------";
    private String crlf = System.getProperty("line.separator");
    private String id;
    private String reqUrl;
    private String method;
    private Map<String, String> headers;
    private Integer status;
    private String payload;

    private Map<String, String> build() {
        Map<String, String> m = new HashMap<>();
        if (flag != null) {
            m.put("flag", flag);
        }
        if (crlf != null) {
            m.put("r", crlf);
        }
        if (id != null) {
            m.put("id", id);
        }
        if (reqUrl != null) {
            m.put("url", reqUrl);
        }
        if (method != null) {
            m.put("method", method);
        }
        if (status != null) {
            m.put("status", status.toString());
        }
        if (payload != null) {
            m.put("payload", payload);
        }
        m.put("header", headers == null ? "" : headers.toString());
        return m;
    }

    public String reqFormat() {
        return format(DEFAULT_REQ_LAYOUT);
    }

    public String respFormat() {
        return format(DEFAULT_RESP_LAYOUT);
    }

    private String format(String template) {
        Map<String, String> m = build();
        return m.entrySet()
                .stream()
                .reduce(template,
                        (s, entry) -> s.replace("[%" + entry.getKey() + "]", entry.getValue()),
                        (s, s2) -> s);
    }

    public static void setDefaultReqLayout(String defaultReqLayout) {
        DEFAULT_REQ_LAYOUT = defaultReqLayout;
    }

    public static void setDefaultRespLayout(String defaultRespLayout) {
        DEFAULT_RESP_LAYOUT = defaultRespLayout;
    }

    public LoggingFormat setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public LoggingFormat setCrlf(String crlf) {
        this.crlf = crlf;
        return this;
    }

    public LoggingFormat setId(String id) {
        this.id = id;
        return this;
    }

    public LoggingFormat setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
        return this;
    }

    public LoggingFormat setMethod(String method) {
        this.method = method;
        return this;
    }

    public LoggingFormat setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public LoggingFormat setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public LoggingFormat setPayload(String payload) {
        this.payload = payload;
        return this;
    }
}
