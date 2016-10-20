# The TiPi Pre-processor


**TPP**, the *TiPi Pre-processor*, is used to produce Java code of many TiPi
classes from a single or a few source files.  In TiPi source tree, the files
which need to be pre-processed by TPP to produce Java code are suffixed by
`.javax` and are all in the `tpp` directory.

The principles of TPP are simple: it interprets special pre-processor
directives in the source code and re-emit other lines of code, possibly several
times if these lines appear in a pre-processor loop, after performing macro
substitution.  Compared to other programming language pre-processors, TPP
provides macros with immediate and deferred substitution, loops, evaluation of
expressions and can be used to generate almost arbitrary code.


## Calling the Preprocessor

The syntax for calling TPP is:

```sh
tpp [OPTIONS] [INPUT [OUTPUT]]
```

to preprocess the source file `INPUT` and produce the destination file
`OUTPUT`.  If omitted or set to `-`, the source (resp. the destination) file is
the standard input (resp.the standard output).

Options in `OPTIONS` are:

* `-Dname=value`: Define a macro (there may be many options of this kind).

* `-a` or `--autopkg`: Guess the Java package from the name of the output file
  and define a macro named `package` with the package name and whose value will
  replace all occurrences of `${package}` in the processed code.

* `-f` or `--docfilter`: Filter JavaDoc comments for unused parameters.

* `--debug`: Turn debug mode on.

* `-h`, `-?` or `--help`: Print a short help message and exit.

* `-v` or `--version`: Print version number and exit.

* `-w` or `--warning`: Add a warning on top of the file to prevent editing the
  result.

* `--`: Indicate the end of the options.

Some options are clearly targeted at Java code, but TPP can be applied to
other programming languages.


## General Syntax

Pre-processor directives are lines of the form:

```java
//# COMMAND ...
```

where, to improve readability, there can be any spaces before `//`, before and
after `#`.  In the output, all lines matching this and with a recognized
`COMMAND` are omitted.

All commands can have and optional comment which is delimited by the first
occurrence of `//` after the `//#`:

```java
//# COMMAND ... // COMMENT
```

Both `COMMAND` and `COMMENT` may be empty.  Comment lines are a special case
with an empty `COMMAND` and empty lines like:

```java
//#
```

have only spaces (including none) after the `#` and are ignored.


## Macros

Macros are pre-processor variables associated with a value.  Every occurrences
of something like `${NAME}` in the code will be replaced by the actual value of
the macro whose name is `NAME`.  It is an error if `NAME` is not a defined
macro.  Macro substitution is recursive: all occurrences of `${...}` are
replaced by the corresponding value in the processed file until no other
substitutions are possible.  The name of a macro is case sensitive, it starts
with a Latin letter or an underscore character (`a` to `z`, `A` to `Z` or `_`)
which can be followed by any number of Latin letters, underscore characters or
digits (`0` to `9`).

A macro can be redefined (unless it is one of the
[read-only macros](#predefined-macros)) and can be undefined at any time.  It
is also possible to temporarily suspend and resume substitution of a specific
macro.


### Simple Macro Definition

The `def` command defines a macro and takes one of the two following forms:

```java
//# def NAME  = VALUE // COMMENT
//# def NAME := VALUE // COMMENT
```

where `NAME` is the name of the macro, `VALUE` specifies the macro value and
`COMMENT` is an optional comment.  The `VALUE` term may be empty to define the
macro to be an empty string.  Spaces after the assignment operator and after
the `VALUE` term are ignored.

The two possibilities for (re)defining a macro differ in the processing of the
`VALUE` term:

* If the assignment operator is `=`, then all macros are recursively
  substituted in the `VALUE` term before defining the macro `NAME`.

* If the assignment operator is `:=`, then substitutions in the `VALUE` term
  are deferred until `${NAME}` is expanded except that occurrences of `${NAME}`
  in the `VALUE` term are substituted.  This exception is to avoid infinite
  substitution loops but can be exploited to append or prepend stuff to an
  existing macro.  This is of interest when a macro is build piece by piece in
  a loop for instance.  As explained later, this *feature* can also be used to
  [mimic macros with arguments](#macros-as-pseudo-functions).

Note that contrarily to C preprocessor, overwritting an existing definition is
not forbidden.


### Evaluate a Numerical Expression

A macro can be defined by evaluating a numerical expression with the directive:

```java
//# eval NAME OPER EXPR
```

where `NAME` is the macro name, `OPER` is an assignment operator and `EXPR` a
numerical expression whose value is computed before (re)defining `NAME` via the
operator.

The expression in `EXPR` is evaluated after recursive macro substitution.  The
syntax of the expression is similar to that of the C (in fact it is implemented
by the `expr` command of Tcl) with a special function `defined(MACRO)` which
yields `true` or `false` depending whether `MACRO` is a defined macro or not.

The operator `OPER` can be a simple `=` to assign the result of evaluating
expression `EXPR` to the macro `NAME` or a composite operator like `OP=` to
assign to `NAME` the result of the expression `${NAME} OP (EXPR)` where `OP`
can be `+`, `-`, `*`, `/`, `%`, `<<` or `<<`.

Example:

```java
//# def a = 1
//# def b = 4
//# eval a += ${b}/2
```

yields a macro named `a` whose value is `3`.

Expressions involve integer arithmetic and tests:

* `defined(NAME)`       check whether macro `NAME` is defined;
* `! defined(NAME)`     check whether macro `NAME` is not defined;
* `EXPR1 || EXPR2`      logical *or* (with lazzy evaluation);
* `EXPR1 && EXPR2`      logical *and* (with lazzy evaluation);
* `"text"`              literal text for string comparison;

Note that macros are recursively substituted if they appear in an expression
including whithin the double quotes of a string.


### Predefined Macros

When processing a file, TPP automatically define (and update) the following
macros:

* `${__FILE__}` yields the name of the current processed file.
* `${__LINE__}` yields the current line number in the processed file.
* `${__NEWLINE__}` yields a newline character: "\n".
* `${__SPACE__}` yields a single space character: " ".
* `${__COMMENT__}` yields the comment delimiter.
* `${}` yields an ordinary dollar sign.

These macros are read-only and cannot be redefined.

Macros can also be defined at the command line with the argument `-Dname=value`
where `name` is the name of the macro and `value` its initial value.  There may
be as many such definitions in the command line as needed.


### Undefine Macros

The command:

```java
//# undef NAME1 NAME2 ...
```

undefines the macros `NAME1`, `NAME2`, etc.  Until they are redefined, it is an
error to expand these macros.  Read-only macros cannot be undefined.
Undefining a macro which is not defined does nothing.


## Suspend Macro Expansion

The `#def` directive with the `:=` operator only partially expands the value of
a macro.  While this is sufficient for most of the cases, it is sometimes
necessary (or more readable) to temporally suspend the expansion of some
macros.

The command:

```java
//# suspend NAME1 NAME2 ...
```

suspends the expansion of the macros `NAME1`, `NAME2`, etc.  Until these macros
are redefined or their expansion resumed with the `#resume` directive, they
will not be expanded.  For instance, the expansion of `${NAME1}` will produce
`${NAME1}`.  Note that it is not required that a macro be defined prior to
suspend its expansion.

As said before, defining a macro (by the `#def` or `#eval` or `#for`
directives) resume the expansion of the macro.  This can also be achieved with
the following directive:

```java
//# resume NAME1 NAME2 ...
```


## Conditional Branching

A block of code can be processed or not depending on a logical test using the
`#if` directive and related `#elif` and `#else` directives.  The syntax is as
follows (with an arbitrary number, including none, of `#elif` directives and at
most one `#else` directive):

```java
//# if EXPR1
BLOCK1
//# elif EXPR2
BLOCK2
//# else
BLOCK3
//# end
```

where `BLOCK1` is processed if `EXPR1` evaluates to `true`, otherwise `BLOCK2`
is processed if `EXPR2` evaluates to `true`, eventually `BLOCK3` is processed
if neither `EXPR1` nor `EXPR2` evaluate to `true`.  Conditional expressions are
evaluated using the same rules as in the `#eval` directive.


## Loops

A distinctive feature of TPP is to provide loops which may be embedded in other
loops to an arbitrarily level.  There are two kinds of loops: `for`-loops and
`while`-loops.

The syntax of a `for`-loop is:

```java
//# for NAME in EXPR
...
//# end
```

where `EXPR` is everything after the `in` keyword and up to the end of line or
to the `//` of the optional comment.  After macro substitution of the `EXPR`
term, it is interpreted either as a list of values separated by spaces or as a
range.  The macro `NAME` will successively takes the different values of the
list or of the range and the body of the loop (that is code up to the matching
`end` keyword) will be processed.  If, after macro substitutions, the value of
`EXPR` is an empty list or an empty range, the the body of the loop is just
skipped.

A range of values has the form:

```java
FIRST : LAST
```

or

```java
FIRST : LAST : STEP
```

where (after macro expansion) `FIRST`, `LAST` and `STEP` are integers, if
omitted, `STEP` is assumed to be `1`.  A zero-`STEP` is forbidden.  Numerical
expressions are not yet supported for these fields but you can use the `#eval`
directive to compute the range parameters.  For instance:

```java
//# eval last = ${x} + 3*${y}
//# for i in 0 : ${last} // main loop
...
//# end // end of main loop
```

Here is another example with a list of values:

```java
//# def list = blue red yellow orange
//# for var in ${list}
//#     emit ${var}
//# end
```

where the directive `#emit` directive emits its argument(s) in the output code.

The syntax of `while`-loops is:

```java
//# while EXPR
...
//# end
```

which results in processing the boby of the loop until `EXPR` expands to a
false value.

The two following loops are similar (the second being more concise):

```java
//# def body := dim${k} = ${k}; // the code to expand
//# def k = 4
//# while ${k} < 12
${boby}
//#     eval k += 3
//# end

//# def body := dim${k} = ${k};
//# for k in 4:12:3
${body}
//# end
```

they both produce:

```java
dim4 = 4
dim7 = 7
dim10 = 10
```

Note how the expansion of `${k}` is deferred by using operator `:=` to define
macro `body`.


## Include Another Source File

The contents of another source file can be processed at any place by using the
`#include` directive:

```java
//# include WHAT
```

where `WHAT` (after recursive macro expansion) takes one of the two forms:
`<FILENAME>` or `"FILENAME"`.  The result is as if the contents of the file
`FILENAME` be inserted in place of the command.  A restriction is that
directives operating on blocks (`#if ... `, `#for ...` and `#while ...`) must
be open and closed in a single included file.


## Emitting Code or Printing Messages

The command:

```java
//# emit CODE
```

substitutes macros in `CODE` and prints it to the output file.

Commands:

```java
//# echo MESG
//# warn MESG
```

substitute macros in `MESG` and print it.  Command `#echo` uses the standard
output stream while `#warn` uses the standard error stream.

It may be useful to examine the contents of some macros (without a fully
recursive expansion).  To that end, the directive

```java
//# debug MESG
```

prints `MESG` after a *single round* of macro substitution on the standard error
stream.

The `#error` command:

```java
//# error MESG
```

substitutes macros in `MESG` and prints it on the standard error stream with
the line number and the name of the processed file and then aborts the
processing.


## Examples

### Variable Length Lists

The following example builds a list of dimensions:

```java
//# def dims =  // start with an empty list
//# def sep  =  // and an empty separator
//# for k in 1 : ${rank}
//#     def dims = ${dims}${sep}dim${k}
//#     def sep = ,${__SPACE__}
//# end
```

and yields a macro named `dims` with contents `dim1, dim2, ...`.  Note the use
of an auxiliary macro `sep` and of the predefined macro `__SPACE__` to nicely
separate the elements of the list.  Another possibility is to write:

```java
//# if ${rank} < 1
//#     def dims =  // result is an empty list
//# else
//#     def dims = dim1 // initial list
//#     for k in 2 : ${rank}
//#         def dims = ${dims}, dim${k}
//#     end
//# end
```


### Macros as Pseudo-Functions

Deferred substitution can be used as follows:

```java
//# def list := ${prefix}1
//# for k in 2 : 3
//#     def list := ${list},${prefix}${k}
//# end
```

which yields a macro `list` whose contents is
`${prefix}1,${prefix}2,${prefix}3`.  Note that, at this stage, `prefix` does
not need to be defined.  The macro `list` can then be used as a template to
make lists after substitution of the `prefix` macro:

//# def prefix  = foo
//# def FooList = ${list}
//# def prefix  = bar
//# def BarList = ${list}
```

which yields `foo1,foo2,foo3` and `bar1,bar2,bar3` for the respective contents
of macros `FooList` and `BarList`.

With this kind of trick it is also possible to mimic the behavior of
macros with arguments (even though it is less readable).

To build the macro `list` above, we may also suspend the expansion of the macro
`prefix`:

```java
//# suspend prefix
//# def list = ${prefix}1
//# for k in 2 : 3
//#     def list = ${list},${prefix}${k}
//# end
```

As (re)defining `prefix` will automatically resume macro expansion, it is not
necessary to have a `#resume prefix` command.


## Index of Directives

* [`//`](#general-syntax) comment;

* [`#debug`](#emitting-code-or-printing-messages) print a debug message;

* [`#def`](#simple-macro-definition) define a macro;

* [`#echo`](#emitting-code-or-printing-messages) print a message to the
  standard output stream;

* [`#elif`](#conditional-branching) conditional branching;

* [`#else`](#conditional-branching) conditional branching;

* [`#emit`](#emitting-code-or-printing-messages) write some code to the result;

* [`#end`](#conditional-branching) mark the end of a loop or of a conditional
  branching;

* [`#error`](#emitting-code-or-printing-messages) output an error message and
  exit;

* [`#eval`](#evaluate-a-numerical-expression) evaluate a numerical expression;

* [`#for`](#loops) loop with a macro taking different values;

* [`#if`](#conditional-branching) conditional branching;

* [`#include`](#include-another-source-file) include the contents of another
  file;

* [`#resume`](#suspend-macro-expansion) resume macro expansion;

* [`#suspend`](#suspend-macro-expansion) suspend macro expansion;

* [`#undef`](#undefine-macros) undefine a macro;

* [`#warn`](#emitting-code-or-printing-messages) print a message to the
  standard error stream;

* [`#while`](#loops) conditional loop;



## Future Evolution

1. It may be useful to customize the syntax in order to accomodate to
   various programming languages.  For instance:

   * `@name@`       to interpolate a macro;
   * `@@`           for a single `@`;
   * `#@ COMMAND`   for preprocessor directives;

2. Macros with arguments (spaces stripped): `${macro(arg1,arg2,...)}`

3. String functions (like predefined macros).

```java
${substr(str,i1,i2,subs)}
${strmatch(string,pattern)}
...
```

4. Allow for numerical expressions in ranges (taking care of the ternary
   operator).
