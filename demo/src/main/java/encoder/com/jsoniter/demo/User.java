package encoder.com.jsoniter.demo;
public class User extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.writeRaw("{\"lastName\":\"", 13);
encode_((com.jsoniter.demo.User)obj, stream);
stream.write((byte)'}');
}
public static void encode_(com.jsoniter.demo.User obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
com.jsoniter.output.CodegenAccess.writeStringWithoutQuote((java.lang.String)obj.lastName, stream);
stream.writeRaw("\",\"firstName\":\"", 15);
com.jsoniter.output.CodegenAccess.writeStringWithoutQuote((java.lang.String)obj.firstName, stream);
stream.writeRaw("\",\"score\":", 10);
stream.writeVal((int)obj.score);
}
}
