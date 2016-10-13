# TiPi

Master: ![Travis build status](https://travis-ci.org/emmt/TiPi.svg?branch=master)

TiPi is a Java *Toolkit for Inverse Problems and Imaging* developed as part of
the MiTiV project <http://mitiv.univ-lyon1.fr/>


## Documentation

Asside from the [Javadoc](http://emmt.github.io/TiPi/) produded from the
source, the framework provided by TiPi is described in the
[*Introduction to TiPi Framework*](info/framework.md) which we recommend to
read first.


## Required Libraries

* Args4J 2.0.21

* [JTranforms and JLargeArrays](https://sites.google.com/site/piotrwendykier/software/jtransforms)


## Eclipse

This project can be used in Eclipse, just copy/clone it into Eclipse workspace,
then from Eclipse:

```
File -> import -> Existing project in workspace and choose TiPi folder.
```

Then Eclipse should automatically accept it as a known project.

To add the needed libraries to TiPi Eclipse project, move all the jars into a folder. Then in eclipse:

```
Right clic on TiPi project in package explorer -> Properties -> Java Build Path -> Libraries -> Add External jar and select all Library jar
```

## Setup for Developping

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


## Javadoc

The Javadoc is auto generated at each push and is available
[here](http://emmt.github.io/TiPi/).


## Known Issues

When adding the project to Eclipse if the project packages are named:
`src.mitiv.*`, you will have to:

```
Right click on project -> Properties -> Java Build Path -> Source: Add Folder..., and choose TiPi-src
```
