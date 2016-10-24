* 2016/../..

  - Deconvolution works in single/double precision.

  - Deconvolution no longer zero-pad the data.

  - Improved line search parameters.

  - "Step changed" bug fixed.

  - Add `isFlat` method for checking whether the array is already in a
    *flat* form.  Methods `flatten()` and `isFlat()` are more consistent.
    Some methods has been optimized to account for flat arrays.

  - Speed up deconvolution by not zero-padding the data (but still use a
    larger object space to avoid border artifacts).

  - Duplicated methods between `VectorSpace` and `Vector` have been removed.
    the low level methods are kept in the `VectorSpace` class and all high
    level methods which involve at least a vector are now provided in the
    `Vector` class.  This yields more readable and consistent code.

  - `byte` type in shaped arrays is assumed to be unsigned as it makes more
    sense for image processing.  This involves expressions like `(b & 0xFF)`
    when using byte value `b`.

  - Input/output of FITS files.

  - Wrapping of `Object` as `ShapedArray`.


* 2014/11/04

  Release 0.1.1

* 2014/11/04

  Release 0.1.0
