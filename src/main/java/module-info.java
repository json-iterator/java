open module com.jsoniter {
	
	exports com.jsoniter;

	exports com.jsoniter.fuzzy;

	exports com.jsoniter.static_codegen;

	exports com.jsoniter.extra;

	exports com.jsoniter.output;

	exports com.jsoniter.annotation;

	exports com.jsoniter.spi;

	exports com.jsoniter.any;


	/** static, because marked as optional in pom.xml*/
	requires static javassist;

	/** static, because marked as optional in pom.xml*/
	requires static com.fasterxml.jackson.core;

	/** static, because marked as optional in pom.xml*/
	requires static com.fasterxml.jackson.annotation;

	/** static, because marked as optional in pom.xml*/
	requires static com.fasterxml.jackson.databind;
	
	/** static, because marked as optional in pom.xml*/
	requires static com.google.gson;

	/** static, because only used in testing */
	requires static java.desktop;

}