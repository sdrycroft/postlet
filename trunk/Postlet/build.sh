#!/bin/sh
javac -g -d dist -source 1.3 -target 1.3 SRC/JAVA/*.java lib/netscape/javascript/*.java
cd dist
jar cvfm postlet.jar ../SRC/manifest *.class netscape/javascript/*.class > /dev/null
rm -rf *.class netscape
jarsigner postlet.jar tstkey
rm -rf ../postlet/postlet.jar
cp postlet.jar ../postlet/
cd ..
zip -q -r postlet.zip postlet/index.html postlet/javaUpload.php postlet/LICENCE postlet/postlet.jar postlet/README postlet/uploadTest.html
zip -q -r postlet-src.zip postlet-src/SRC/JAVA/*.java postlet-src/dist postlet-src/build.sh postlet-src/SRC/manifest postlet-src/README postlet-src/README postlet-src/lib
sshfs sdrycroft@shell.sourceforge.net:/home/groups/p/po/postlet/htdocs ssh_sf/htdocs
cp dist/postlet.jar ssh_sf/htdocs/example/
fusermount -u ssh_sf/htdocs
#restartFirefox
