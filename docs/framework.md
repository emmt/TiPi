# Basic building blocks in TiPi

The objective of TiPi is to provide a framework for developping fast algorithms
for solving inverse problems in particular in the domain of imaging.  In
general, such problems involve minimization of objective functions with a large
number of variables (*e.g.*, as many as the number of pixels in an image or
voxels in a volume) by means of iterative methods.  TiPi attempts to address
the challenge of achieving efficient computations with large amount of data
while keeping some flexibility to reshape the data and implement various data
model and optimization methods.  For portability reasons, this version of TiPi
is implemented in Java which has almost the same performances of C/C++ or
FORTRAN in terms of computations.


## Vectors

For large scale problems, the variables and the data may be stored in many
different forms (using conventional CPU memory, or GPU memory, or a mixture of
these) and they may be split between several machines or shared between several
processes or threads.  Using GPU and/or more more than one CPU is also the way
to achieve fast computations.  In order to hide this complexity, TiPi assume a
simple and minimal interface (in the Java sense) to access the variables and
the data of the problem.  This interface consists in a limited (a dozen) number
of methods which are sufficient to implement all TiPi optimization algorithms.
If one has specific memory and computation models, it is sufficient to
implement these few required methods to have access to all TiPi algorithms.

It happens that this minimal set of methods merely corresponds to the
properties of [vectors](http://en.wikipedia.org/wiki/Vector_space) in the
conventional linear algebra sense.  Hence objects implementing these methods
are also called **vectors** in TiPi.  Any concrete (that is instanciable)
vector type must inherit from the abstract `Vector` class of TiPi.  In math,
vectors belong to vector spaces and this is also the case in TiPi where vector
instances are owned by instances of vector spaces (whose classes are derived
from the abstract `VectorSpace` class).

Vector manipulations are very simple, for instance assuming `x` and `y` are
two vectors, then:

```java
x.dot(y)
```

yields the inner product of `x` and `y`, while:

```java
x.add(alpha, y)
```

adds `alpha` (a floating point value) times `y` to `x`.  Thanks to the fact
that vectors are elements of their vector spaces, it is trivial to check
whether `x` and `y` do belong to the same vector space and thus that the above
operations make sense:

```java
if (x.getOwner() != y.getOwner()) {
    throw new IncorrectSpaceException("Vectors x and y do not belong to the same vector space");
}
```

This is one of the reasons to link vectors to their respective vector space.
Of course, the above checking is automatically performed by all vector methods
offered by TiPi which throw an `IncorrectSpaceException` exception if
incompatible vectors are used.

TiPi vectors are described in more details in section
[*Using vectors*](vectors.md) of the documentation.  The section
[*Creating new vector types*](new-vector-types.md) is intended for developers
who want to implement their own specific type of vectors.


## Shaped arrays

Vectors in TiPi are easy to implement but do not provide methods for flexible
management of data and pre- or post-processing.  Thus TiPi provides so-called
[*shaped arrays*](shaped-arrays.md) to manipulate multi-dimensional
(rectangular) arrays in a more convenient way than vectors.  Of course, TiPi
provides means to convert between shaped arrays (designed for easy and flexible
manipulation) and vectors (designed for efficiency and intensive computations).

A shaped array may have a single or many (up to 9) dimensions and its elements
can be any primitive Java numerical type (`byte`, `short`, `int`, `long`,
`float` or `double`).  Methods provided with shaped arrays include:

* Creating shaped arrays of different type and shape.

* Addressing the elements of a sub-array individually or via functional
  mappings or scanners.

* Element type conversion.  For instance `arr.toFloat()` to convert array
  `arr` into an array whose elements are of type `float`.

* Cropping or padding.

* Cyclic permutation of elements along the dimensions of the array.

* Making an image into a shaped array and conversely.

* Reading/writing shaped arrays from/to files.

* Efficient sub-array manipulation (slicing, sub-selection, *etc.*).

* Basic arithmetic or rank reducing operations.

* *etc.*

Section [*Shaped arrays in TiPi*](shaped-arrays.md) of the manual describes
methods implemented by shaped arrays in more details.  Section
[*Array factory in TiPi*](array-factory.md) of the manual describes how to
create shaped arrays from scratch or from given data.
