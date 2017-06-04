there are 7 packages, listed in abstraction level order

# bottom abstraction

* spi: bottom of the abstraction

# concrete implementations

* iterator/any: these two packages are tangled together, doing decoding
* output: doing encoding, should only depend on spi

# addons

* annotation: make spi accessible with annotation. everything here can be done using code
* fuzzy: pre-defined decoders to work with messy input
* extra: extra encoders/decoders, useful for a lot of people, but not all of them
* static_codegen: command to generate encoder/decoder statically