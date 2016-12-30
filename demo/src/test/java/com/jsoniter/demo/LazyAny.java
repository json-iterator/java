package com.jsoniter.demo;

import com.jsoniter.Any;
import com.jsoniter.JsonIterator;
import com.jsoniter.Slice;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@State(Scope.Thread)
public class LazyAny {

    private JsonIterator iter;
    private Slice input;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) throws IOException {
        InputStream resourceAsStream = LazyAny.class.getResourceAsStream("/large.json");
        byte[] buf = new byte[32 * 1024];
        int size = resourceAsStream.read(buf);
        input = new Slice(buf, 0, size);
        iter = new JsonIterator();
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "LazyAny",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
                "-prof", "stack",
        });
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        System.out.println(jsoniter());
        System.out.println(jsoniter_object());
    }

    @Benchmark
    public void jsoniter(Blackhole bh) throws IOException {
        bh.consume(jsoniter());
    }

    @Benchmark
    public void jsoniter_object(Blackhole bh) throws IOException {
        bh.consume(jsoniter_object());
    }

    public int jsoniter() throws IOException {
        iter.reset(input);
Any users = iter.readAny();
int total = 0;
for (Any user : users) {
    total += user.getValue("friends").size();
}
return total;
    }

    public int jsoniter_object() throws IOException {
        iter.reset(input);
List users = (List) iter.read();
int total = 0;
for (Object userObj : users) {
    Map user = (Map) userObj;
    List friends = (List) user.get("friends");
    total += friends.size();
}
return total;
    }
}
