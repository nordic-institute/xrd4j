# Setting Up an Environment For XRd4J Development <!-- omit in toc -->

This document describes the requirements and steps to set up an environment for XRd4J development.

### Table of Contents <!-- omit in toc -->

<!-- toc -->
- [Software Requirements](#software-requirements)
- [Getting the code](#getting-the-code)
- [Building the code](#building-the-code)
- [Using local builds in your project](#using-local-builds-in-your-project)
<!-- tocstop -->

### Software Requirements

* Linux / Windows / MacOS
* JDK 21

### Getting the code

There are several ways to get the code, e.g. download it as
a [zip](https://github.com/nordic-institute/xrd4j/archive/master.zip) file or clone the git repository.

```bash
git clone https://github.com/nordic-institute/xrd4j.git
```

The code is located in the `src` folder and the application is made up of four modules `client`, `common`, `server` and
`rest`.

### Building the code

XRd4J uses gradle as the build management tool. In order to build the whole project and generate the four jar files (
client-x.x.x-SNAPSHOT.jar, common-x.x.x-SNAPSHOT.jar, server-x.x.x-SNAPSHOT.jar, rest-x.x.x-SNAPSHOT.jar), you must run
the gradle command below from the `src` directory.

```bash
./gradlew build
```

Running the above gradle command generates the jar files under the directories presented below:

```text
src/client/build/libs/client-x.x.x-SNAPSHOT.jar
src/common/build/libs/common-x.x.x-SNAPSHOT.jar
src/server/build/libs/server-x.x.x-SNAPSHOT.jar
src/rest/build/libs/rest-x.x.x-SNAPSHOT.jar
```

To run the tests on another installed JDK, for example Java 25, pass its version with the `testJavaVersion` property.
The code is still compiled with Java 21.

```bash
./gradlew check -PtestJavaVersion=25
```

### Using local builds in your project

If you want to use the local builds in your project, publish the jar files to your local maven repository by running the
following command:

```bash
./gradlew publishToMavenLocal
```

and then use maven local repository in your project build file.
