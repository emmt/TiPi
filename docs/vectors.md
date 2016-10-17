# Vectors in TiPi

**Warning:** This page needs to be edited to update its contents.  The
presented general ideas are however correct.


## Required Operations in a Vector Space

* create a new vector
* copy vector contents from/to conventional memory<sup>(a)</sup>
* fetch/set the value of a specific element<sup>(a)</sup>
* fill a vector with a value
* copy (duplicate) a vector
* compute the dot product of two vectors
* copy vector contents to another vector
* swap the contents of two vectors
* scale a vector by a scalar
* compute linear combinations of 2 or 3 vectors
* compute the norm(s) of a vector

<sup>(a)</sup> These operations are not guaranteed to be efficient, their usage
  should be limited, for instance to set the inputs of an algorithm and to
  recover its result.

Example of methods provided by a vector space:

* Method `create(alpha)` creates a new vector of the vector space with all
  components set to the given value `alpha`.


## Basic Operations on Vectors

To create a shaped-vector from the contents of the array

    space.create(arr)

where `arr` can be: a `ShapedArray` (with same shape, automatically converted
to the correct type), a flat Java vector (with the same number of elements,
type is automatically converted).  To spare memory, the result may be a
wrapping of the source and thus may share its contents with it, but there is no
guarantee of that.  To make sure that the two object have independent contents,
use:

    space.create(arr, forceCopy)

with *forceCopy* set to `true`.

To convert to a flat Java vector (of known element type) do one of:

    vec.flatten()
    vec.flatten(forceCopy)

Thus a shaped array may be reformed by one of:

    ShapedArray arr = ShapedArray.create(vec);
    ShapedArray arr = ShapedArray.wrap(vec.flatten(), vec.cloneShape(), false);

with variants:

    FloatArray fltArr = FloatArray.createFrom(fltvec);
    DoubleArray dblArr = DoubleArray.createFrom(dblVec);

assuming:

    FloatShapedVector fltVec;
    DoubleShapedVector dblVec;

{==So there must be some copyFrom/copyTo for ShapedArray and ShapedVector
which are optimized if the contents are the same...==}

Assuming that `arr` is a `ShapedArray`, the reason to distinguish:

    vec = space.create(arr);

and

    vec = space.create();
    vec.assign(arr);

is that, although the two fragments of code result in a vector with the same
contents as the array, the first fragment of code may have a chance to avoid
duplicating memory, while the second yields independent contents.  In fact, the
second fragment of code is equivalent to:

    vec = space.create(arr, true); // force copy

Since we want to be able to store vectors in non-conventional memory
(e.g. GPU), there are no guaranteed means to share contents between an array
and a vector.  Thus, to recover the contents of a vector after some
computations, you must call, either:

    arr.assign(vec);

to make sure the contents of the existing array match that of the
vector, or:

    arr = ShapedArray.create(vec);

which creates a new array from a shaped vector.  These operations are
optimized, so there is a chance that they costs almost nothing (if the existing
array and the vector share their contents or if the vector contents can be
wrapped into an array), however this cannot be guaranteed.

