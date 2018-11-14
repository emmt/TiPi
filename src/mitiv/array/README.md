Shaped objects
 - public int getRank(); -------------> number of dimensions
 - public int getNumber(); -----------> number of elements
 - public int getOrder(); ------------> the ordering of the dimensions
 - public int getDimension(int k); ---> k-th dimension
 - public int[] cloneShape(); --------> a copy of the dimension list
 - int[] getShape(); -----------------> the dimension list (the result must be considered as read-only)

Typed objects just have a type (type constants and utilities are in Traits.java):
 - public int getType(); ------------> the type of the elements
 
ShapedArray objects have a shape and a type 
 
IndexableND
 - public void set(int i1, ..., int iN, byte/short/int/long/float/double value);
 - public byte getByte(int i1, ..., int iN);
 - public short getShort(int i1, ..., int iN);
   etc.
 - public double getDouble(int i1, ..., int iN);

public abstract class ByteArray implements Typed, Shaped {
}
public abstract class Float3D extends Array


Byte1D, Short1D, ..., Float1D, and Double1D are typed versions of Array1D
Byte2D, Short2D, ..., Float2D, and Double2D are typed versions of Array2D
Byte3D, Short3D, ..., Float3D, and Double3D are typed versions of Array3D
etc.

These are abstract classes, however they can be instantiated by wrappers around
other types of objects.  From the point of view of the user, any flavor of a,
say, Fload3D object is used in a similar way: obj.get(i1,i2,i3) to query a
value, set(i1,i2,i3,val) to set a value, etc.  Below the hood, the Fload3D object
can be wrapped around a flat
`float[] array` or around a `float[][][] array`, or be a 3D view of another object.

In fact these classes are the only objects that the end_user has to manipulate.
All is needed to know is the type and the dimensionality.
 
The only thing that may matter may be the storage order to access the elements
of the object in the most efficient order.  The getOrder() returns the storage
order.
 
Each such derived object have a flatten method which returns the contents of
the ShapedArray as a 1D Java vector
of the type of the elements stored by the object and a length equals to the
number of elements.  For instance:

   Float3D arr = ...;
   float[] v = arr.flatten();
 
is such that v.length = err.getNumber();  If you want to make sure to get a
private copy of the contents, call arr.flatten(true);  The ordering of the
elements is column-major storage order.

If you want to ensure that the object is in the most compact form (i.e.
flatten) and with the preferred ordering of TiPi (column-major) call
the pack() method:
```java
   Float3D arr = ...
   arr = arr.pack(); 
   `float[][][] cube = new float[5][6][7]`;
   Float3D arr = Float3D.wrap(cube).pack();
   ```
This may break the sharing of values.

A view is a mean to access sub-parts of a shaped array.  Note that a view
is just another flavor of a shaped array.


Automatic conversion
====================
sed -e 's/FLOAT/DOUBLE/g;s/Float\([1-9]D\|Array\|Function\|Generator\|Scanner\)/Double\1/g;s/float/double/g;s/nextFloat/nextDouble/g;s/\([0-9]\.[0-9]*\)F/\1/g' <Float1D.java > Double1D.java
 
