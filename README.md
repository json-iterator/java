# json iterator (jsoniter)

faster than DOM, more usable than SAX/StAX. Join us [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/json-iterator/Lobby)

# Benchmark (databind)

| jsoniter    | dsljson     | fastjson   | gson       | jackson     |
| ---         | ---         | ---        | ---        | ---         |
| 5791477.392 | 1825251.497 | 528568.402 | 509073.118 | 1002068.202 |

10x faster than fastjson, 3x faster than dsljson

# Example Usage

Give me json and class, give back the object bind

```
Jsoniter iter = Jsoniter.parseString("[1,2,3]");
int[] val = iter.read(int[].class);
assertArrayEquals(new int[]{1, 2, 3}, val);
```