# Notes for developers


## Eclipse

### External libraries

Copy the JAR file(s) of the external libraries in `lib` directory, then
`Refresh` the Project and in `Project Properties`, choose `Java Build Path`,
tab `Libraries` and hit `Add JARs...`.  Then browse in the `lib` directory of
the project and select the JAR file.

It is then possible to attach source and documentation for the JAR library file
by expanding its entry in the list (the small black arrow at the left of its
name).


### Ant scripts

Java, Ant and JavadDoc must be installed:

    sudo apt install ant
    sudo apt install default-jdk

In `Export` choose `General > Ant Buildfiles` select `TiPi`.


## Documentation

Documentation consists in JavaDoc automatically generated from the Java source
and manual pages written in MarkDown format (in the `docs` directory) and
published using [MkDocs](http://www.mkdocs.org/).


### Installation of MkDocs

[MkDocs](http://www.mkdocs.org/) can be installed by PIP:

    sudo apt install python-pip python-setuptools
    sudo pip install --upgrade mkdoc


### Editing the documentation

While editing the files in the `docs` directory, you can have a look at the
result by using the built-in server of MkDocs and opening URL
http://127.0.0.1:8000 with your browser.  To launch the server:

    mkdocs serve

while in the directory where is the `mkdocs.yml` file (above the `docs`
directory).  To generate manually the documentation, type (from the same
directory):

    mkdocs build --clean


### Publishing the documentation

To publish the doc:

    mkdocs gh-deploy --clean

