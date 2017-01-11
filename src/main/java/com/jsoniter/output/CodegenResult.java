package com.jsoniter.output;

import com.jsoniter.JsonException;
import com.jsoniter.JsonIterator;

import java.io.IOException;

class CodegenResult {

    String prelude = null; // first
    String epilogue = null; // last
    private StringBuilder lines = new StringBuilder();
    private StringBuilder buffered = new StringBuilder();

    public static String bufferToWriteOp(String buffered) {
        if (buffered.length() == 1) {
            return String.format("stream.write('%s');", escape(buffered.charAt(0)));
        } else if (buffered.length() == 2) {
            return String.format("stream.write('%s', '%s');",
                    escape(buffered.charAt(0)), escape(buffered.charAt(1)));
        } else if (buffered.length() == 3) {
            return String.format("stream.write('%s', '%s', '%s');",
                    escape(buffered.charAt(0)), escape(buffered.charAt(1)), escape(buffered.charAt(2)));
        } else {
            StringBuilder escaped = new StringBuilder();
            for (int i = 0; i < buffered.length(); i++) {
                char c = buffered.charAt(i);
                if (c == '"') {
                    escaped.append('\\');
                }
                escaped.append(c);
            }
            return String.format("stream.writeRaw(\"%s\", %s);", escaped.toString(), buffered.length());
        }
    }

    private static String escape(char c) {
        if (c == '"') {
            return "\\\"";
        }
        if (c == '\\') {
            return "\\\\";
        }
        return String.valueOf(c);
    }

    public void append(String str) {
        if (str.contains("stream")) {
            // maintain the order of write op
            // must flush now
            appendBuffer();
        }
        lines.append(str);
        lines.append("\n");
    }

    public void buffer(char c) {
        buffered.append(c);
    }

    public void buffer(String s) {
        if (s == null) {
            return;
        }
        buffered.append(s);
    }

    public void flushBuffer() {
        if (buffered.length() == 0) {
            return;
        }
        if (prelude == null) {
            prelude = buffered.toString();
        } else {
            epilogue = buffered.toString();
        }
        buffered.setLength(0);
    }

    public String toString() {
        return lines.toString();
    }

    public void appendBuffer() {
        flushBuffer();
        if (epilogue != null) {
            lines.append(bufferToWriteOp(epilogue));
            lines.append("\n");
            epilogue = null;
        }
    }
}
