package encoder.java.util.List_com.jsoniter.demo;
public class User implements com.jsoniter.spi.Encoder {
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
java.util.Iterator iter = ((java.util.Collection)obj).iterator();
if (!iter.hasNext()) { stream.writeEmptyArray(); return; }
stream.writeArrayStart();
encoder.com.jsoniter.demo.User.encode_((com.jsoniter.demo.User)iter.next(), stream);
while (iter.hasNext()) {
stream.writeMore();
encoder.com.jsoniter.demo.User.encode_((com.jsoniter.demo.User)iter.next(), stream);
}
stream.writeArrayEnd();
}
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((java.util.List)obj, stream);
}
}
