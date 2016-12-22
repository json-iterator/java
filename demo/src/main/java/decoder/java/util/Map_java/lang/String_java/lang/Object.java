package decoder.java.util.Map_java.lang.String_java.lang;
public class Object implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { if (iter.readNull()) { return null; }
java.util.HashMap map = (java.util.HashMap)com.jsoniter.CodegenAccess.resetExistingObject(iter);
if (map == null) { map = new java.util.HashMap(); }
if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {
return map;
}
String field = com.jsoniter.CodegenAccess.readObjectFieldAsString(iter);
map.put(field, iter.readAnyObject());
while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {
field = com.jsoniter.CodegenAccess.readObjectFieldAsString(iter);
map.put(field, iter.readAnyObject());
}
return map;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
