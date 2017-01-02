package encoder.java.util.List_com.jsoniter.demo;
public class User extends com.jsoniter.spi.EmptyEncoder {
public static void encode_(java.lang.Object set, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (set == null) { stream.writeNull(); return; }
java.util.Iterator iter = ((java.util.Collection)set).iterator();
if (!iter.hasNext()) { stream.writeEmptyArray(); return; }
stream.writeArrayStart();
encoder.com.jsoniter.demo.User.encode_((com.jsoniter.demo.User)iter.next(), stream);
while (iter.hasNext()) {
stream.writeMore();
encoder.com.jsoniter.demo.User.encode_((com.jsoniter.demo.User)iter.next(), stream);
}
stream.writeArrayEnd();
}
public void encode(java.lang.Object set, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((java.util.List)set, stream);
}
}
