# Deblurring/denoising of images with edge preserving

As a demonstration, TiPi provides a command line tool `tipideconv` for the
deblurring/denoising of images.  This tool avoids border artifacts, implements
edge-preserving regularization, accounts for bad/missing data and constraints
such as nonnegativity.


## Inverse problem

The principle of `tipideconv` is to solve the following inverse problem:

<p align="center"><code>
    min { f(x) = f<sub>data</sub>(x) + µ f<sub>prior</sub>(x) }
</code></p>

where `x` is the solution (*e.g.* the deblurred/denoised image) and `f(x)` is
the objective function to minimize.  The objective function is specified by
<code>f<sub>data</sub>(x)</code>, the *likelihood* term, which imposes to fit
the data, <code>f<sub>prior</sub>(x)</code>, the *regularization* term, which
imposes prior knowledge about `x` and `µ ≥ 0` which tune the relative
importance of the regularization.  Minimizing `f(x)` can be seen as seeking for
`x` which is compromise between good fit to the data and agreement with some
priors.

The likelihood term is defined as:

<p align="center"><code>
    f<sub>data</sub>(x) = (1/2) ∥h*x - y∥<sup>2</sup><sub>W</sub>
</code></p>

with `h` the point spread function (PSF), `h*x` the convolution of the image
`x` by `h`, `y` the data and `W = diag(w)` a diagonal weighting operator.  The
(squared) weighted norm is given by <code>∥u∥<sup>2</sup><sub>W</sub> =
u<sup>t</sup>⋅W⋅u</code>

The regularization favors egde-preserving smoothness, it is given by:

<p align="center"><code>
    f<sub>prior</sub>(x) = ∑<sub> i</sub>
    sqrt( ∥(∇x)<sub>i</sub>∥<sup>2</sup> + ε<sup>2</sup> )
</code></p>

where <code>∥(∇x)<sub>i</sub>∥</code> is the Euclidean norm of the spatial
gradient at position `i` and `ε` is an edge threshold parameter.  The
smoothness is less severely imposed where the spatial gradient is larger than
this threshold which should be set to the typical height of the edges.

The quantities `y`, `h`, `w`, `µ` and `ε` are all input parameters of
`tipideconv`.  If the PSF `h` is not specified, it is assumed to be a Dirac
delta function thus no attempt to deblur the image is done and the result of
the inverse problem is a denoised image.


## Simple usage

The command line tool is typically called as:

    java -jar TiPi.jar [OPTIONS] INPUT OUTPUT

where `TiPi.jar` is the launchable JAR file of TiPi, `OPTIONS` represent
optional settings, `INPUT` is the name of the file with the input image (or
data) `y` and `OUTPUT` is the name of the file to save the result `x`. All
files are identified by their extension (at least FITS and MDA format are
supported).

The most common options are:

* `-psf NAME` to specify the name of the file with the PSF `h`.  If not
  specified, a Dirac delta function is assumed (*i.e.* no deblurring).  If
  option `-normalize` is given, the input PSF is normalized so that `sum(h) =
  1`.

* `-mu µ` to specify the regularization level.

* `-tau τ` to specify the edge threshold.

There are many other options which are described in the following sections.
Most parameters have default values.  All parameters (and their default values)
can be displayed with:

    tipideconv -help


## Initial solution

The image restoration is an iterative process.  By default, the initial image
has the same value everywhere.  Another initial image may be provided with the
option `-init FILENAME` where `FILENAME` is the name of the file with the image
to start with.  Although the solution of the inverse problem is unique
(provided `µ > 0`), this option is useful to speedup computations by starting
the algorithm with a better approximation of the solution than the default
initial image.  This is of particular importance when tuning the parameters
(*e.g.* `µ` and `ε`) which have an influence on the solution.


## Weighting of the data

By default, the data noise is assumed to be i.i.d. (independent and identically
distributed) with a normal distribution.  This amounts to having a simple
squared Euclidean norm for the data agreement term
<code>f<sub>data</sub>(x)</code>, *i.e.* the weighting operator is equal to the
identity, `W = I`.  There are however options to specify other weights `w` and
assume that the data noise distribution is approximately independent and
Gaussian but not necessarily identically distributed.

Option `-weights FILENAME` where `FILENAME` is the name of a file with the
weights to use is the most flexible way to specify the weights `w`.  Note that
the weights `w` must have the same dimensions as the data `y` (*i.e.* one
weight per piece of data) and that all weights must be finite and nonnegative.

Another possibility is to use options `-noise SIGMA` and `-gain GAMMA` to
compute the weights using a simple model of the variance of the data:

* If only `-noise SIGMA` is specified, it is assumed that `SIGMA` is the
 standard deviation of the noise in the same units as the data for each
 measurement. `SIGMA` must be strictly positive.

* If `-noise SIGMA` and `-gain GAMMA` are both specified, it is assumed that
  `SIGMA` is the standard deviation of the readout noise in counts (*e.g.*
  photo-electrons) per measurement while `GAMMA` is the conversion factor in
  counts per analog digital unit (ADU) such that `GAMMA⋅y` is the measured data
  in count units.

Note that option `-weights` has precedence over `-noise` and `-gain`, the two
latter options are ignored if `-weights` is specified.  In neither `-noise`,
nor `-gain`, nor `-weights` are specified, a simple least-squares norm is used
for the data agreement term.


## Invalid data and avoiding border artifacts

One of the important feature of `tipideconv` is to properly take into account
missing or invalid data.  This however necessitates to properly specify where
are the invalid measurements.

In the input data, all non-finite values are assumed to be invalid and will not
be considered by the processing.  Furthermore, if weights are specified (via
the `-weights` option), any piece of data whose corresponding weight is zero is
also not considered.

This behavior can be combined with the option `-invalid FILENAME` to specify
the name of a data file with the same dimensions as the input data and whose
values are non-zero where the data should be discarded.

To avoid aliasing and the resulting border artifacts, `tipideconv` is capable
of restoring an image which is larger than the original data.  Option `-pad`
can be used to control the amount of padding, its value can be `auto` (to avoid
aliasing almost completely), `min` to work with the minimal possible size, or a
number `n` to pad by at least `n` elements along all dimensions.  In addition
to this padding, the dimensions may be enlarged to achieve faster operations
(because of the FFT).  Option `-crop` can be specified to remove the extra
padding in the saved solution.  By default, the value of the padding elements
is set to the weighted mean of the data divided by the sum of the PSF values,
another value can be specified with `-fill VALUE`.


## Bound constraints

Options `-min LOWER` and `-max UPPER` may be used to specify a lower and an
upper bounds for the result.  For instance, `-min 0` enforces the result to be
nonnegative.  Note that when any bound is specified, the optimization method is
automatically set to be a variant of the limited memory BGFS method with bound
constraints.


## Tuning the optimizer

A limited memory optimizer is in charge of solving the inverse problem.  The
number of memorized steps can be specified with option `-mem NUMBER`.  If
`NUMBER` is less or equal zero and if there are no bound constraints on the
variables, then a non-linear conjugate gradient algorithm is used; otherwise, a
limited memory quasi-Newton method (similar to L-BFGS) is used.  The number of
memorized steps has only an incidence on the computation time, a moderate
number of memorized steps is usually a good compromise to achieve the least
number of iterations while keeping each iteration not too expensive to compute
and the amount of memory reasonable.  For very large problems, you may have to
take `NUMBER = 1` or `NUMBER = 0` to keep the memory requirements as low as
possible.  You may also tune the parameters of the `java` command to allocate
more memory to the Java virtual machine (in the limits of your machine memory
of course).

Another way to limit the amount of memory (and to speedup the computations) is
to force the algorithm to use single precision floating point values by
specifying option `-single`.  By default, single precision is used if all
inputs files use integers or single precision floating point values.

The convergence criterion is based on the Euclidean norm of the gradient of the
objective function (or on the infinite norm of the gradient if there are bound
constraints).  The convergence of the algorithm is assumed as soon as:

<p align="center"><code>
    ∥∇f(x)∥ ≤ max( gatol, grtol⋅∥∇f(x<sub>0</sub>)∥ )
</code></p>

where <code>x<sub>0</sub></code> is the initial solution while `gatol` and
`grtol` are the absolute and relative gradient tolerances.  Both can be
specified by the options `-gatol VALUE` and `-grtol VALUE`.

It is possible to limit the number of iterations of the algorithm and/or the
number of evaluations of the cost function respectively via the options
`-maxiter NUMBER` and `-maxeval NUMBER`.  Note that at least one function (and
gradient) evaluation per iteration is required.



