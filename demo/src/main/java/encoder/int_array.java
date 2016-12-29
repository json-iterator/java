package encoder;
public class int_array implements com.jsoniter.spi.Encoder {
public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (obj == null) { stream.writeNull(); return; }
int[] arr = (int[])obj;
if (arr.length == 0) { stream.writeEmptyArray(); return; }
stream.writeArrayStart();
int i = 0;
stream.writeVal((int)arr[i++]);
while (i < arr.length) {
stream.writeMore();
stream.writeVal((int)arr[i++]);
}
stream.writeArrayEnd();
}
public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((int[])obj, stream);
}
}
