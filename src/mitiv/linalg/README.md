Introduction
============
The idea is to have a Java interface (or abstract class)
to represent a vector space and its functionalities.

For instance:


Vector create(); // to create a suitable new vector with undefined contents
Vector create(double value); // to create a suitable new vector filled with value
Vector create(final Vector x); // to create a copy of x

void copy(final Vector src, Vector dst); // to copy SRC into DST
Vector copy(final Vector src);

void clear(Vector x);

void axpby(double alpha, final Vector x, double beta, Vector y);
void axpby(double alpha, final Vector x, double beta, final Vector y, Vector dst);

void dot(final Vector x, final Vector y);
void dot(final Vector w, final Vector x, final Vector y);

Optionally:
void project(Vector x); // in-place projection to vector space
void project(final Vector src, vector dst);

Notes:

1. To simplify the interface, scalars are of type double, but may be converted
   into float by the methods if Vector is in fact a collection of single
   precision values.
   
2. The "create" method is most likely the constructor of the Vector class.

3. Here a "Vector" is a very general concept, it can be a 1D array, an image
   (i.e. a 2D array), ..., in fact anything for which it makes sense to apply
   the above operations.

4. The fact that most methods have the destination vector as their arguments
   is intended to favor reusability of vector which may be objects that are
   costly to build or to store. 

5. According to the algebra definitions (see below) our vector space is
   a Euclidean one (because it has an inner product and because it must be
   in practice of finite dimension).

Issues:

1. There may be several vector spaces (e.g. an input and an output space
   for a linear operator).  It makes no sense to combine vectors of different
   species for some operations (e.g. the inner product). How is it possible
   to detect potential incompatibilities?

2. Can we avoid using templates?

3. The concept of Vector space is very like that of a "factory".

4. Functors (also see anonymous inner classes in Java).

WISH LIST
=========
 * implement LBFGS operator (and its inverse?);
 * implement trust region conjugate gradient (Steihaug method);
   Trond Steihaug, "The conjugate gradient method and trust regions in large
   scale optimization," SIAM J. Numer. Anal., vol. 20, pp. 626-637 (1983).
 * implement projectors to various sub-spaces (not necessarily vector spaces, e.g. for positivity);
 * implement proximal operators;
 * implement norms (Euclidean, L1, Infinite);

Algebra definitions
===================
 * A vector space E over the field F is a collection of elements can be added
   and scaled by a scalar (of the field F), these operations must have the
   following properties (axioms):

    - associativity of addition: u + (v + w) = (u + v) + w
    - commutativity of addition: u + v = v + u
    - there exists an identity element (the zero vector 0) for the
      addition: u + 0 = u
    - every vector has an inverse for the addition: v + (-v) = 0
    - scalar multiplication is distributive w.r.t. vector addition:
      α (u + v) = α u + α v
    - distributivity of scalar multiplication with respect to field
      addition: (α + β) v = α v + β v
    - compatibility of scalar multiplication with field multiplication:
      α (β v) = (α β) v
    - identity element of scalar multiplication: 1 v = v, where 1 denotes
      the multiplicative identity in F.

 * A Hilbert space {H, 〈⋅,⋅〉} is a vector space endowed with an inner
   product (which yields length and angle). A Hilbert space can have infinite
   dimension.

 * A Euclidean space is a vector space of finite dimension endowed with an
   inner product (which yields length and angle).

 * A Banach space is a Hilbert space of which the norm has the following
   property (parallelogram rule):
       ||u + v||^2 + ||u - v||^2 = 2 ( ||u||^2 + ||v||^2 )
   In words: the sum of the squared lengths of the diagonals of a parallelogram
   is equal to the sum of the squared lengths of the four sides of the
   parallelogram.
 
 Notation pour un espace de Hilbert : {H, 〈⋅,⋅〉}

TO DO
=====
LinearConjugateGradient should return exceptions rather than status?
