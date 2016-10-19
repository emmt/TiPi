# Using vectors in TiPi

**Warning:** This page needs to be edited to update its contents.  The
presented general ideas are however correct.


# Operations involving vector spaces

Vectors can be created by their vector space with either undefined contents of
with their components set to given values.  For instance, to create a new
vector of the vector space `vsp`:

```java
vsp.create()    // yields a new vector with undefined contents
vsp.create(val) // yields a new vector with all components set to `val`
vsp.one()       // is equivalent to space.create(1)
vsp.zero()      // is equivalent to space.create(0)
```

To check whether a vector `vec` belongs to the vector space `vsp`, the
following expressions can be used:

```java
vsp.owns(vec)
vec.belongsTo(vsp)
vec.getOwner() == vsp
```

The call:

```java
vsp.check(vec)
```

checks whether vector `vec` belongs to the vector space `vsp` and throws an
`IncorrectSpaceException` exception otherwise.


## Methods implemented by vectors

Many methods are available to directly manipulate vectors.  These methods are
sufficient to implement the iterative optimization algorithms provided by TiPi.


The following methods are assuemd to be efficient:

* `vec.create()` yields a new vector of the same vector space as `vec`
  with undefined contents.

* `vec.clone()` yields a new vector of the same vector space as `vec` and with
  all its components set to the corresponding values of the components of
  `vec`.

* `dst.copy(src)` copies the values of the components of vector `src` into
  vector `dst`.

* `vec.norm2()` yields the Euclidean norm of `vec` which is the square root of
  the sum of the squared value of its components.

* `vec.norm1()` yields the L1 norm of `vec` which is the sum of the absolute
  value of its components.

* `vec.norm2()` yields the infinite norm of `vec` which is the maximum absolute
  value of its components.

* `x.dot(y)` yields the dot product of vectors `x` and `y`.

* `w.dot(x,y)` yields the dot product of vectors `x` and `y` weighted by `w`.
  This dot product can be expressed as `x'.W.y` where `W = diag(w)` is a
  diagonal weighting operator.

* `x.swap(y)` exchanges the values of the corresponding components of vectors
  `x` and `y`.

* `vec.fill(val)` set all components of vector `vec` to the value `val`.

* `vec.zero()` is the same as `x.fill(0)`

Various linear combination of vectors can be formed:

* `dst.add(alpha, x)` add to the components of `dst` the components of the
  vector `x` multiplied by the scalar `alpha`.

* `dst.combine(alpha, x, beta, y)` stores in `dst` the sum of `alpha` times `x`
  plus `beta` times `y`.

* `dst.combine(alpha, x, beta, y, gamma, z)`

* `vec.scale(val)` scales all the components of the vector `vec` by the scalar
  `alpha`

* `dst.scale(alpha, src)` stores in vector `dst` the result of scaling vector
  `src` by the scalar `alpha`.

* `dst.multiply(w)` stores in vector `dst` the result of the component-wise
  multiplication of `dst` by the vector `w`.

* `dst.multiply(w, x)` stores in vector `dst` the result of the component-wise
  multiplication of vectors `w` and `x`.

The above methods manipulate vectors globally are supposed to be efficient.
Two methods are provided to examine or set individually the components of a
vector.  These methods are mainly used for debugging or informative purposes
and are not meant to be efficient (although it does not hurt if they are!).

* `vec.get(i)` get the value of `i`-th component of vector `vec`.

* `vec.set(i, val)` set the `i`-th component of vector `vec` to the value `val`.

where index `i` is an integer between `0` for the first component and `n - 1`
for the last component (`n` being the number of components of the vector).

* `vec.getNumber()` and `vec.length()` yields the number of components of the
  vector `vec`.

* `vec.getOwner()` and `vec.getSpace()` yields the vector space to which
  belongs vector `vec`.


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


## Basic operations on vectors

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

