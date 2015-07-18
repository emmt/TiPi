TiPi
====

![Travis build status](http://travis-ci.org/emmt/TiPi.png?branch=master)

TiPi is a Java Toolkit for Inverse Problems and Imaging developed by the MiTiV project <http://mitiv.univ-lyon1.fr/>

Library needed
==============

Args4J 2.0.21

JTranforms and JLargeArrays	Can be found  [here](https://sites.google.com/site/piotrwendykier/software/jtransforms)


Eclipse
=======

This project can be used in Eclipse, just copy/clone it into Eclipse workspace, then from Eclipse:

```
File -> import -> Existing project in workspace and choose TiPi folder.
```

Then Eclipse should automatically accept it as a known project.

To add the needed libraries to TiPi Eclipse project, move all the jars into a folder. Then in eclipse:

```
Right clic on TiPi project in package explorer -> Properties -> Java Build Path -> Libraries -> Add External jar and select all Library jar
```

Setup for developping
=====================

After cloning the GitHub repository by:
```
    git clone https://github.com/emmt/TiPi.git
```
you may configure git by editing the file .git/config which should
look like:
```
[core]
	repositoryformatversion = 0
	filemode = true
	bare = false
	logallrefupdates = true
[remote "origin"]
	url = https://github.com/emmt/TiPi.git
#	url = ssh://git@github.com/emmt/TiPi.git
	fetch = +refs/heads/*:refs/remotes/origin/*
[branch "master"]
	remote = origin
	merge = refs/heads/master
[branch "devel"]
	remote = origin
	merge = refs/heads/devel
[filter "cleanup-code"]
	clean = ./tools/code_cleanup
	smudge = cat
[filter "cleanup-text"]
	clean = ./tools/code_cleanup
	smudge = cat
```

Javadoc
=======

The javadoc is auto generated at each push and is [HERE](http://emmt.github.io/TiPi/)

Known Bugs
==========

When adding the project to Eclipse if the project packages are named: src.mitiv.*, you will have to:

```
Right click on project -> Properties -> Java Build Path -> Source: Add Folder..., and choose TiPi-src
```

Credits
=======

The development of OptimPack was supported by the
[MiTiV](http://mitiv-univ-lyon1.fr) project funded by the French *Agence
Nationale pour la Recherche* (ref. ANR-09-EMER-008).


License
=======

TiPi is released under under the [MIT "Expat" License](LICENSE.md).
