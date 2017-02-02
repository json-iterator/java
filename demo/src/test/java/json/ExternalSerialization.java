/*
* Created by DSL Platform
* v1.7.6218.18384 
*/

package json;



public class ExternalSerialization implements com.dslplatform.json.Configuration {
	
	
	@SuppressWarnings("unchecked")
	public void configure(final com.dslplatform.json.DslJson json) {
		setup(json);
	}

	@SuppressWarnings("unchecked")
	public static void setup(final com.dslplatform.json.DslJson json) {
		
		
		json.registerReader(com.jsoniter.demo.object_with_4_fields.TestObject.class, JSON_READER_struct7);
		json.registerWriter(com.jsoniter.demo.object_with_4_fields.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.object_with_4_fields.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.object_with_4_fields.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.object_with_2_fields.TestObject.class, JSON_READER_struct2);
		json.registerWriter(com.jsoniter.demo.object_with_2_fields.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.object_with_2_fields.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.object_with_2_fields.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.object_with_1_double_field.TestObject.class, JSON_READER_struct8);
		json.registerWriter(com.jsoniter.demo.object_with_1_double_field.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.object_with_1_double_field.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.object_with_1_double_field.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.object_with_1_field.TestObject.class, JSON_READER_struct4);
		json.registerWriter(com.jsoniter.demo.object_with_1_field.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.object_with_1_field.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.object_with_1_field.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.object_with_3_fields.TestObject.class, JSON_READER_struct1);
		json.registerWriter(com.jsoniter.demo.object_with_3_fields.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.object_with_3_fields.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.object_with_3_fields.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.object_with_1_int_field.TestObject.class, JSON_READER_struct3);
		json.registerWriter(com.jsoniter.demo.object_with_1_int_field.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.object_with_1_int_field.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.object_with_1_int_field.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.object_with_5_fields.TestObject.class, JSON_READER_struct5);
		json.registerWriter(com.jsoniter.demo.object_with_5_fields.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.object_with_5_fields.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.object_with_5_fields.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.ObjectOutput.TestObject.class, JSON_READER_struct6);
		json.registerWriter(com.jsoniter.demo.ObjectOutput.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.ObjectOutput.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.ObjectOutput.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
		
		json.registerReader(com.jsoniter.demo.SimpleObjectBinding.TestObject.class, JSON_READER_struct0);
		json.registerWriter(com.jsoniter.demo.SimpleObjectBinding.TestObject.class, new com.dslplatform.json.JsonWriter.WriteObject<com.jsoniter.demo.SimpleObjectBinding.TestObject>() {
			@Override
			public void write(com.dslplatform.json.JsonWriter writer, com.jsoniter.demo.SimpleObjectBinding.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
	}
	
	public static void serialize(final com.jsoniter.demo.object_with_4_fields.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_4_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}
		
			if (self.field3 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field3\":", 9);
				sw.writeString(self.field3);
			}
		
			if (self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				sw.writeString(self.field2);
			}
		
			if (self.field4 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field4\":", 9);
				sw.writeString(self.field4);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_4_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}
		
			
			if (self.field3 != null) {
				sw.writeAscii(",\"field3\":", 10);
				sw.writeString(self.field3);
			} else {
				sw.writeAscii(",\"field3\":null", 14);
			}
		
			
			if (self.field2 != null) {
				sw.writeAscii(",\"field2\":", 10);
				sw.writeString(self.field2);
			} else {
				sw.writeAscii(",\"field2\":null", 14);
			}
		
			
			if (self.field4 != null) {
				sw.writeAscii(",\"field4\":", 10);
				sw.writeString(self.field4);
			} else {
				sw.writeAscii(",\"field4\":null", 14);
			}
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_4_fields.TestObject> JSON_READER_struct7 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_4_fields.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_4_fields.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_4_fields.TestObject instance = new com.jsoniter.demo.object_with_4_fields.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_4_fields.TestObject deserializestruct7(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_4_fields.TestObject instance = new com.jsoniter.demo.object_with_4_fields.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_4_fields.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		String _field1_ = null;
		String _field3_ = null;
		String _field2_ = null;
		String _field4_ = null;
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
					case 1178651196:
						_field3_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = com.dslplatform.json.StringConverter.deserialize(reader);
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
					case 1178651196:
						_field3_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = com.dslplatform.json.StringConverter.deserialize(reader);
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
		instance.field3 = _field3_;
		instance.field2 = _field2_;
		instance.field4 = _field4_;
	}
	
	public static void serialize(final com.jsoniter.demo.object_with_2_fields.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_2_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
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

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_2_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
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

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_2_fields.TestObject> JSON_READER_struct2 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_2_fields.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_2_fields.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_2_fields.TestObject instance = new com.jsoniter.demo.object_with_2_fields.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_2_fields.TestObject deserializestruct2(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_2_fields.TestObject instance = new com.jsoniter.demo.object_with_2_fields.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_2_fields.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
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
	
	public static void serialize(final com.jsoniter.demo.object_with_1_double_field.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_1_double_field.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != 0.0) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				com.dslplatform.json.NumberConverter.serialize(self.field1, sw);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_1_double_field.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			sw.writeAscii("\"field1\":", 9);
			com.dslplatform.json.NumberConverter.serialize(self.field1, sw);
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_1_double_field.TestObject> JSON_READER_struct8 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_1_double_field.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_1_double_field.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_1_double_field.TestObject instance = new com.jsoniter.demo.object_with_1_double_field.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_1_double_field.TestObject deserializestruct8(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_1_double_field.TestObject instance = new com.jsoniter.demo.object_with_1_double_field.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_1_double_field.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		double _field1_ = 0.0;
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
						_field1_ = com.dslplatform.json.NumberConverter.deserializeDouble(reader);
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
						_field1_ = com.dslplatform.json.NumberConverter.deserializeDouble(reader);
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
	}
	
	public static void serialize(final com.jsoniter.demo.object_with_1_field.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_1_field.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_1_field.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_1_field.TestObject> JSON_READER_struct4 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_1_field.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_1_field.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_1_field.TestObject instance = new com.jsoniter.demo.object_with_1_field.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_1_field.TestObject deserializestruct4(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_1_field.TestObject instance = new com.jsoniter.demo.object_with_1_field.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static void deserialize(final com.jsoniter.demo.object_with_1_field.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		String _field1_ = null;
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
	}
	
	public static void serialize(final com.jsoniter.demo.object_with_3_fields.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_3_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}
		
			if (self.field3 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field3\":", 9);
				sw.writeString(self.field3);
			}
		
			if (self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				sw.writeString(self.field2);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_3_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}
		
			
			if (self.field3 != null) {
				sw.writeAscii(",\"field3\":", 10);
				sw.writeString(self.field3);
			} else {
				sw.writeAscii(",\"field3\":null", 14);
			}
		
			
			if (self.field2 != null) {
				sw.writeAscii(",\"field2\":", 10);
				sw.writeString(self.field2);
			} else {
				sw.writeAscii(",\"field2\":null", 14);
			}
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_3_fields.TestObject> JSON_READER_struct1 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_3_fields.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_3_fields.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_3_fields.TestObject instance = new com.jsoniter.demo.object_with_3_fields.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_3_fields.TestObject deserializestruct1(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_3_fields.TestObject instance = new com.jsoniter.demo.object_with_3_fields.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static void deserialize(final com.jsoniter.demo.object_with_3_fields.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		String _field1_ = null;
		String _field3_ = null;
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
					case 1178651196:
						_field3_ = com.dslplatform.json.StringConverter.deserialize(reader);
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
					case 1178651196:
						_field3_ = com.dslplatform.json.StringConverter.deserialize(reader);
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
		instance.field3 = _field3_;
		instance.field2 = _field2_;
	}
	
	public static void serialize(final com.jsoniter.demo.object_with_1_int_field.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_1_int_field.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != 0) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				com.dslplatform.json.NumberConverter.serialize(self.field1, sw);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_1_int_field.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			sw.writeAscii("\"field1\":", 9);
			com.dslplatform.json.NumberConverter.serialize(self.field1, sw);
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_1_int_field.TestObject> JSON_READER_struct3 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_1_int_field.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_1_int_field.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_1_int_field.TestObject instance = new com.jsoniter.demo.object_with_1_int_field.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_1_int_field.TestObject deserializestruct3(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_1_int_field.TestObject instance = new com.jsoniter.demo.object_with_1_int_field.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_1_int_field.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		int _field1_ = 0;
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
	}
	
	public static void serialize(final com.jsoniter.demo.object_with_5_fields.TestObject self, final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_5_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}
		
			if (self.field3 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field3\":", 9);
				sw.writeString(self.field3);
			}
		
			if (self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				sw.writeString(self.field2);
			}
		
			if (self.field5 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field5\":", 9);
				sw.writeString(self.field5);
			}
		
			if (self.field4 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field4\":", 9);
				sw.writeString(self.field4);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_5_fields.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}
		
			
			if (self.field3 != null) {
				sw.writeAscii(",\"field3\":", 10);
				sw.writeString(self.field3);
			} else {
				sw.writeAscii(",\"field3\":null", 14);
			}
		
			
			if (self.field2 != null) {
				sw.writeAscii(",\"field2\":", 10);
				sw.writeString(self.field2);
			} else {
				sw.writeAscii(",\"field2\":null", 14);
			}
		
			
			if (self.field5 != null) {
				sw.writeAscii(",\"field5\":", 10);
				sw.writeString(self.field5);
			} else {
				sw.writeAscii(",\"field5\":null", 14);
			}
		
			
			if (self.field4 != null) {
				sw.writeAscii(",\"field4\":", 10);
				sw.writeString(self.field4);
			} else {
				sw.writeAscii(",\"field4\":null", 14);
			}
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_5_fields.TestObject> JSON_READER_struct5 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.object_with_5_fields.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_5_fields.TestObject read(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_5_fields.TestObject instance = new com.jsoniter.demo.object_with_5_fields.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_5_fields.TestObject deserializestruct5(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_5_fields.TestObject instance = new com.jsoniter.demo.object_with_5_fields.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_5_fields.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		String _field1_ = null;
		String _field3_ = null;
		String _field2_ = null;
		String _field5_ = null;
		String _field4_ = null;
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
					case 1178651196:
						_field3_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = com.dslplatform.json.StringConverter.deserialize(reader);
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
					case 1178651196:
						_field3_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = com.dslplatform.json.StringConverter.deserialize(reader);
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
		instance.field3 = _field3_;
		instance.field2 = _field2_;
		instance.field5 = _field5_;
		instance.field4 = _field4_;
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
		
		final java.util.List<String> _tmp_field2_ = self.field2;
		if(_tmp_field2_ != null && _tmp_field2_.size() != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"field2\":[", 10);
			com.dslplatform.json.StringConverter.serializeNullable(_tmp_field2_.get(0), sw);
			for(int i = 1; i < _tmp_field2_.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
				com.dslplatform.json.StringConverter.serializeNullable(_tmp_field2_.get(i), sw);
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
		else if(self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"field2\":[]", 11);
		}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.ObjectOutput.TestObject self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}
		
		final java.util.List<String> _tmp_field2_ = self.field2;
		if(_tmp_field2_ != null && _tmp_field2_.size() != 0) {
			sw.writeAscii(",\"field2\":[", 11);
			com.dslplatform.json.StringConverter.serializeNullable(_tmp_field2_.get(0), sw);
			for(int i = 1; i < _tmp_field2_.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
				com.dslplatform.json.StringConverter.serializeNullable(_tmp_field2_.get(i), sw);
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
		else if(self.field2 != null) sw.writeAscii(",\"field2\":[]", 12);
		else sw.writeAscii(",\"field2\":null", 14);
	}

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.ObjectOutput.TestObject> JSON_READER_struct6 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.ObjectOutput.TestObject>() {
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
	static com.jsoniter.demo.ObjectOutput.TestObject deserializestruct6(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.ObjectOutput.TestObject instance = new com.jsoniter.demo.ObjectOutput.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.ObjectOutput.TestObject instance, final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
		
		String _field1_ = null;
		java.util.List<String> _field2_ = null;
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
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							java.util.List<String> __res = com.dslplatform.json.StringConverter.deserializeNullableCollection(reader);
							_field2_ = __res;
						}
						nextToken = reader.getNextToken();
					}
					else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
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
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							java.util.List<String> __res = com.dslplatform.json.StringConverter.deserializeNullableCollection(reader);
							_field2_ = __res;
						}
						nextToken = reader.getNextToken();
					}
					else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
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

	public static final com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.SimpleObjectBinding.TestObject> JSON_READER_struct0 = new com.dslplatform.json.JsonReader.ReadObject<com.jsoniter.demo.SimpleObjectBinding.TestObject>() {
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
	static com.jsoniter.demo.SimpleObjectBinding.TestObject deserializestruct0(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
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
