package com.lxs.sml.filter;

import org.apache.commons.io.output.TeeOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author liuxinsi
 */
public class HttpServletResponseAdapter extends HttpServletResponseWrapper {
    private ByteArrayOutputStream bos;
    private TeeOutputStream tee;
    private PrintWriter writer;

    public HttpServletResponseAdapter(HttpServletResponse response) throws IOException {
        super(response);
        bos = new ByteArrayOutputStream();
        tee = new TeeOutputStream(response.getOutputStream(), bos);
        writer = new PrintWriter(bos);
    }


    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                tee.write(b);
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new TeePrintWriter(new PrintWriter[]{super.getWriter(), writer});
    }

    public byte[] toByteArray() {
        return bos.toByteArray();
    }
}
