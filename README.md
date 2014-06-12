TiPi
====

TiPi is a Java Toolkit for Inverse Problems and Imaging developed by the MiTiV project <http://mitiv.univ-lyon1.fr/>

Library needed
==============

bio-formats.jar	Icy dependency, In Icy/lib

EzPlug.jar	Icy dependency, In Icy/plugins/adufour

icy.jar		Icy dependency, In Icy

jtranform.jar	Globaly needed, maybe in Icy/Lib



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
