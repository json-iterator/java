# json iterator (jsoniter)

faster than DOM, more usable than SAX/StAX. Join us [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/json-iterator/Lobby)

This library also has a golang version, with same api and performance: https://github.com/json-iterator/go

# Benchmark (databind)

|       | jsoniter    | dsljson     | fastjson   | gson       | jackson     |
| ---   | ---         | ---         | ---        | ---        | ---         |
| 10kb  | 5791477.392 | 1825251.497 | 528568.402 | 509073.118 | 1002068.202 |
| 100kb | 64452.613   | 19128.133   | 8612.036   | 6303.252   | 12011.405   |

10x faster than fastjson, 3x faster than dsljson

# Example Usage

Give me json and class, give back the object bind

```
Jsoniter iter = Jsoniter.parseString("[1,2,3]");
int[] val = iter.read(int[].class);
assertArrayEquals(new int[]{1, 2, 3}, val);
```