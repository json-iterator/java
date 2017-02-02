package encoder.java.util.List_java.lang;
public class Integer extends com.jsoniter.spi.EmptyEncoder {
public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
stream.write((byte)'[');
encode_((java.util.List)obj, stream);
stream.write((byte)']');
}
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
java.util.List list = (java.util.List)obj;
int size = list.size();
if (size == 0) { return; }
java.lang.Object e = list.get(0);
if (e == null) { stream.writeNull(); } else {
stream.writeVal((java.lang.Integer)e);
}
for (int i = 1; i < size; i++) {
stream.write(',');
e = list.get(i);
if (e == null) { stream.writeNull(); } else {
stream.writeVal((java.lang.Integer)e);
}
}
}
}
