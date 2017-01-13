package encoder.java.util.Map_java.lang.String_java.lang;
public class Object extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write('{');
encode_((java.util.Map)obj, stream);
stream.write('}');
}
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
java.util.Map map = (java.util.Map)obj;
java.util.Iterator iter = map.entrySet().iterator();
if(!iter.hasNext()) { return; }
java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
stream.writeVal((String)entry.getKey());
stream.write(':');
if (entry.getValue() == null) { stream.writeNull(); } else {
stream.writeVal((java.lang.Object)entry.getValue());
}
while(iter.hasNext()) {
entry = (java.util.Map.Entry)iter.next();
stream.write(',');
stream.writeObjectField((String)entry.getKey());
if (entry.getValue() == null) { stream.writeNull(); } else {
stream.writeVal((java.lang.Object)entry.getValue());
}
}
}
}
