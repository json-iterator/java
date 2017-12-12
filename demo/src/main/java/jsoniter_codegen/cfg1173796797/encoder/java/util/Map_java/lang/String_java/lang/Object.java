package jsoniter_codegen.cfg1173796797.encoder.java.util.Map_java.lang.String_java.lang;
public class Object implements com.jsoniter.spi.Encoder {
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
encode_((java.util.Map)obj, stream);
}
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
java.util.Map map = (java.util.Map)obj;
java.util.Iterator iter = map.entrySet().iterator();
if(!iter.hasNext()) { stream.write((byte)'{', (byte)'}'); return; }
java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
stream.writeObjectStart(); stream.writeIndention();
stream.writeVal((java.lang.String)entry.getKey());
stream.write((byte)':', (byte)' ');
if (entry.getValue() == null) { stream.writeNull(); } else {
stream.writeVal((java.lang.Object)entry.getValue());
}
while(iter.hasNext()) {
entry = (java.util.Map.Entry)iter.next();
stream.writeMore();
stream.writeVal((java.lang.String)entry.getKey());
stream.write((byte)':', (byte)' ');
if (entry.getValue() == null) { stream.writeNull(); } else {
stream.writeVal((java.lang.Object)entry.getValue());
}
}
stream.writeObjectEnd();
}
}
