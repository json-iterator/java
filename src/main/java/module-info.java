open module com.jsoniter {
	exports com.jsoniter.fuzzy;

	exports com.jsoniter.static_codegen;

	exports com.jsoniter.extra;

	exports com.jsoniter.output;

	exports com.jsoniter.annotation;

	exports com.jsoniter;

	exports com.jsoniter.spi;

	exports com.jsoniter.any;

	requires com.fasterxml.jackson.annotation;

	requires com.fasterxml.jackson.core;

	requires com.fasterxml.jackson.databind;

	requires com.google.gson;

	requires java.desktop;

	requires javassist;
}