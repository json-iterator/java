package com.jsoniter.output;

class CodegenContext {

    StringBuilder lines = new StringBuilder();
    StringBuilder buffered = new StringBuilder();

    public void append(String str) {
        lines.append(str);
        lines.append("\n");
    }

    public void flushBuffer() {
        if (buffered.length() == 0) {
            return;
        }
        if (buffered.length() == 1) {
            append(String.format("stream.write('%s');", buffered.toString()));
        } else {
            append(String.format("stream.writeRaw(\"%s\");", buffered.toString()));
        }
        buffered.setLength(0);
    }

    public String toString() {
        return lines.toString();
    }
}
