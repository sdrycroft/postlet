#!/bin/bash

# JDK bin directory
JDKBIN=/usr/java/jdk1.6.0_04/bin

# Reminder to insert the correct version number
# echo "Have you put the correct version number in?"

# Compile the .java files
$JDKBIN/javac -g -d dist -source 1.3 -target 1.3 SRC/JAVA/*.java -classpath /usr/java/jre1.6.0_06/lib/plugin.jar

# move into the compiled folder, and package it up
cd dist
$JDKBIN/jar cvfm postlet.jar ../SRC/manifest *.class > /dev/null

# Delete class files (paranoia)
rm -rf *.class

# Sign the jar file
$JDKBIN/jarsigner -storepass password postlet.jar tstkey

# Move the jar file
rm -rf ../postlet/postlet.jar
cp postlet.jar ../postlet/
cp postlet.jar /var/www/postlet.com/cvs

# Finally make the release zip files
cd ..
zip -q -r postlet.zip postlet/index.html postlet/javaUpload.php postlet/LICENCE postlet/postlet.jar postlet/README postlet/uploadTest.html
