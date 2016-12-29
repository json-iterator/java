package encoder.java.util.List_java.lang;
public class Integer implements com.jsoniter.spi.Encoder {
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
java.util.Iterator iter = ((java.util.Collection)obj).iterator();
if (!iter.hasNext()) { stream.writeEmptyArray(); return; }
stream.writeArrayStart();
stream.writeVal((java.lang.Integer)iter.next());
while (iter.hasNext()) {
stream.writeMore();
stream.writeVal((java.lang.Integer)iter.next());
}
stream.writeArrayEnd();
}
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((java.util.List)obj, stream);
}
}
