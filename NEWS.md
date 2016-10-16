* 2016/../..

  - Deconvolution works in single/double precision.
  - Deconvolution no longer zero-pad the data.
  - Improved line search parameters.
  - "Step changed" bug fixed.
  - Add `isFlat` method for checking whether the array is already in a
    *flat* form.  Methods `flatten()` and `isFlat()` are more consistent.
  - Speed up deconvolution by not zero-padding the data (but still use a
    larger object space to avoid border artifacts).

* 2014/11/04

  Release 0.1.1

* 2014/11/04

  Release 0.1.0
