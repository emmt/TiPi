# The array factory in TiPi

The class `ArrayFactory` provides static methods for creating new shaped
arrays, converting arrays to have a specific element type or wrap existing
objects in a shaped array which share its elements with the object.


## Create a new shaped array

The static method `ArrayFactory.create()` creates a shaped array with given
element type and shape.  There are many possibilities to specify the shape of
the array, for instance:

```java
ArrayFactory.create(type, shape)
ArrayFactory.create(type, dims)
ArrayFactory.create(type, dim1, dim2, ...)
```

where `type` is the element type, `shape` is the array shape (an instance of
`Shape`), `dims` is an `int[]` array with the list of dimensions, `dim1`,
`dim2` ... are the successive dimensions.  Shaped arrays may have up to 9
dimensions (which is probably too large for any reasonnable application).

No dimensions may be specified in order to create a scalar of a given type:

```java
ArrayFactory.create(type)
```

The array element type can be one of:

* `Traits.BYTE` for an array of `byte` elements;
* `Traits.SHORT` for an array of `short` elements;
* `Traits.INT` for an array of `int` elements;
* `Traits.LONG` for an array of `long` elements;
* `Traits.FLOAT` for an array of `float` elements;
* `Traits.DOUBLE` for an array of `double` elements.

The storage of the elements of an array created by `ArrayFactory.create(...)`
is provided by a simple monodimensional Java array with the same element type.
For multi-dimensional arrays, the storage order of arrays created by
`ArrayFactory.create(...)` is *colum-major* which means that the leftmost
(first) indices vary faster when stepping through consecutive memory locations.


## Wrap an array around existing data

A shaped array may be created to share its elements with an existing
monodimensional Java array of any generic numerical type.  This is done by one
of the `ArrayFactory.wrap(...)` methods as follows:

```java
ArrayFactory.wrap(buf, shape)
ArrayFactory.wrap(buf, dims)
ArrayFactory.wrap(buf, dim1, dim2, ...)
```

where `buf` is the Java array and the same notation as above is used for
specifying the list of dimensions.  The length of the buffer `buf` must be the
same as the number of elements of the resulting shaped array (the product of
its dimensions).  The constructors of the `Stridden<Type><Rank>D` classes can
be used to wrap shaped arrays with arbitrary strides and offsets around
monodimensional Java arrays.

A single element buffer can be wrapped into a scalar by:

```java
ArrayFactory.wrap(buf)
```

Any shaped vector, say `vec`, can also be wrapped into a shaped array as
follows:

```java
ArrayFactory.wrap(vec)
```

which yieds a `ShapedArray` if `vec` is a `ShapedVector` and a
`FloatShapedArray` or a `DoubleShapedArray` if `vec` is a `FloatShapedVector`
or a `DoubleShapedVector` respectively.  The type and shape of the resulting
array are the same as those of `vec` and it is guaranteed that they share their
contents (with contiguous elements in colum-major order).  So the resulting
shaped array can be seen as a *view* of the shaped vector.


## Type conversion

The element type af an existing array can be converted by one of the
`to<Type>()` methods.  For instance:

```java
ArrayFactory.toFloat(arr)
arr.toFloat()
```

both yield a version of `arr` whose elements are of generic type `float`.  For
maximum efficiency, the conversion operation is *lazzy* in the sense that the
same array is returned if it already has the requested type.

**Warning:** In a near futur, static methods like `ArrayFactory.toFloat(arr)`
will be deprecated in favor of instance methods like `arr.toFloat()` which
are more readable.
