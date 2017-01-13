package encoder.com.jsoniter.demo;
public class User extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.writeRaw("{\"firstName\":\"", 14);
encode_((com.jsoniter.demo.User)obj, stream);
stream.write('}');
}
public static void encode_(com.jsoniter.demo.User obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
com.jsoniter.output.CodegenAccess.writeStringWithoutQuote((java.lang.String)obj.firstName, stream);
stream.writeRaw("\",\"lastName\":\"", 14);
com.jsoniter.output.CodegenAccess.writeStringWithoutQuote((java.lang.String)obj.lastName, stream);
stream.writeRaw("\",\"score\":", 10);
stream.writeVal((int)obj.score);
}
}
