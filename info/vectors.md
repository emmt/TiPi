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


## Methods for a vector space

Implementing a concrete `VectorSpace` involves two things:

1. A concrete implementation of the vectors of this vector space.  This can be
   as simple as implementing the two methods `get()` and `set()` for getting
   and setting a specific component of a vector.  These methods are not meant
   to be efficient, they are mainly provided for testing or debugging purposes.

2. A number of methods which operate on the specifc vectors of this vector
   space have to be implemented.  These methods are assumed to be efficient and
   are used by TiPi to perform all necessary operations on vectors.

All methods whose name begins with an underscore character, *e.g.* `_dot`, are
low-level `protected` methods which must not be directly called by the
end-user.  They are used by higher level methods which take care of checking
that the arguments are coorect, in particular that the vectors belong to the
correct vector space.  Thus in the implementation of these low level methods no
argument checking is necessary.

Even though vectors can use any type of floating point values to store their
components, all scalar values in TiPi are passed as double precision floating
point.


### Methods that must be overridden

A concrete implementation of the `Vector` class has to override the following
methods:

* Method `public double get(int i) throws IndexOutOfBoundsException` yields
  the value of the component at index `i` of the vector.

* Method `public void set(int i, double val) throws IndexOutOfBoundsException`
  set the component at index `i` of the vector to the value `val`.

As said before, these two methods are not meant to be efficient, they are
mainly provided for testing or debugging purposes.  This is not the case for
the methods of the concrete implementation of the `VectorSpace` class described
below.

A concrete implementation of the `VectorSpace` class has to override the
following mandatory methods:

* Method `public Vector create()` creates a new vector of the vector space with
  undefined contents.

* Method `public Vector create(double alpha)` creates a new vector of the
  vector space with all components set to the given value `alpha`.

* Method `protected void _copy(Vector dst, Vector src)` copies the contents of
  a vector into another one: `dst[i] = src[i]` (for all `i`).

* Method `protected void _swap(Vector x, Vector y)` exchange the contents of
  the two vectors `x` and `y`.

* Method `protected void _fill(Vector vec, double alpha)` set all components of
  vector `vec` to the value `alpha`.

* Method `protected void _scale(Vector dst, double alpha, Vector src)` for
  scaling the components of vector `src` by the scalar `alpha` and store the
  result in vector `dst`.

* Method `protected double _dot(Vector x, Vector y)` computes the inner
  product of the two vectors `x` and `y`, that is the sum of the products of
  the corresponding components of the two vectors.

* Method `protected double _dot(Vector w, Vector x, Vector y)` computes the
  inner product of three vectors or the weighted inner product of two vectors.

* Method `protected double _norm1(Vector x)` computes the L1 norm of the vector
  `x`, that is the sum of absolute values of the components of `x`.

* Method `protected double _normInf(Vector x)` computes the infinite norm of
  the vector `x`, that is the maximum absolute value of the components of `x`.

* Method `protected void _multiply(Vector dst, Vector x, Vector y)` performs a
  component-wise multiplication of two vectors: `dst[i] = x[i]*y[i]` (for all
  `i`).  This method is used to implement diagonal operators.

* Method `protected void _combine(Vector dst, double alpha, Vector x, double
  beta, Vector y)` computes the linear combination of two vectors: `dst[i] =
  alpha*x[i] + beta*x[i]` (for all `i`).  As this method can be used to emulate
  other operations (as `copy`, `zero`, *etc.*), actual code should be optimized
  for specific factors `alpha` and/or `beta` equal to +/-1 or 0.  In particular
  when `alpha` (resp. `beta`) is zero, then `x` (resp. `y`) must not be
  referenced.

* Method `protected void _combine(Vector dst, double alpha, Vector x, double
  beta, Vector y, double gamma, Vector z)` computes the linear combination of
  three vectors: `dst[i] = alpha*x[i] + beta*x[i] + gamma*z[i]` (for all `i`).
  As this method can be used to emulate other operations (as `copy`, `zero`,
  *etc.*), actual code should be optimized for specific factors `alpha` and/or
  `beta` equal to +/-1 or 0.  In particular when `alpha` (resp. `beta` or
  `gamma`) is zero, then `x` (resp. `y` or `z`) must not be referenced.


### Methods with default implementation

The following methods have a default implementation which can be overwritten
with more efficient versions by the descendants of the `VectorSpace` abstract
class:

* Method `protected double _norm2(Vector x)` computes the Euclidean (L2) norm
  of the vector `x`, that is the square root of the sum of squared components
  of `x`.  The following default implementation is provided:

    protected double _norm2(Vector x) {
        return Math.sqrt(_dot(x, x));
    }

* Method `protected void _scale(Vector vec, double alpha)` for in-place scaling
  of vector `vec` by the scalar `alpha` has the following default
  implementation:

    protected void _scale(Vector vec, double alpha) {
        _scale(vec, alpha, vec);
    }

* Method `protected Vector _clone(Vector vec)` creates a new vector as a clone
  of another vector has the following default implementation:

    protected Vector _clone(Vector vec) {
        Vector cpy = create();
        _copy(cpy, vec);
        return cpy;
    }

* Method `void _zero(Vector vec)` to set to zero all components of vector `vec`
  has the following default implementation:

    protected void _zero(Vector vec) {
        _fill(vec, 0.0);
    }
