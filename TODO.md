 * mitiv.io.BufferedImage: most methods here are deprecated.

 * Add support for `boolean` arrays.

 * Decide that `byte` are interpreted as being unsigned (there is no
   real use of `signed byte` in image processing and having both
   `signed` and `unsigned` version of integers is not worth the added
   complexity, and bugs), the cleanest way to do that is to change the
   `get()` method for `byte` arrays.  Side effects: MDA format, etc.
   Note that this is similar to how is inetrpreted the `TYPE_BYTE` in
   `DataBuffer` objects.

 * Use `ImageWriteParam` and `ImageReadParam` to improve
   reading/writing of images.  This can be set in `FormatOptions`.

 * Deal with `NaN` and `Infinite` when converting arrays to images.

