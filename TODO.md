* For typical inverse problems where `f(x) = fdata(x) + µ fprior(x)`, set
  convergebce criterion to be `‖∇f(x)‖ ≪ max(‖∇fdata(x)‖, |µ|⋅‖∇fprior(x)‖)`.

* Rename `HyperbolicTotalVariation` as `EdgePreservingSmoothness`.

* Force a cubic step for the initial step or after a restart in LBFGS.

* Optimize methods like `sum`, `increment`, `scan` for multi-dimensional arrays
  when `getdata()` returns non-null.

* mitiv.io.BufferedImage: most methods here are deprecated.

* Add support for `boolean` arrays.

* Have public methods of `CostFunction` and descendants check the arguments and
  short-circuit if `alpha = 0`, not the low level implementations.

* Decide that `byte` are interpreted as being unsigned (there is no real use of
  `signed byte` in image processing and having both `signed` and `unsigned`
  version of integers is not worth the added complexity, and bugs), the
  cleanest way to do that is to change the `get()` method for `byte` arrays.
  Side effects: MDA format, etc.  Note that this is similar to how is
  interpreted the `TYPE_BYTE` in `DataBuffer` objects. **ALMOST DONE**

* Optimize methods for flat arrays as for the conversion routines.

* Use `ImageWriteParam` and `ImageReadParam` to improve reading/writing of
  images.  This can be set in `FormatOptions`.

* Deal with `NaN` and `Infinite` when converting arrays to images.  In
  particular with the initial image.

* Implement broadcasting rules for `assign`.

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

* Many operations such as `slice`, `view`, etc. could have their args tested at
  an higher level and a low level, e.g. `_slice`, method is then called whith
  different implementations.

* In the cost functions, `evaluate` and `computeCostAndGradient` should check
  arguments?  Otherwise, make them low level (with an underscore).

* Make `Flat{Double,Float}*` and `ShapedVector` more intricate?

* `space.create()` or `vec.similar()` to create a new vector.

* Automatically build JavaDoc.

* Rename toplevel package as `tipi`.
