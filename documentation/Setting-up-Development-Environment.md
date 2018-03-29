## Setting Up an Environment For XRd4J Development

This document describes the requirements and steps to set up an environment for XRd4J development.

### Software Requirements

* Linux or Windows
* Java 8
* Tomcat 6 or 7 or 8
* Maven 3.x

### Getting the code

There are several ways to get the code, e.g. download it as a [zip](https://github.com/vrk-kpa/xrd4j/archive/master.zip) file or clone the git repository.

```
git clone https://github.com/vrk-kpa/xrd4j.git
```

The code is located in the `src` folder and the application is made up of four modules `client`, `common`, `server` and `rest`.

### Building the code

XRd4J uses maven as the build management tool. In order to build the whole project and generate the four war files (client-x.x.x-SNAPSHOT.jar, common-x.x.x-SNAPSHOT.jar, server-x.x.x-SNAPSHOT.jar, rest-x.x.x-SNAPSHOT.jar), you must run the maven command below from the `src` directory.

```
mvn clean install
```

Running the above maven command generates the jar files under the directories presented below:

```
src/client/target/client-x.x.x-SNAPSHOT.jar
src/common/target/common-x.x.x-SNAPSHOT.jar
src/server/target/server-x.x.x-SNAPSHOT.jar
src/rest/target/rest-x.x.x-SNAPSHOT.jar
```
