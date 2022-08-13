open module com.jsoniter {
	exports com.jsoniter.fuzzy;

	exports com.jsoniter.static_codegen;

	exports com.jsoniter.extra;

	exports com.jsoniter.output;

	exports com.jsoniter.annotation;

	exports com.jsoniter;

	exports com.jsoniter.spi;

	exports com.jsoniter.any;
	
	requires transitive com.fasterxml.jackson.annotation;

	requires transitive com.fasterxml.jackson.core;

	requires transitive com.fasterxml.jackson.databind;
	
	requires transitive javassist;

	requires transitive com.google.gson;

	requires transitive java.desktop;

}