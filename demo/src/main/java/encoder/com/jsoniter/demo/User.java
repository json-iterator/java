package encoder.com.jsoniter.demo;
public class User implements com.jsoniter.spi.Encoder {
public static void encode_(com.jsoniter.demo.User obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.startObject();
stream.writeField("firstName");
stream.writeVal((java.lang.String)obj.firstName);
stream.writeMore();
stream.writeField("lastName");
stream.writeVal((java.lang.String)obj.lastName);
stream.writeMore();
stream.writeField("score");
stream.writeVal((int)obj.score);
stream.endObject();
}
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((com.jsoniter.demo.User)obj, stream);
}
}
