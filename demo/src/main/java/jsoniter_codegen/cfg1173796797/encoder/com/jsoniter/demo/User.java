package jsoniter_codegen.cfg1173796797.encoder.com.jsoniter.demo;
public class User implements com.jsoniter.spi.Encoder {
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
encode_((com.jsoniter.demo.User)obj, stream);
}
public static void encode_(com.jsoniter.demo.User obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
stream.writeObjectStart();
stream.writeIndention();
stream.writeObjectField("firstName");
stream.writeVal((java.lang.String)obj.firstName);
stream.writeMore();
stream.writeObjectField("lastName");
stream.writeVal((java.lang.String)obj.lastName);
stream.writeMore();
stream.writeObjectField("score");
stream.writeVal((int)obj.score);
stream.writeObjectEnd();
}
}
