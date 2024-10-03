## Setting Up an Environment For XRd4J Development

This document describes the requirements and steps to set up an environment for XRd4J development.

### Software Requirements

* Linux / Windows / MacOS
* Java 11
* Tomcat 6 or 7 or 8
* Maven 3.x

### Getting the code

There are several ways to get the code, e.g. download it as
a [zip](https://github.com/nordic-institute/xrd4j/archive/master.zip) file or clone the git repository.

```
git clone https://github.com/nordic-institute/xrd4j.git
```

The code is located in the `src` folder and the application is made up of four modules `client`, `common`, `server` and
`rest`.

### Building the code

XRd4J uses gradle as the build management tool. In order to build the whole project and generate the four jar files (
client-x.x.x-SNAPSHOT.jar, common-x.x.x-SNAPSHOT.jar, server-x.x.x-SNAPSHOT.jar, rest-x.x.x-SNAPSHOT.jar), you must run
the maven command below from the `src` directory.

```
./gradlew build
```

Running the above gradle command generates the jar files under the directories presented below:

```
src/client/build/libs/client-x.x.x-SNAPSHOT.jar
src/common/build/libs/common-x.x.x-SNAPSHOT.jar
src/server/build/libs/server-x.x.x-SNAPSHOT.jar
src/rest/build/libs/rest-x.x.x-SNAPSHOT.jar
```

### Using local builds in your project

If you want to use the local builds in your project, publish the jar files to your local maven repository by running the
following command:

```
./gradlew publishToMavenLocal
```

and then use maven local repository in your project build file.
