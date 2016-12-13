package com.jsoniter;


import com.dslplatform.json.DslJson;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Thread)
public class BenchmarkRunner {

    private DslJson dslJson;
    private byte[] input;
    private Jsoniter iter;
    private TypeLiteral<Users> typeLiteral;

    @Setup(Level.Trial)
    public void init() throws IOException {
        dslJson = new DslJson();
        iter = Jsoniter.parse(new byte[0]);
        input = "{'users':[{'_id':'49023014153147979279','index':504544056,'guid':'7Mfe3vuSTRUGe4tU5w9o','isActive':false,'balance':'lkGyu1My3XAcreUrHBJB','picture':'EObzaMQrsi8V3Ej05CInjEX1abWRkaDrubBuC9HdSDcQ4Q9PCSQgK4k8BxPSFVuyOhJCYNjz0AVrgYGd1dB9m0vvFestatKPfIcT','age':57,'eyeColor':'5DhRLd0oQP363x7Hk5QS','name':'ucE7kkWJUc7ANrJrnCpt','gender':'eDjOjJVP0ycDIY4h6ph9','company':'WIJAJJRbMlpcoifJpRFr','email':'1jXc6n002cfDHeAE2Qoh','phone':'xeAxm8tRmM0nkrJfY08z','address':'KHldMLmZnLPTEteIUzIX','about':'aW00u8sagEM2RSZSzhfk','registered':'MXHOUJ5BEi97yL0qCxBz','latitude':34.78675932619872,'longitude':150.3409799411619,'tags':['wc9XTeEMxD','5MtdX74JxL','YDDE4Zezq7'],'friends':[{'id':'6678','name':'yDMIiMgcYjsrPTSjjibnZxlwuFyJle'},{'id':'8871','name':'kLhbDxMUIsYJMjbDmyRsRafSRCAyno'},{'id':'6871','name':'mcyhcYaaKYBTAUBGWhDjpvwAdzERye'}],'greeting':'KskIZ897aVE0w6pbU5V8','favoriteFruit':'qL0AcntEgsEM1SQNBbou'},{'_id':'63266066379660810414','index':688601857,'guid':'slOQ51OBO7qKlHNuDLpZ','isActive':false,'balance':'mslFmTBKcgTF32L8yrMJ','picture':'WETlnQGXwPy2qf4xWuSuNpU3trvxMCulb6eOZSqGcrIOP3Sn6SrHDDjhurQ6UtNmWfGdpvej4lAgAQsotXVl053xyKIj00QWclCK','age':22,'eyeColor':'MV202fvdE5kfLMT1TJwq','name':'NpkpIySh4A7qZQd0yP4y','gender':'EK1CNkYHNPZr8M1tammD','company':'QcQ5JzC4OvJh0EiAZoOc','email':'SIguvNJ6GmNwX9pyqR4r','phone':'QO8inFeMD2XVEwz2gn5X','address':'DSqEzXHO8QuoAvoKCItt','about':'DcgNVUIYSzzB2RQWLUNb','registered':'bWo9eERfRHsNtCBfkMyR','latitude':43.236164998621916,'longitude':134.05843169650643,'tags':[],'friends':[],'greeting':'0o8pSuEAxXTwXpCN9XTO','favoriteFruit':'HOt8tfEQsP5Jwk7jlswr'}]}".replace('\'', '"').getBytes();
        typeLiteral = new TypeLiteral<Users>() {
        };
    }

    @Benchmark
    public void jsoniter(Blackhole bh) throws Exception {
        iter.reset(input);
        bh.consume(iter.read(typeLiteral));
    }

    @Benchmark
    public void dsljson(Blackhole bh) throws Exception {
        bh.consume(dslJson.deserialize(Users.class, input, input.length));
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
