#!/bin/sh

set -x

rm -rf ../javadoc
./gradlew javadoc
