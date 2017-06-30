#!/bin/sh

set -x

rm -rf ../javadoc
mkdir -p ../javadoc
javadoc -d ../javadoc -sourcepath common/src/main/java:client/src/main/java:server/src/main/java:rest/src/main/java -subpackages fi.vrk.xrd4j
