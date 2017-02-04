/*
* Created by DSL Platform
* v1.7.6218.18384 
*/

package com.dslplatform.json;



public class ExternalSerialization implements Configuration {


	@SuppressWarnings("unchecked")
	public void configure(final DslJson json) {
		setup(json);
	}

	@SuppressWarnings("unchecked")
	public static void setup(final DslJson json) {


		json.registerReader(com.jsoniter.demo.object_with_1_double_field.TestObject.class, JSON_READER_struct5);
		json.registerWriter(com.jsoniter.demo.object_with_1_double_field.TestObject.class, new JsonWriter.WriteObject<com.jsoniter.demo.object_with_1_double_field.TestObject>() {
			@Override
			public void write(JsonWriter writer, com.jsoniter.demo.object_with_1_double_field.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});

		json.registerReader(com.jsoniter.demo.object_with_1_field.TestObject.class, JSON_READER_struct1);
		json.registerWriter(com.jsoniter.demo.object_with_1_field.TestObject.class, new JsonWriter.WriteObject<com.jsoniter.demo.object_with_1_field.TestObject>() {
			@Override
			public void write(JsonWriter writer, com.jsoniter.demo.object_with_1_field.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});

		json.registerReader(com.jsoniter.demo.object_with_10_fields.TestObject.class, JSON_READER_struct6);
		json.registerWriter(com.jsoniter.demo.object_with_10_fields.TestObject.class, new JsonWriter.WriteObject<com.jsoniter.demo.object_with_10_fields.TestObject>() {
			@Override
			public void write(JsonWriter writer, com.jsoniter.demo.object_with_10_fields.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});

		json.registerReader(com.jsoniter.demo.object_with_1_int_field.TestObject.class, JSON_READER_struct0);
		json.registerWriter(com.jsoniter.demo.object_with_1_int_field.TestObject.class, new JsonWriter.WriteObject<com.jsoniter.demo.object_with_1_int_field.TestObject>() {
			@Override
			public void write(JsonWriter writer, com.jsoniter.demo.object_with_1_int_field.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});

		json.registerReader(com.jsoniter.demo.object_with_5_fields.TestObject.class, JSON_READER_struct2);
		json.registerWriter(com.jsoniter.demo.object_with_5_fields.TestObject.class, new JsonWriter.WriteObject<com.jsoniter.demo.object_with_5_fields.TestObject>() {
			@Override
			public void write(JsonWriter writer, com.jsoniter.demo.object_with_5_fields.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});

		json.registerReader(com.jsoniter.demo.object_with_15_fields.TestObject.class, JSON_READER_struct4);
		json.registerWriter(com.jsoniter.demo.object_with_15_fields.TestObject.class, new JsonWriter.WriteObject<com.jsoniter.demo.object_with_15_fields.TestObject>() {
			@Override
			public void write(JsonWriter writer, com.jsoniter.demo.object_with_15_fields.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});

		json.registerReader(com.jsoniter.demo.SimpleObjectBinding.TestObject.class, JSON_READER_struct3);
		json.registerWriter(com.jsoniter.demo.SimpleObjectBinding.TestObject.class, new JsonWriter.WriteObject<com.jsoniter.demo.SimpleObjectBinding.TestObject>() {
			@Override
			public void write(JsonWriter writer, com.jsoniter.demo.SimpleObjectBinding.TestObject value) {
				serialize(value, writer, json.omitDefaults);
			}
		});
	}

	public static void serialize(final com.jsoniter.demo.object_with_1_double_field.TestObject self, final JsonWriter sw, final boolean minimal) {
		sw.writeByte(JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_1_double_field.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {


			if (self.field1 != 0.0) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				NumberConverter.serialize(self.field1, sw);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_1_double_field.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {



			sw.writeAscii("\"field1\":", 9);
			NumberConverter.serialize(self.field1, sw);
	}

	public static final JsonReader.ReadObject<com.jsoniter.demo.object_with_1_double_field.TestObject> JSON_READER_struct5 = new JsonReader.ReadObject<com.jsoniter.demo.object_with_1_double_field.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_1_double_field.TestObject read(final JsonReader reader) throws java.io.IOException {
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
	static com.jsoniter.demo.object_with_1_double_field.TestObject deserializestruct5(final JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_1_double_field.TestObject instance = new com.jsoniter.demo.object_with_1_double_field.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_1_double_field.TestObject instance, final JsonReader reader) throws java.io.IOException {

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
						_field1_ = NumberConverter.deserializeDouble(reader);
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
						_field1_ = NumberConverter.deserializeDouble(reader);
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

	public static void serialize(final com.jsoniter.demo.object_with_1_field.TestObject self, final JsonWriter sw, final boolean minimal) {
		sw.writeByte(JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_1_field.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {


			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_1_field.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {



			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}
	}

	public static final JsonReader.ReadObject<com.jsoniter.demo.object_with_1_field.TestObject> JSON_READER_struct1 = new JsonReader.ReadObject<com.jsoniter.demo.object_with_1_field.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_1_field.TestObject read(final JsonReader reader) throws java.io.IOException {
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
	static com.jsoniter.demo.object_with_1_field.TestObject deserializestruct1(final JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_1_field.TestObject instance = new com.jsoniter.demo.object_with_1_field.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_1_field.TestObject instance, final JsonReader reader) throws java.io.IOException {

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
						_field1_ = StringConverter.deserialize(reader);
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
						_field1_ = StringConverter.deserialize(reader);
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

	public static void serialize(final com.jsoniter.demo.object_with_10_fields.TestObject self, final JsonWriter sw, final boolean minimal) {
		sw.writeByte(JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_10_fields.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {


			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}

			if (self.field10 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field10\":", 10);
				sw.writeString(self.field10);
			}

			if (self.field7 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field7\":", 9);
				sw.writeString(self.field7);
			}

			if (self.field6 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field6\":", 9);
				sw.writeString(self.field6);
			}

			if (self.field9 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field9\":", 9);
				sw.writeString(self.field9);
			}

			if (self.field8 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field8\":", 9);
				sw.writeString(self.field8);
			}

			if (self.field3 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field3\":", 9);
				sw.writeString(self.field3);
			}

			if (self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				sw.writeString(self.field2);
			}

			if (self.field5 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field5\":", 9);
				sw.writeString(self.field5);
			}

			if (self.field4 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field4\":", 9);
				sw.writeString(self.field4);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_10_fields.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {



			if (self.field1 != null) {
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii("\"field1\":null", 13);
			}


			if (self.field10 != null) {
				sw.writeAscii(",\"field10\":", 11);
				sw.writeString(self.field10);
			} else {
				sw.writeAscii(",\"field10\":null", 15);
			}


			if (self.field7 != null) {
				sw.writeAscii(",\"field7\":", 10);
				sw.writeString(self.field7);
			} else {
				sw.writeAscii(",\"field7\":null", 14);
			}


			if (self.field6 != null) {
				sw.writeAscii(",\"field6\":", 10);
				sw.writeString(self.field6);
			} else {
				sw.writeAscii(",\"field6\":null", 14);
			}


			if (self.field9 != null) {
				sw.writeAscii(",\"field9\":", 10);
				sw.writeString(self.field9);
			} else {
				sw.writeAscii(",\"field9\":null", 14);
			}


			if (self.field8 != null) {
				sw.writeAscii(",\"field8\":", 10);
				sw.writeString(self.field8);
			} else {
				sw.writeAscii(",\"field8\":null", 14);
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

	public static final JsonReader.ReadObject<com.jsoniter.demo.object_with_10_fields.TestObject> JSON_READER_struct6 = new JsonReader.ReadObject<com.jsoniter.demo.object_with_10_fields.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_10_fields.TestObject read(final JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_10_fields.TestObject instance = new com.jsoniter.demo.object_with_10_fields.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_10_fields.TestObject deserializestruct6(final JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_10_fields.TestObject instance = new com.jsoniter.demo.object_with_10_fields.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static void deserialize(final com.jsoniter.demo.object_with_10_fields.TestObject instance, final JsonReader reader) throws java.io.IOException {

		String _field1_ = null;
		String _field10_ = null;
		String _field7_ = null;
		String _field6_ = null;
		String _field9_ = null;
		String _field8_ = null;
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
						_field1_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 268646422:
						_field10_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1111540720:
						_field7_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1128318339:
						_field6_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1346427386:
						_field9_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1363205005:
						_field8_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1178651196:
						_field3_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = StringConverter.deserialize(reader);
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
						_field1_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 268646422:
						_field10_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1111540720:
						_field7_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1128318339:
						_field6_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1346427386:
						_field9_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1363205005:
						_field8_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1178651196:
						_field3_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = StringConverter.deserialize(reader);
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
		instance.field10 = _field10_;
		instance.field7 = _field7_;
		instance.field6 = _field6_;
		instance.field9 = _field9_;
		instance.field8 = _field8_;
		instance.field3 = _field3_;
		instance.field2 = _field2_;
		instance.field5 = _field5_;
		instance.field4 = _field4_;
	}

	public static void serialize(final com.jsoniter.demo.object_with_1_int_field.TestObject self, final JsonWriter sw, final boolean minimal) {
		sw.writeByte(JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_1_int_field.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {


			if (self.field1 != 0) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				NumberConverter.serialize(self.field1, sw);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_1_int_field.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {



			sw.writeAscii("\"field1\":", 9);
			NumberConverter.serialize(self.field1, sw);
	}

	public static final JsonReader.ReadObject<com.jsoniter.demo.object_with_1_int_field.TestObject> JSON_READER_struct0 = new JsonReader.ReadObject<com.jsoniter.demo.object_with_1_int_field.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_1_int_field.TestObject read(final JsonReader reader) throws java.io.IOException {
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
	static com.jsoniter.demo.object_with_1_int_field.TestObject deserializestruct0(final JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_1_int_field.TestObject instance = new com.jsoniter.demo.object_with_1_int_field.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.object_with_1_int_field.TestObject instance, final JsonReader reader) throws java.io.IOException {

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
						_field1_ = NumberConverter.deserializeInt(reader);
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
						_field1_ = NumberConverter.deserializeInt(reader);
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

	public static void serialize(final com.jsoniter.demo.object_with_5_fields.TestObject self, final JsonWriter sw, final boolean minimal) {
		sw.writeByte(JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_5_fields.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {


			if (self.field1 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}

			if (self.field3 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field3\":", 9);
				sw.writeString(self.field3);
			}

			if (self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				sw.writeString(self.field2);
			}

			if (self.field5 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field5\":", 9);
				sw.writeString(self.field5);
			}

			if (self.field4 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field4\":", 9);
				sw.writeString(self.field4);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_5_fields.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {



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

	public static final JsonReader.ReadObject<com.jsoniter.demo.object_with_5_fields.TestObject> JSON_READER_struct2 = new JsonReader.ReadObject<com.jsoniter.demo.object_with_5_fields.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_5_fields.TestObject read(final JsonReader reader) throws java.io.IOException {
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
	static com.jsoniter.demo.object_with_5_fields.TestObject deserializestruct2(final JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_5_fields.TestObject instance = new com.jsoniter.demo.object_with_5_fields.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	public static void deserialize(final com.jsoniter.demo.object_with_5_fields.TestObject instance, final JsonReader reader) throws java.io.IOException {

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
						_field1_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1178651196:
						_field3_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = StringConverter.deserialize(reader);
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
						_field1_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1178651196:
						_field3_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = StringConverter.deserialize(reader);
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

	public static void serialize(final com.jsoniter.demo.object_with_15_fields.TestObject self, final JsonWriter sw, final boolean minimal) {
		sw.writeByte(JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.object_with_15_fields.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {


			if (self.field11 != null) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field11\":", 10);
				sw.writeString(self.field11);
			}

			if (self.field12 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field12\":", 10);
				sw.writeString(self.field12);
			}

			if (self.field1 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				sw.writeString(self.field1);
			}

			if (self.field10 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field10\":", 10);
				sw.writeString(self.field10);
			}

			if (self.field15 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field15\":", 10);
				sw.writeString(self.field15);
			}

			if (self.field13 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field13\":", 10);
				sw.writeString(self.field13);
			}

			if (self.field14 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field14\":", 10);
				sw.writeString(self.field14);
			}

			if (self.field7 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field7\":", 9);
				sw.writeString(self.field7);
			}

			if (self.field6 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field6\":", 9);
				sw.writeString(self.field6);
			}

			if (self.field9 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field9\":", 9);
				sw.writeString(self.field9);
			}

			if (self.field8 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field8\":", 9);
				sw.writeString(self.field8);
			}

			if (self.field3 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field3\":", 9);
				sw.writeString(self.field3);
			}

			if (self.field2 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				sw.writeString(self.field2);
			}

			if (self.field5 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field5\":", 9);
				sw.writeString(self.field5);
			}

			if (self.field4 != null) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field4\":", 9);
				sw.writeString(self.field4);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.object_with_15_fields.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {



			if (self.field11 != null) {
				sw.writeAscii("\"field11\":", 10);
				sw.writeString(self.field11);
			} else {
				sw.writeAscii("\"field11\":null", 14);
			}


			if (self.field12 != null) {
				sw.writeAscii(",\"field12\":", 11);
				sw.writeString(self.field12);
			} else {
				sw.writeAscii(",\"field12\":null", 15);
			}


			if (self.field1 != null) {
				sw.writeAscii(",\"field1\":", 10);
				sw.writeString(self.field1);
			} else {
				sw.writeAscii(",\"field1\":null", 14);
			}


			if (self.field10 != null) {
				sw.writeAscii(",\"field10\":", 11);
				sw.writeString(self.field10);
			} else {
				sw.writeAscii(",\"field10\":null", 15);
			}


			if (self.field15 != null) {
				sw.writeAscii(",\"field15\":", 11);
				sw.writeString(self.field15);
			} else {
				sw.writeAscii(",\"field15\":null", 15);
			}


			if (self.field13 != null) {
				sw.writeAscii(",\"field13\":", 11);
				sw.writeString(self.field13);
			} else {
				sw.writeAscii(",\"field13\":null", 15);
			}


			if (self.field14 != null) {
				sw.writeAscii(",\"field14\":", 11);
				sw.writeString(self.field14);
			} else {
				sw.writeAscii(",\"field14\":null", 15);
			}


			if (self.field7 != null) {
				sw.writeAscii(",\"field7\":", 10);
				sw.writeString(self.field7);
			} else {
				sw.writeAscii(",\"field7\":null", 14);
			}


			if (self.field6 != null) {
				sw.writeAscii(",\"field6\":", 10);
				sw.writeString(self.field6);
			} else {
				sw.writeAscii(",\"field6\":null", 14);
			}


			if (self.field9 != null) {
				sw.writeAscii(",\"field9\":", 10);
				sw.writeString(self.field9);
			} else {
				sw.writeAscii(",\"field9\":null", 14);
			}


			if (self.field8 != null) {
				sw.writeAscii(",\"field8\":", 10);
				sw.writeString(self.field8);
			} else {
				sw.writeAscii(",\"field8\":null", 14);
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

	public static final JsonReader.ReadObject<com.jsoniter.demo.object_with_15_fields.TestObject> JSON_READER_struct4 = new JsonReader.ReadObject<com.jsoniter.demo.object_with_15_fields.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.object_with_15_fields.TestObject read(final JsonReader reader) throws java.io.IOException {
			if(reader.last() != '{') {
				throw new java.io.IOException("Expecting \'{\' at position " + reader.positionInStream() + ". Found " + (char)reader.last());
			}
			reader.getNextToken();
			final com.jsoniter.demo.object_with_15_fields.TestObject instance = new com.jsoniter.demo.object_with_15_fields.TestObject();
			deserialize(instance, reader);
			return instance;
		}
	};

	@SuppressWarnings("unchecked")
	static com.jsoniter.demo.object_with_15_fields.TestObject deserializestruct4(final JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.object_with_15_fields.TestObject instance = new com.jsoniter.demo.object_with_15_fields.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
    public static void deserialize(final com.jsoniter.demo.object_with_15_fields.TestObject instance, final JsonReader reader) throws java.io.IOException {

		String _field11_ = null;
		String _field12_ = null;
		String _field1_ = null;
		String _field10_ = null;
		String _field15_ = null;
		String _field13_ = null;
		String _field14_ = null;
		String _field7_ = null;
		String _field6_ = null;
		String _field9_ = null;
		String _field8_ = null;
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

					case 285424041:
						_field11_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 235091184:
						_field12_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1212206434:
						_field1_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 268646422:
						_field10_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 352534517:
						_field15_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 251868803:
						_field13_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 335756898:
						_field14_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1111540720:
						_field7_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1128318339:
						_field6_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1346427386:
						_field9_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1363205005:
						_field8_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1178651196:
						_field3_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = StringConverter.deserialize(reader);
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

					case 285424041:
						_field11_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 235091184:
						_field12_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1212206434:
						_field1_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 268646422:
						_field10_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 352534517:
						_field15_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 251868803:
						_field13_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 335756898:
						_field14_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1111540720:
						_field7_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1128318339:
						_field6_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1346427386:
						_field9_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1363205005:
						_field8_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1178651196:
						_field3_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1145095958:
						_field5_ = StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 1161873577:
						_field4_ = StringConverter.deserialize(reader);
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

		instance.field11 = _field11_;
		instance.field12 = _field12_;
		instance.field1 = _field1_;
		instance.field10 = _field10_;
		instance.field15 = _field15_;
		instance.field13 = _field13_;
		instance.field14 = _field14_;
		instance.field7 = _field7_;
		instance.field6 = _field6_;
		instance.field9 = _field9_;
		instance.field8 = _field8_;
		instance.field3 = _field3_;
		instance.field2 = _field2_;
		instance.field5 = _field5_;
		instance.field4 = _field4_;
	}

	public static void serialize(final com.jsoniter.demo.SimpleObjectBinding.TestObject self, final JsonWriter sw, final boolean minimal) {
		sw.writeByte(JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(self, sw, false);
		} else {
			__serializeJsonObjectFull(self, sw, false);
		}
		sw.writeByte(JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final com.jsoniter.demo.SimpleObjectBinding.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {


			if (self.field1 != 0) {
			hasWrittenProperty = true;
				sw.writeAscii("\"field1\":", 9);
				NumberConverter.serialize(self.field1, sw);
			}

			if (self.field2 != 0) {
			if(hasWrittenProperty) sw.writeByte(JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"field2\":", 9);
				NumberConverter.serialize(self.field2, sw);
			}
	}

	static void __serializeJsonObjectFull(final com.jsoniter.demo.SimpleObjectBinding.TestObject self, JsonWriter sw, boolean hasWrittenProperty) {



			sw.writeAscii("\"field1\":", 9);
			NumberConverter.serialize(self.field1, sw);


			sw.writeAscii(",\"field2\":", 10);
			NumberConverter.serialize(self.field2, sw);
	}

	public static final JsonReader.ReadObject<com.jsoniter.demo.SimpleObjectBinding.TestObject> JSON_READER_struct3 = new JsonReader.ReadObject<com.jsoniter.demo.SimpleObjectBinding.TestObject>() {
		@SuppressWarnings("unchecked")
		@Override
		public com.jsoniter.demo.SimpleObjectBinding.TestObject read(final JsonReader reader) throws java.io.IOException {
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
	static com.jsoniter.demo.SimpleObjectBinding.TestObject deserializestruct3(final JsonReader reader) throws java.io.IOException {
		final com.jsoniter.demo.SimpleObjectBinding.TestObject instance = new com.jsoniter.demo.SimpleObjectBinding.TestObject();
		deserialize(instance, reader);
		return instance;
	}

	@SuppressWarnings("unchecked")
	static void deserialize(final com.jsoniter.demo.SimpleObjectBinding.TestObject instance, final JsonReader reader) throws java.io.IOException {

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
						_field1_ = NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = NumberConverter.deserializeInt(reader);
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
						_field1_ = NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case 1195428815:
						_field2_ = NumberConverter.deserializeInt(reader);
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
