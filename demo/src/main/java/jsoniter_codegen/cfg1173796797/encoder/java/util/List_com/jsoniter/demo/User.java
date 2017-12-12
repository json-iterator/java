package jsoniter_codegen.cfg1173796797.encoder.java.util.List_com.jsoniter.demo;
public class User implements com.jsoniter.spi.Encoder {
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
encode_((java.util.List)obj, stream);
}
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
java.util.List list = (java.util.List)obj;
int size = list.size();
if (size == 0) { stream.write((byte)'[', (byte)']'); return; }
stream.writeArrayStart(); stream.writeIndention();
java.lang.Object e = list.get(0);
if (e == null) { stream.writeNull(); } else {

jsoniter_codegen.cfg1173796797.encoder.com.jsoniter.demo.User.encode_((com.jsoniter.demo.User)e, stream);

}
for (int i = 1; i < size; i++) {
stream.writeMore();
e = list.get(i);
if (e == null) { stream.writeNull(); } else {

jsoniter_codegen.cfg1173796797.encoder.com.jsoniter.demo.User.encode_((com.jsoniter.demo.User)e, stream);

}
}
stream.writeArrayEnd();
}
}
