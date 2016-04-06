#!/usr/bin/env bash
#path to SharkFW
SOURCE=./../SharkFW/src/java

#Fixed to java sources, do not change
ROOT=./app/src/main/java/net/sharkfw
TEST=./app/src/androidTest/java/net/sharkfw

#######################

rm -rf $ROOT
rm -rf $TEST

mkdir -p $ROOT
cp -R $SOURCE/android/net/sharkfw/* $ROOT
cp -R $SOURCE/core/net/sharkfw/* $ROOT
cp -R $SOURCE/j2seMail/net/sharkfw/* $ROOT
cp -R $SOURCE/j2se_android/net/sharkfw/* $ROOT
cp -R $SOURCE/j2seTests/net/sharkfw/* $ROOT

# does not compile
rm -f $ROOT/genericProfile/BasicGPKPTest.java
rm -f $ROOT/wasp/WASPSerializer.java
# confusing with pki/security
rm -f $ROOT/pki/*
