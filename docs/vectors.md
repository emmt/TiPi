# Using vectors in TiPi

This section describes the objects used in TiPi to store the variables (but
also the data and their weights) of an optimization problem.


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


The following methods are assumed to be efficient:

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

