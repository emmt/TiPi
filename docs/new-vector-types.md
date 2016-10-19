# Creating new vector types in TiPi

For now TiPi only provides two concrete types of vectors, `DoubleShapedVector`
and `FlaotShapedVector`, with their respective vector spaces classes,
`DoubleShapedVectorSpace` and `FloatShapedVectorSpace`.  Such vectors store
their components in a single Java array of type `double[]` or `float[]` and are
closely connected to `ShapedArray` (*i.e.*, they also have a *shape*).  For
very large problems, it may be more convenient to store the components of the
vectors differently, perhaps on different machines and/or in GPU memory.  It is
possible (and hopefully easy) to make TiPi aware of this specific storage by
implementing the corresponding vector and vector space classes.  Another reason
to create a new vector and vector space type may be to provide more efficient
(*e.g.*, multi-threaded) versions of the methods designed to operate on
vectors.

Implementing a new type of vectors in TiPi involves two things:

1. A concrete sub-class of `Vector` for the vectors of this vector space.  This
   can be as simple as implementing the two methods `get()` and `set()` for
   getting and setting a specific component of a vector.  These methods are not
   meant to be efficient, they are mainly provided for testing or debugging
   purposes.

2. A concrete sub-class of `VectorSpace` for the vector space.  Only a dozen of
   methods which operate on the specifc vectors of this vector space have to be
   implemented.  These methods are assumed to be efficient and are used by TiPi
   to perform all necessary operations on vectors.

All methods whose name begins with an underscore character, *e.g.* `_dot`, are
low-level `protected` methods which must not be directly called by the
end-user.  They are used by higher level methods which take care of checking
the validity of the arguments, in particular that the vectors belong to the
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

The following methods have a default implementation which can be overridden
with more efficient versions by the descendants of the `VectorSpace` abstract
class:

* Method `protected double _norm2(Vector x)` computes the Euclidean (L2) norm
  of the vector `x`, that is the square root of the sum of squared components
  of `x`.  The following default implementation is provided:

```java
protected double _norm2(Vector x) {
    return Math.sqrt(_dot(x, x));
}
```

* Method `protected void _scale(Vector vec, double alpha)` for in-place scaling
  of vector `vec` by the scalar `alpha` has the following default
  implementation:

```java
protected void _scale(Vector vec, double alpha) {
    _scale(vec, alpha, vec);
}
```

* Method `protected void _copy(Vector dst, Vector src)` copies the contents of
  a vector into another one: `dst[i] = src[i]` (for all `i`) with the following
  default implementation:

```java
protected void _copy(Vector dst, Vector src) {
    _combine(dst, 1, src, 0, src);
}
```

* Method `protected Vector _clone(Vector vec)` creates a new vector as a clone
  of another vector, it has the following default implementation:

```java
protected Vector _clone(Vector vec) {
    Vector cpy = create();
    _copy(cpy, vec);
    return cpy;
}
```

* Method `protected void _zero(Vector vec)` to set to zero all components of
  vector `vec` has the following default implementation:

```java
protected void _zero(Vector vec) {
    _fill(vec, 0);
}
```

* Method `protected void _add(Vector dst, double alpha, Vector x)` adds `alpha`
  times `x` to `dst`: `dst[i] += alpha*x[i]` and has the following default
  implementation:

```java
protected void _add(Vector dst, double alpha, Vector x) {
    _combine(dst, 1, dst, alpha, x);
}
```
