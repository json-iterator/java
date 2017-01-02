package decoder.java.util.List_java.lang;
public class Integer implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { java.util.ArrayList col = (java.util.ArrayList)com.jsoniter.CodegenAccess.resetExistingObject(iter);
if (iter.readNull()) { return null; }
if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {
return col == null ? new java.util.ArrayList(0): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
}
Object a1 = java.lang.Integer.valueOf(iter.readInt());
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
java.util.ArrayList set = col == null ? new java.util.ArrayList(1): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
set.add(a1);
return set;
}
Object a2 = java.lang.Integer.valueOf(iter.readInt());
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
java.util.ArrayList set = col == null ? new java.util.ArrayList(2): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
set.add(a1);
set.add(a2);
return set;
}
Object a3 = java.lang.Integer.valueOf(iter.readInt());
if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {
java.util.ArrayList set = col == null ? new java.util.ArrayList(3): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
set.add(a1);
set.add(a2);
set.add(a3);
return set;
}
Object a4 = java.lang.Integer.valueOf(iter.readInt());
java.util.ArrayList set = col == null ? new java.util.ArrayList(8): (java.util.ArrayList)com.jsoniter.CodegenAccess.reuseCollection(col);
set.add(a1);
set.add(a2);
set.add(a3);
set.add(a4);
while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {
set.add(java.lang.Integer.valueOf(iter.readInt()));
}
return set;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
