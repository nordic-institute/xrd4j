This document describes how a developer's workstation can be setup.

### Software Requirements

* Linux / Windows / MacOS
* Java 8 or Java 11
* Maven 3.x
* Docker (*optional*)

### Getting the code

The Example Adapter project is a part of the XRd4J git repository. You can access the source code by cloning it:

```bash
git clone https://github.com/nordic-institute/xrd4j.git
```

The Example Adapter is a separate Maven project and can be found under the `example-adapter` directory.

### Building the code

Example Adapter uses Maven as the build management tool. In order to build the whole project and generate the WAR-file (`example-adapter-x.x.x-SNAPSHOT.war`), you must run the maven command below from the project root directory.

```bash
mvn clean verify
```

Running the above maven command generates the WAR-file under the directory presented below (`x.x.x` being replaced by the version):

```
target/example-adapter-x.x.x-SNAPSHOT.war
```
