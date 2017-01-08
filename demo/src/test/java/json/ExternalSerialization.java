/*
* Created by DSL Platform
* v1.7.6214.30238 
*/

package json;



public class ExternalSerialization implements com.dslplatform.json.Configuration {
	
	
	@SuppressWarnings("unchecked")
	public void configure(final com.dslplatform.json.DslJson json) {
		setup(json);
	}

	@SuppressWarnings("unchecked")
	public static void setup(final com.dslplatform.json.DslJson json) {
		
		
		json.registerReader(com.jsoniter.demo.ObjectOutput.TestObject.class, JSON_READER_struct0);
		json.registerWriter(com.jsoniter.demo.ObjectOutput.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.ObjectOutput.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.ObjectOutput.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.SimpleObjectBinding.TestObject.class, JSON_READER_struct1);
		json.registerWriter(com.jsoniter.demo.SimpleObjectBinding.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.SimpleObjectBinding.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.SimpleObjectBinding.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
	}
	
	public static void serialize(final com.jsoniter.demo.ObjectOutput.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.ObjectOutput.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}
		
			if (self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				sw.writeString(self.field2);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.ObjectOutput.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}
		
			
			if (self.field2 != null) {
				sw.writeAscii(",\"field2\":", 10);
				sw.writeString(self.field2);
			} else {
				sw.writeAscii(",\"field2\":null", 14);
			}
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.ObjectOutput.TestObject> JSON_READER_struct0 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.ObjectOutput.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.ObjectOutput.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.ObjectOutput.TestObject instance = new com.jsoniter.demo.ObjectOutput.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.ObjectOutput.TestObject deserializestruct0(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.ObjectOutput.TestObject instance = new com.jsoniter.demo.ObjectOutput.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.ObjectOutput.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		String _field1_ = null;
		String _field2_ = null;
		byte nextToken = reader.last();
		if(nextToken != '}') {
			int nameHash = reader.fillName();
			nextToken = reader.getNextToken();
			if(nextToken == 'n') {
				if (reader.wasNull()) {
					nextToken = reader.getNextToken();
				} else {
					throw new java.io.IOException("Expecting 'u' (as null) at position " + reader.positionInStream() + ". Found " + (char)nextToken);
				}
			} else {
				switch(nameHash) {
					
					case 1212206434:
						_field1_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					default:
						nextToken = reader.skip();
						break;
				}
			}
			while (nextToken == ',') {
				nextToken = reader.getNextToken();
				nameHash = reader.fillName();
				nextToken = reader.getNextToken();
				if(nextToken == 'n') {
					if (reader.wasNull()) {
						nextToken = reader.getNextToken();
						continue;
					} else {
						throw new java.io.IOException("Expecting 'u' (as null) at position " + reader.positionInStream() + ". Found " + (char)nextToken);
					}
				}
				switch(nameHash) {
					
					case 1212206434:
						_field1_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					default:
						nextToken = reader.skip();
						break;
				}
			}
			if (nextToken != '}') {
				throw new java.io.IOException("Expecting '}' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
			}
		}
		
		instance.field1 = _field1_;
		instance.field2 = _field2_;
	}
	
	public static void serialize(final com.jsoniter.demo.SimpleObjectBinding.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.SimpleObjectBinding.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != 0) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				com.dslplatform.json.NumberConverter.serialize(self.field1, sw);
			}
		
			if (self.field2 != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				com.dslplatform.json.NumberConverter.serialize(self.field2, sw);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.SimpleObjectBinding.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			sw.writeAscii("\"field1\":", 9);
			com.dslplatform.json.NumberConverter.serialize(self.field1, sw);
		
			
			sw.writeAscii(",\"field2\":", 10);
			com.dslplatform.json.NumberConverter.serialize(self.field2, sw);
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.SimpleObjectBinding.TestObject> JSON_READER_struct1 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.SimpleObjectBinding.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.SimpleObjectBinding.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.SimpleObjectBinding.TestObject instance = new com.jsoniter.demo.SimpleObjectBinding.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.SimpleObjectBinding.TestObject deserializestruct1(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.SimpleObjectBinding.TestObject instance = new com.jsoniter.demo.SimpleObjectBinding.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.SimpleObjectBinding.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		int _field1_ = 0;
		int _field2_ = 0;
		byte nextToken = reader.last();
		if(nextToken != '}') {
			int nameHash = reader.fillName();
			nextToken = reader.getNextToken();
			if(nextToken == 'n') {
				if (reader.wasNull()) {
					nextToken = reader.getNextToken();
				} else {
					throw new java.io.IOException("Expecting 'u' (as null) at position " + reader.positionInStream() + ". Found " + (char)nextToken);
				}
			} else {
				switch(nameHash) {
					
					case 1212206434:
						_field1_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					default:
						nextToken = reader.skip();
						break;
				}
			}
			while (nextToken == ',') {
				nextToken = reader.getNextToken();
				nameHash = reader.fillName();
				nextToken = reader.getNextToken();
				if(nextToken == 'n') {
					if (reader.wasNull()) {
						nextToken = reader.getNextToken();
						continue;
					} else {
						throw new java.io.IOException("Expecting 'u' (as null) at position " + reader.positionInStream() + ". Found " + (char)nextToken);
					}
				}
				switch(nameHash) {
					
					case 1212206434:
						_field1_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					default:
						nextToken = reader.skip();
						break;
				}
			}
			if (nextToken != '}') {
				throw new java.io.IOException("Expecting '}' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
			}
		}
		
		instance.field1 = _field1_;
		instance.field2 = _field2_;
	}
}
