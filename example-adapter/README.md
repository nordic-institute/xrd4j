# X-Road Example Adapter

X-Road Example Adapter provides an example implementation of a web service that is compatible with X-Road 7. The
Example Adapter support the X-Road Message Protocol for SOAP
version [4.0](https://github.com/nordic-institute/X-Road/blob/develop/doc/Protocols/pr-mess_x-road_message_protocol.md).

The implementation is based on the [XRd4J](https://github.com/nordic-institute/xrd4j) library. The Example Adapter
contains
a single class that implements the following services:

* `getRandom` : returns a random number between 1-100
* `helloService` : returns a hello message with the given name
* `listPeople` : returns a list of mock people
* `personDetails` : returns a mock person based on the given `SSN`
* `storeAttachments` : accepts the request with attachments and returns name and size of each attachment
* `getAttachments` : returns the response with attachments. The number of attachments and their size are given in
  request.

The Example Adapter is meant to illustrate the basic level usage of the XRd4J library in processing X-Road messages.

## Try It Out

The fastest and easiest way to try out the application is by using the Spring Boot Gradle plugin. The only requirement
is to have Java 17 or later installed on your machine.

```bash
./gradlew bootRun
```

After that the application is accessible at:

```
http://localhost:8080/example-adapter/Endpoint
```

The WSDL description is accessible at:

```
http://localhost:8080/example-adapter/Endpoint?wsdl
```

## Software Requirements

* Java 17 or later
* Docker (*optional*)

## Development Environment

Setting up an environment for example-related development is explained [here](Setting-up-Development-Environment.md).

## Building

> **Note:** To use locally built XRd4J library in Example Adapter, the XRd4J library has to be built and published to
> the local Maven repository. See
> the [XRd4J documentation](../documentation/Setting-up-Development-Environment.md#using-local-builds-in-your-project)
> for details.

X-Road Example Adapter can be built using the provided Gradle wrapper. The following command will build the project.

```bash
./gradlew clean build
```

When successfully built, the following artifacts are produced in the `build/libs` directory:

* `example-adapter-x.x.x.war` : a deployable WAR file
* `example-adapter-x.x.x-boot.war` : a Spring Boot executable WAR file

## Installation

X-Road Example Adapter can be installed and run in the following ways:

* Deploying `example-adapter-x.x.x.war` into a web container such as Tomcat.
* Run the Spring Boot executable WAR file `example-adapter-x.x.x-boot.war` with java -jar command. This can also be done
  using Docker.

### Web container

Build X-Road Example Adapter and deploy it to a Java application server, e.g., Tomcat.

* Build the project and produce `example-adapter-x.x.x.war` file.
* Copy the file `tomcat.home/webapps` folder.
* Start/restart Tomcat.

### Spring Boot Executable WAR File

Spring Boot executable WAR file `example-adapter-x.x.x-boot.war` can be run with `java -jar` command:

```bash
java -jar example-adapter-x.x.x-boot.war
```

#### Docker

You can create a Docker image to run X-Road Example Adapter inside a container, using the provided Dockerfile.
Before building the Docker image, `example-adapter-x.x.x-boot.war` file has to be [built with Gradle](#building).

While you are in the project root directory, build the image using the `docker build` command. The `-t` parameter gives
your image a tag, so you can run it more easily later. Don’t forget the `.` command, which tells the `docker build`
command to look in the current directory for a file called Dockerfile.

```bash
docker build -t example-adapter .
```

After building the image, you can run X-Road Test Service using it.

```bash
docker run -p 8080:8080 example-adapter
```

See [Usage section](#usage) for further instructions.

## Access the application

After installation the application is accessible at:

```
http://localhost:8080/example-adapter/Endpoint
```

The WSDL description is accessible at:

```
http://localhost:8080/example-adapter/Endpoint?wsdl
```

## Usage

This section provides examples for calling the service after it is deployed and running. The example commands require
`curl` to be installed.

### getRandom

An example [SOAP request](examples/getRandomRequest.xml) (available in the `examples` directory).

At the project root, the following command will call `getRandom` service:

```bash
curl -d @examples/getRandomRequest.xml --header "Content-Type: text/xml" -X POST http://localhost:8080/example-adapter/Endpoint
```

An example of the corresponding [SOAP response](examples/getRandomResponse.xml) (available in the `examples` directory).

### helloService

An example [SOAP request](examples/helloServiceRequest.xml) (available in the `examples` directory).

At the project root, the following command will call `helloService`:

```bash
curl -d @examples/helloServiceRequest.xml --header "Content-Type: text/xml" -X POST http://localhost:8080/example-adapter/Endpoint
```

An example of the corresponding [SOAP response](examples/helloServiceResponse.xml) (available in the `examples`
directory).

### listPeople

An example [SOAP request](examples/listPeopleRequest.xml) (available in the `examples` directory).

At the project root, the following command will call `listPeople`:

```bash
curl -d @examples/listPeopleRequest.xml --header "Content-Type: text/xml" -X POST http://localhost:8080/example-adapter/Endpoint
```

An example of the corresponding [SOAP response](examples/listPeopleResponse.xml) (available in the `examples`
directory).

### personDetails

An example [SOAP request](examples/personDetailsRequest.xml) (available in the `examples` directory).

At the project root, the following command will call `personDetails`:

```bash
curl -d @examples/personDetailsRequest.xml --header "Content-Type: text/xml" -X POST http://localhost:8080/example-adapter/Endpoint
```

An example of the corresponding [SOAP response](examples/personDetailsResponse.xml) (available in the `examples`
directory).

### storeAttachments

An example [multipart MIME request](examples/storeAttachmentsRequest.txt) (available in the `examples` directory).

At the project root, the following command will call `storeAttachments`:

```bash
curl -X POST -H "Content-Type: multipart/related; start=\"<rootpart>\"; boundary=MIME_boundary" --data-binary @examples/storeAttachmentsRequest.txt -X POST http://localhost:8080/example-adapter/Endpoint
```

An example of the corresponding [SOAP response](examples/storeAttachmentsResponse.xml) (available in the `examples`
directory).

### getAttachments

An example [SOAP request](examples/getAttachmentsRequest.xml) (available in the `examples` directory).

At the project root, the following command will call `getAttachments`:

```bash
curl -d @examples/getAttachmentsRequest.xml --header "Content-Type: text/xml" -X POST http://localhost:8080/example-adapter/Endpoint
```

The response is multipart MIME message containing SOAP response and attachments. An example of the
corresponding [response](examples/getAttachmentsResponse.txt) (available in the `examples` directory).
