package encoder.com.jsoniter.demo;
public class User extends com.jsoniter.spi.EmptyEncoder {
public static void encode_(com.jsoniter.demo.User set, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (set == null) { stream.writeNull(); return; }
stream.writeObjectStart();
stream.writeObjectField("firstName");
stream.writeVal((java.lang.String)set.firstName);
stream.writeMore();
stream.writeObjectField("lastName");
stream.writeVal((java.lang.String)set.lastName);
stream.writeMore();
stream.writeObjectField("score");
stream.writeVal((int)set.score);
stream.writeObjectEnd();
}
public void encode(java.lang.Object set, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((com.jsoniter.demo.User)set, stream);
}
}
