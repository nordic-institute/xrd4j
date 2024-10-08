# Setting Up an Environment For Example Adapter Development <!-- omit in toc -->

This document describes how a developer's workstation can be setup.

### Table of Contents <!-- omit in toc -->

<!-- toc -->
- [Software Requirements](#software-requirements)
- [Getting the code](#getting-the-code)
- [Building the code](#building-the-code)
<!-- tocstop -->

### Software Requirements

* Linux / Windows / MacOS
* Java 17 or later
* Gradle
* Docker (*optional*)

### Getting the code

The Example Adapter project is a part of the XRd4J git repository. You can access the source code by cloning it:

```bash
git clone https://github.com/nordic-institute/xrd4j.git
```

The Example Adapter is a separate Gradle project and can be found under the `example-adapter` directory.

### Building the code

Example Adapter uses Gradle as the build management
tool. [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
is the recommended way of using Gradle to ensure the correct version.

In order to build the whole project, you must run the command below from the `example-adapter`
directory.

```bash
./gradlew build
```

Running the above command generates the following WAR-files under the `build/libs` directory (`x.x.x` being replaced by
the version):

* `build/libs/example-adapter-x.x.x-SNAPSHOT.war` - a deployable WAR file
* `build/libs/example-adapter-x.x.x-SNAPSHOT-boot.war` - a Spring Boot executable WAR file
