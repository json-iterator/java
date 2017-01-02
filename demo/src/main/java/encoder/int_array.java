package encoder;
public class int_array extends com.jsoniter.spi.EmptyEncoder {
public static void encode_(java.lang.Object set, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
if (set == null) { stream.writeNull(); return; }
int[] arr = (int[])set;
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
public void encode(java.lang.Object set, com.jsoniter.output.JsonStream stream) throws java.io.IOException {
encode_((int[])set, stream);
}
}
