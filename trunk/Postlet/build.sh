#!/bin/sh
javac -g -verbose -d dist -source 1.3 -target 1.3 SRC/JAVA/*.java
cd dist
jar cvfm postlet.jar ../SRC/manifest *.class
rm -rf *.class
jarsigner postlet.jar tstkey
