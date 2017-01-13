package decoder.com.jsoniter.demo;
public class User implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { if (iter.readNull()) { com.jsoniter.CodegenAccess.resetExistingObject(iter); return null; }
com.jsoniter.demo.User obj = (com.jsoniter.CodegenAccess.existingObject(iter) == null ? new com.jsoniter.demo.User() : (com.jsoniter.demo.User)com.jsoniter.CodegenAccess.resetExistingObject(iter));
if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return obj; }
switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {
case -799547430: 
obj.firstName = (java.lang.String)iter.readString();
break;
case -1078100014: 
obj.lastName = (java.lang.String)iter.readString();
break;
case -768634731: 
obj.score = com.jsoniter.CodegenAccess.readInt("score@decoder.com.jsoniter.demo.User", iter);
break;
default:
iter.skip();
}
while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {
switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {
case -799547430: 
obj.firstName = (java.lang.String)iter.readString();
continue;
case -1078100014: 
obj.lastName = (java.lang.String)iter.readString();
continue;
case -768634731: 
obj.score = com.jsoniter.CodegenAccess.readInt("score@decoder.com.jsoniter.demo.User", iter);
continue;
}
iter.skip();
}
return obj;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
