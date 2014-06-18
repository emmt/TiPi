TiPi
====

TiPi is a Java Toolkit for Inverse Problems and Imaging developed by the MiTiV project <http://mitiv.univ-lyon1.fr/>

Library needed
==============

bio-formats.jar	Icy dependency, In Icy/lib

EzPlug.jar	Icy dependency, In Icy/plugins/adufour

icy.jar		Icy dependency, In Icy

jtranform.jar	Globaly needed, maybe in Icy/Lib

Eclipse
=======

This project can be used in Eclipse, just copy/clone it into Eclipse workspace, then from Eclipse: 

'''
File -> import -> Existing project in workspace and choose TiPi folder.
'''

Then Eclipse should automatically accept it as a known project.

To add the needed libraries to TiPi Eclipse project, move all the jars into a folder. Then in eclipse:

'''
Right clic on TiPi project in package explorer -> Properties -> Java Build Path -> Libraries -> Add External jar and select all Library jar
'''

Icy in Eclipse
==============

There is a plugin to launch quickly and easily Icy from eclipse [here](http://icy.bioimageanalysis.org/index.php?display=startDevWithIcy).

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
