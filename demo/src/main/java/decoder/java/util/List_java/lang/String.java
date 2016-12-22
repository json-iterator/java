package decoder.java.util.List_java.lang;
public class String implements com.jsoniter.spi.Decoder {
public static Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { if (iter.readNull()) { return null; }
java.util.ArrayList col = (java.util.ArrayList)com.jsoniter.CodegenAccess.resetExistingObject(iter);
if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {
return col == null ? new java.util.ArrayList(0): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
}
Object a1 = iter.readString();
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
java.util.ArrayList obj = col == null ? new java.util.ArrayList(1): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
obj.add(a1);
return obj;
}
Object a2 = iter.readString();
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
java.util.ArrayList obj = col == null ? new java.util.ArrayList(2): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
obj.add(a1);
obj.add(a2);
return obj;
}
Object a3 = iter.readString();
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
java.util.ArrayList obj = col == null ? new java.util.ArrayList(3): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
obj.add(a1);
obj.add(a2);
obj.add(a3);
return obj;
}
Object a4 = iter.readString();
java.util.ArrayList obj = col == null ? new java.util.ArrayList(8): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
obj.add(a1);
obj.add(a2);
obj.add(a3);
obj.add(a4);
while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {
obj.add(iter.readString());
}
return obj;
}public Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
