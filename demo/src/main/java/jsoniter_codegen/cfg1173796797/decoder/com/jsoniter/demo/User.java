package jsoniter_codegen.cfg1173796797.decoder.com.jsoniter.demo;
public class User implements com.jsoniter.spi.Decoder {
public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);
byte nextToken = com.jsoniter.CodegenAccess.readByte(iter);
if (nextToken != '{') {
if (nextToken == 'n') {
com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);
return null;
} else {
nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
if (nextToken == 'n') {
com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);
return null;
}
} // end of if null
} // end of if {
nextToken = com.jsoniter.CodegenAccess.readByte(iter);
if (nextToken != '"') {
if (nextToken == '}') {
return (existingObj == null ? new com.jsoniter.demo.User() : (com.jsoniter.demo.User)existingObj);
} else {
nextToken = com.jsoniter.CodegenAccess.nextToken(iter);
if (nextToken == '}') {
return (existingObj == null ? new com.jsoniter.demo.User() : (com.jsoniter.demo.User)existingObj);
} else {
com.jsoniter.CodegenAccess.unreadByte(iter);
}
} // end of if end
} else { com.jsoniter.CodegenAccess.unreadByte(iter); }// end of if not quote
java.lang.String _firstName_ = null;
java.lang.String _lastName_ = null;
int _score_ = 0;
do {
switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {
case -1078100014: 
_lastName_ = (java.lang.String)iter.readString();
continue;
case -799547430: 
_firstName_ = (java.lang.String)iter.readString();
continue;
case -768634731: 
_score_ = (int)com.jsoniter.CodegenAccess.readInt("score@jsoniter_codegen.cfg1173796797.decoder.com.jsoniter.demo.User", iter);
continue;
}
iter.skip();
} while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter));
com.jsoniter.demo.User obj = (existingObj == null ? new com.jsoniter.demo.User() : (com.jsoniter.demo.User)existingObj);
obj.firstName = _firstName_;
obj.lastName = _lastName_;
obj.score = _score_;
return obj;
}public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {
return decode_(iter);
}
}
