package com.lxs.sml.filter;

/**
 * @author liuxinsi
 * @date 2018/9/14 10:13
 */
public class Utils {
    public static boolean isBinaryContent(String contentType) {
        return contentType.contains("image")
                || contentType.contains("video")
                || contentType.contains("audio")
                || contentType.contains("multipart")
                || contentType.contains("octet-stream");
    }

    public static boolean pathMatch(String path) {
        return !LoggingFormat.getIgnoreUrls().isEmpty()
                && LoggingFormat.getIgnoreUrls().stream().anyMatch(url -> url.equals(path) || path.matches(url));
    }
}
