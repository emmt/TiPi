
# Introduction

TiPi is a Java *Toolkit for Inverse Problems and Imaging* developed by the
[MiTiV](http://mitiv.univ-lyon1.fr/) project.  The objective of TiPi is to
provide a framework for developping fast algorithms for solving inverse
problems.

For now TiPi provides:

* Classes and methods to manage multi-dimensional arrays, so-called *shaped
  arrays*, in a flexible way.

* Classes and methods to deal with so-called *vectors* which stores values in
  an efficient way.  These vectors are needed by iterative optimization methods
  and typically used to store the data, theirs weights and the variables of the
  inverse problem.

* Limited memory optimization algorithms to deal with large scale non-linear
  problems.

* Building-block classes to encode objective functions (likelihood,
  regularization, etc.).

* Utilities.


## Basic vector operations

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


## Rationale and Performances Issues

Vectors are intended to have a limited set of methods.  These methods are just
those which are needed by optimization methods and merely correspond to the
properties of "[*vectors*](http://en.wikipedia.org/wiki/Vector_space)" in the
conventional linear algebra sense.  Hence the name of these objects in TiPi.

Thanks to these restrictions, "*vectors*" can be implemented in forms suitable
for performances (non-conventional or distributed memory, etc.) and a
particular implementation is simplified by the fact that only a few methods
have to be exposed.

However, "*vectors*" are not really adpated for the many manipulations required
to prepare the inputs of the algorithms (e.g., reading images, converting
types, perform zero-padding, recentering, etc.) and to save the result (e.g.,
extracting parts, rescaling, writing to a file, etc.).  This is why TiPi
provides another type of objects: the shaped arrays.  These objects are much
more versatile and use conventional memory.  We were careful so that their
manipulation should be efficient but not to the sacrifice of flexibility.

Typically, an algorithm written in the TiPi framework starts with shaped
arrays, does some pre-processing, creates vectors from the input arrays, runs
the iterative method, copies the result into a shaped array, displays and/or
saves the shaped array, perhaps after some post-processing.


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


# Shaped Arrays

A `ShapedArray` stores rectangular multi-dimensional arrays of elements of the
same data type.  Compared to a `ShapedVector`, the elements of a `ShapedArray`
reside in conventional memory and may be stored in arbitrary order and in a
non-contiguous way.


## Class Hierarchy

Various interfaces or abstract classes extend the `ShapedArray` interface
depending on whether the *type*<sup name="type1">[[1]](#type)</sup> and/or the
*rank*<sup name="rank1">[[2]](#rank)</sup> of the array are unveiled.  The
naming conventions are:

    ShapedArray   // neither rank nor type is known
    <Type>Array   // a shaped array of known type, but any rank
    Array<Rank>D  // a shaped array of known rank, but any type
    <Type><Rank>D // a shaped array with known type and rank

where the `<Type>` field is the capitalized name of the primitive type (*e.g.*
`Double` for `double`, `Int` for `int`, *etc.*) and `<Rank>` is a decimal
number in the range 1-9.  Arrays representating scalars are also needed, as
`0D` is a bit odd for 0-rank (that is scalar) object it is replaced by the
substantive `Scalar` in the rules above for 0-rank arrays and a `Scalar`
denotes a scalar of any type.  For instance:

* `FloatArray` is an array of float's;
* `Array3D` is a three-dimensional array;
* `Scalar` is a scalar of any type;
* `DoubleScalar` is a 0-rank array to access a single double precision
  value;
* `Long4D` is a four-dimensional array of long integer values.

All these are abstract classes or interfaces, the actual class depends on the
storage of the elements of the array.  Most of the time, the array can be
manipulated regardless of its concrete class.


## Operations on Shaped Arrays

This section describes the operations available for the most basic type, that
is `ShapedArray`.


### Basic Operations on Shaped Arrays

The basic operations available for a `ShapedArray`, say `arr`, are the same as
those of `Shaped` and `Typed` objects:

    arr.getType()        // query the type of the array
    arr.getRank()        // query the rank of the array
    arr.getNumber()      // query the number of elements in the array
    arr.getDimension(k)  // query the length of the (k+1)-th dimension
    arr.getOrder()       // query the storage order of the elements in the array
    arr.getShape()       // query the shape of the array

and type conversion:

    arr.toByte()         // convert to ByteArray
    arr.toShort()        // convert to ShortArray
    arr.toInt()          // convert to IntArray
    arr.toLong()         // convert to LongArray
    arr.toFloat()        // convert to FloatArray
    arr.toDouble()       // convert to DoubleArray

For efficiency reasons, conversion operations are *lazzy*: they return the same
object if it is already of the correct type.  Use the `copy` method to
duplicate an array:

    arr.copy()           // duplicate the contents of the array

This method yields a new shaped array which has the same shape, type and values
as `arr` but whose contents is independent from that of `arr`.  If `arr` is a
*view*, then the `copy` method yields a compact array in a *flat*<sup
name="flat1">[[3]](#flat)</sup> form.

The elements of a shaped array are generic Java data, their type, as given
by `arr.getType()`, can be one of:

* `Traits.BYTE` for an array of `byte` elements.
* `Traits.SHORT` for an array of `short` elements.
* `Traits.INT` for an array of `int` elements.
* `Traits.LONG` for an array of `long` elements.
* `Traits.FLOAT` for an array of `float` elements.
* `Traits.DOUBLE` for an array of `double` elements.

The storage order, as given by `arr.getOrder()`, indicates how to travel the
elements of the array for maximum efficiency.  It can be one of:

* `ShapedArray.COLUMN_MAJOR` for column-major storage order, that is the
  leftmost (first) indices vary faster when stepping through consecutive memory
  locations.

* `ShapedArray.ROW_MAJOR` for row-major storage order, that is the rightmost
  (last) indices vary faster when stepping through consecutive memory
  locations.

* `ShapedArray.NONSPECIFIC_ORDER` for any other storage order.


### Assign Elements of a Shaped Array

It is possible to assign the values of an array from another object:

    arr.assign(src)

where `src` is another shaped array, a shaped vector, or a Java array of a
primitive type.  If the type of the elements of `src` does not match that of
the elements of `arr`, conversion is automatically and silently performed.  If
`src` is a `Shaped` object, its shape however match that of `arr`.  If `src` is
a Java array, it must have as many elements as `arr`.  The `assign` operation is
optimized: if `arr` and `src` share their contents with the same storage,
nothing is done as nothing has to be done.  If `arr` and `src` share their
contents in a different form, the result is unpredictable.

When the type and dimensions of a shaped array are unveiled by its class,
individual elements become accessible via its `get(...)` and `set(...)`
methods.


### Create a Similar Shaped Array

To create a new array with the same type and shape as `arr` is simply done by:

    arr.create()

which return a *flat* array whose elements are contiguous and stored in
column-major order.  The `ArrayFactory` provides static methods to create
shaped arrays of any given type and shape.


### 1D View of a Shaped Array

Finally it is possible to view a shaped array as if it was a mono-dimensional
(1-D) array.  This is done by:

    arr.as1D()

Since the rank is unveiled by this operation, it is available for any
`ShapedArray` and yields an instance of the abstract class `Array1D`.  Note
that a *view* such as the one returned by the `as1D()` method let you fetch and
assign values of the original array with `get(...)` and `set(...)` methods.


## Operations Available for Arrays with Unveiled Type

Add/subtract/multiply all elements by a scalar:

    arr.increment(value)
    arr.decrement(value)
    arr.scale(value)

Fill with given or generated value:

    arr.fill(value)
    arr.fill(generator)

Map a function to every elements:

    arr.map(func)

Scan all the elements of the array:

    arr.scan(scanner)

Extract the contents as a flat Java vector:

    arr.flatten([forceCopy])


## Other Operations

Fetch a value:

    arr.get(i1,...,iR)

element type and rank must be known so `arr` is an instance of some
`<Type><Rank>D` abstract class.

Assign a value to an element:

    arr.set(i1,...,ir, value)


## Operations Available for Arrays with Unveiled Rank


### Slicing

    arr.slice(index[, dim])

By default `dim` = `LAST` (-1) to slice through the last dimension, result is
an array of same data type with one less dimension (slicing a `Float3D` yields
a `Float2D`, slicing a `Double1D` yields a `DoubleScalar`, *etc.*).  The
operation is fast as it returns a view on `arr`.  A slice shares its contents
with its parent.  The same rules as for ranges (see this section) apply to
`index` and `dim` which can have a negative value understood as being relative
to the end.


### Ranged Sub-array

You can make a sub-array for given ranges of indices:

    arr.view(rng1, rng2, ..., rngR)

which returns a view to `arr` limited to the elements indexed by the
ranges `rng*` are either `Range` objects or `null` which means
"*all*".

A range is a construction like:

    Range rng = new Range(start, stop);

or:

    Range rng = new Range(start, stop, step);

where `start` is the initial index of the range, `stop` is the last index and
`step` is optional and defaults to `+1` if omitted.

As in Java, indexes of a range start at `0` (also `Range.FIRST`) but may be
negative.  Starting/ending indexes of a range are interpreted as offsets
relative to the length of the dimension of interest if they are strictly
negative.  That is `-1` (also `Range.LAST`) is the last element along a
dimension, `-2` is the penultimate element, *etc.* This rule is however only
applied once: if `j` is a start/stop index, then *j* (if *j* >= 0) or *j*+*n*
(if *j* < 0) must be greater or equal 0 and strictly less than *n*, the length
of the dimension of interest.

The convention is that the element corresponding to the first index of a range
is always considered while the last one may not be reached.  In pseudo-code,
the rules are:

    if (step > 0) {
        for (int index = first; index <= last; index += step) {
            ...
        }
    } else if (step < 0) {
        for (int index = first; index >= last; index += step) {
            ...
        }
    }

The sign of the step must be in agreement with the ordering of the first and
last element of the range, otherwise the range may be *empty*.  This occurs
when (after applying the rules for negative indices) `first` > `last` and
`step` > 0 and when `first` < `last` and `step` < 0.

To ease the construction of ranges and make the code more readable, we
recommend to introduce a range factory in your class, *e.g.*:

    static public Range range(int first, int last) {
        return new Range(first, last);
    }
    static public Range range(int first, int last, int step) {
        return new Range(first, last, step);
    }

Then a ranged view of an array can be written as:

    arr.view(range(0,-1,2), range(4,8))

which is lighter and more readable than:

    arr.view(new Range(0,-1,2), new Range(4,8))


### Selected Sub-array

Likewise:

    arr.view(sel1, sel2, ..., selR)

returns a view to arr from a selection of indices, all `sel*` are either
`int[]` index list or `null` which means "*all*".  Beware that in selections of
indices, all indices must be reachable (`-1` is considered as out-of-bounds).

To ease the construction of such views and make the code more
readable, you can also use selection factories like:

    static public int[] select(int i1) {
        return new int[]{i1};
    }
    static public int[] select(int i1, int i2) {
        return new int[]{i1, i2};
    }
    static public int[] select(int i1, int i2, int i3) {
        return new int[]{i1, i2, i3};
    }
    ...

and write something like:

     arr.view(select(3,2,9), select(5,1))

Note that sub-arrays and slices are views to the original array (which can
itself be a view) with fast access to the elements of this array.  If you do
not want that the view and its parent share the same elements, use the `copy()`
method after making the view.

It is currently not possible to mix ranges, slicing and index selection in a
single su-array construction, as it would result in too many possibilities (and
potentially inefficient code).  It is however possible to chain
sub-array-constructions to obtain the same effect.  For instance:

    arr.view(null, range(2,11,3)).view(select(6,4,8,2), null)

to mimic:

    arr.view(select(6,4,8,2), range(2,11,3))

which is not allowed for now.




<b id="type">[1] Type:</b> The primitive type of the elements of a `Typed` object.
[↩](#type1)

<b id="rank">[2] Rank:</b> The number of dimensions of a multi-dimensional
(a.k.a. *shaped*) object. [↩](#rank1)

<b id="flat">[3] Flat:</b> Zero-offset, contiguous storage in column-major order.
[↩](#flat1)

