jsoniter (json-iterator) is fast and flexible JSON parser available in [Java](https://github.com/json-iterator/java) and [Go](https://github.com/json-iterator/go)

# Why jsoniter?

* Jsoniter is the fastest JSON parser. It could be up to 10x faster than normal parser, data binding included. Shameless self [benchmark](http://jsoniter.com/benchmark.html)
* Extremely flexible api. You can mix and match three different styles: bind-api, any-api or iterator-api. Checkout your [api choices](http://jsoniter.com/api.html)

# Show off

Here is a quick show off, for more complete report you can checkout the full [benchmark](http://jsoniter.com/benchmark.html) with [in-depth optimization](http://jsoniter.com/benchmark.html#optimization-used) to back the numbers up

![java1](http://jsoniter.com/benchmarks/java1.png)

# Bind-API is the best

Bind-api should always be the first choice. Given this JSON document `[0,1,2,3]`

Parse with Java bind-api

```java
import com.jsoniter.JsonIterator;
JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
int[] val = iter.read(int[].class);
System.out.println(val[3]);
```

# Iterator-API for quick extraction

When you do not need to get all the data back, just extract some.

Parse with Java iterator-api

```java
import com.jsoniter.JsonIterator;
JsonIterator iter = JsonIterator.parse("[0, [1, 2], [3, 4], 5]");
int count = 0;
while(iter.readArray()) {
    iter.skip();
    count++;
}
System.out.println(count); // 4
```

# Any-API for maximum flexibility

Parse with Java any-api

```java
import com.jsoniter.JsonIterator;
JsonIterator iter = JsonIterator.parse("[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"'));
Any val = iter.readAny();
System.out.println(val.toInt(1, "field2")); // 22
```

Notice you can extract from nested data structure, and convert any type to the type to you want.

# How to get

```
<dependency>
    <groupId>com.jsoniter</groupId>
    <artifactId>jsoniter</artifactId>
    <version>0.9.4</version>
</dependency>
```

# Contribution Welcomed !

Report issue or pull request, or email taowen@gmail.com, or [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/json-iterator/Lobby)
