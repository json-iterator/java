package encoder.java.util.Map_java.lang.String_java.lang;
public class Object implements com.jsoniter.spi.Encoder {
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
java.util.Map map = (java.util.Map)obj;
java.util.Iterator iter = map.entrySet().iterator();
if(!iter.hasNext()) { stream.writeEmptyObject(); return; }
java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
stream.writeObjectStart();
stream.writeObjectField((String)entry.getKey());
stream.writeVal((java.lang.Object)entry.getValue());
while(iter.hasNext()) {
entry = (java.util.Map.Entry)iter.next();
stream.writeMore();
stream.writeObjectField((String)entry.getKey());
stream.writeVal((java.lang.Object)entry.getValue());
}
stream.writeObjectEnd();
}
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((java.util.Map)obj, stream);
}
}
