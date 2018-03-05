# 0.9.22

* fix #167 parse Object.class follow jackson convention. fixed the case of 1.0 parsed as int not double.
* fix #154 support map integer key
* fix #152

# 0.9.21

* fix #149 parse Object.class follow jackson convention
* fix #145 add Any.registerEncoders
* merge #143

# 0.9.20

* fix #136, field with only getter is also considered as java bean property, so that @JsonIgnore on the field should be propagated to getter 

# 0.9.19
* changed cfg class name to hashcode based
* fix static codegen
* fix #133 NPE when no extra
* fix #132 MaybeEmptyArrayDecoder
* fix #130 @JsonCreator not compatible with @JsonIgnore
* fix #126 surrogate unicode

# 0.9.18
* fix of overflow detection for numeric primitive types
* fix of method prefix of error message
* issue #125 avoid nested JsonException
* fix #109 treat wildcard generics variable as Object

# 0.9.17
* fix leading zero
* fix #112 #119
* fix of parsing zero & min values
* issue #115 better leading zero detection
* fix #144, parse max int/long
* fix #110 if @JsonProperty is marked on field, ignore getter/setter

# 0.9.16

* issue #107 annotation should be marked on getter/setter if present
* fix ctor is null when encoding issue
* issue #104, JsonWrapper argument should not be mandatory
* issue #99 added mustBeValid method to Any class
* issue #97 demonstrate JsonProperty when both field and setter
* like "1.0e+10" should not fail
* issue #94 skip transient field
* issue #94 fix JsonProperty not changing fromNames and toNames
* issue #93 some control character should be esacped specially
* issue #93 fix control character serialization
* issue #92 fix generics support

# 0.9.15

breaking changes

* `null` is not omitted by default config

new features

* add `defaultValueToOmit` to @JsonProperty
* add `omitDefaultValue` to config
* encoder support indention in dynamic mode