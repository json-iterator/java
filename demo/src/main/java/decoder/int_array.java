package decoder;
public class int_array implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { if (iter.readNull()) { return null; }
if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {
return new int[0];
}
int a1 = iter.readInt();
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
return new int[]{ a1 };
}
int a2 = iter.readInt();
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
return new int[]{ a1, a2 };
}
int a3 = iter.readInt();
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
return new int[]{ a1, a2, a3 };
}
int a4 = (int) iter.readInt();
int[] arr = new int[8];
arr[0] = a1;
arr[1] = a2;
arr[2] = a3;
arr[3] = a4;
int i = 4;
while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {
if (i == arr.length) {
int[] newArr = new int[arr.length * 2];
System.arraycopy(arr, 0, newArr, 0, arr.length);
arr = newArr;
}
arr[i++] = iter.readInt();
}
int[] result = new int[i];
System.arraycopy(arr, 0, result, 0, i);
return result;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
