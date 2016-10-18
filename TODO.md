* Rename `HyperbolicTotalVariation` as `EdgePreservingSmoothness`.

* Optimize methods like `sum`, `increment`, `scan` for multi-dimensional arrays
  when `getdata()` returns non-null.

* mitiv.io.BufferedImage: most methods here are deprecated.

* Add support for `boolean` arrays.

* Decide that `byte` are interpreted as being unsigned (there is no real use of
  `signed byte` in image processing and having both `signed` and `unsigned`
  version of integers is not worth the added complexity, and bugs), the
  cleanest way to do that is to change the `get()` method for `byte` arrays.
  Side effects: MDA format, etc.  Note that this is similar to how is
  inetrpreted the `TYPE_BYTE` in `DataBuffer` objects.

* Use `ImageWriteParam` and `ImageReadParam` to improve reading/writing of
  images.  This can be set in `FormatOptions`.

* Deal with `NaN` and `Infinite` when converting arrays to images.

* Implement broadcasting rules for `assign`.

* Deprecate type conversions via the `ArrayFactory`,
  e.g. `ArrayFactory.toByte(arr)` in favor of `arr.toByte()`.

* Cleanup unused or poorly designed code: `ColorMap.java`, `MathUtils.java`,
  `mitivCLI.java`, `NavigableImagePanel.java`, `CommonUtils.java`...

* Move `mitiv.array.ArrayUtils.java` into `mitiv.utils.ArrayUtils.java`.

* Use varargs, e.g. `int... dims` to simplify the code of, e.g. `Shape.java`,
  and suppress the needs of TPP in some cases.

* Support CFitsIO syntax for specifying a particular HDU or extension and a
  ROI: `filename.fits[HDU][RNG1,RNG2,...]` where `HDU` is the HDU number or
  extension name (automatically trimmed and converted to upper-case letters)
  and `RNGn` are ranges (possibly `*` or `:` for all).

* Use `WeightedData` class in `WeightedConvolution`.

* In `FlatArray.javax` and `StriddenArray.javax` there is no checking nor
  wrapping of the slicing index (contrarily to the `dim` arg).

* Many operations such as slice, view, etc. could have their args tested at an
  higher level and a low level, e.g. _slice, method is then called whith
  different implementations.

* Make `Flat{Double,Float}*` and `ShapedVector` more intricate?

* `space.create()` or `vec.similar()` to create a new vector.

* Automatically build JavaDoc.

* Rename toplevel package as `tipi`.
