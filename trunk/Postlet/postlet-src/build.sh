#!/bin/sh
javac -g -verbose -d dist -source 1.3 -target 1.3 SRC/JAVA/*.java lib/*.java
cd dist 
jar cvfm postlet.jar ../SRC/manifest *.class netscape
rm -rf *.class netscape
jarsigner postlet.jar tstkey
