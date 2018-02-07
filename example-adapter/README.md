# X-Road Adapter Example

This project provides an example implementation of a web service that is compatible with the X-Road v6 adapter server protocol version [4.0](https://github.com/vrk-kpa/X-Road/blob/develop/doc/Protocols/pr-mess_x-road_message_protocol.md). The implementation is based on the [XRd4J](https://github.com/vrk-kpa/xrd4j) library. The example adapter contains a single class that
implements two services:

* `getRandom` : returns a random number between 1-100
* `helloService` : returns a hello message with the given name

The example adapter is meant to illustrate the basic level usage of the XRd4J library in processing X-Road messages.

## Try It Out

### Docker

The `example-adapter` directory contains a Dockerfile that can be used to create a container for running the example. Requires Docker to be installed and configured. 

Clone the repository to get access to the sources:  

```
git clone https://github.com/vrk-kpa/xrd4j.git
```

The example adapter project root is under the `example-adapter` directory.

Build the project from the project root with:

```
mvn clean install
```

After a successful build, at project root directory run:

```
docker build -t example-adapter .
```

This will create a new docker image named `example-adapter`. A container based on the image can be run with:

```
docker run -p 8080:8080 example-adapter 
```

This will start the container and listen to port `8080`. On Linux, the container should be available at `localhost`. On Windows use `docker-machine ip` command to get Docker host’s IP address.

The example service can now be found at `http://localhost:8080/example-adapter-x.x.x-SNAPSHOT/Endpoint` where `x.x.x` needs to be replaced by the version that can be verified from the maven project file, or the archives produced by the build.
Example requests can be found in the `examples` directory. 

See [Usage section](#usage) for further instructions.

### Software Requirements

* Java 6 or later
* Tomcat 6 or later

### Development Environment

Setting up an environment for example-related development is explained [here](Setting-up-Development-Environment.md).


### Installation

**N.B.** If you intend to connect the services to an X-Road Security Server you must update your server's IP address / host name to the WSDL file. Look for the below line and replace the default URL with your server's IP / host name.

```XML
<soap:address location="http://localhost:8080/example-adapter-x.x.x-SNAPSHOT/Endpoint" />
```

#### JAR

* Build the project and produce `example-adapter-x.x.x-SNAPSHOT.jar` file (`x.x.x` being replaced by the actual version).
* Run the application: `$ java -jar example-adapter-x.x.x-SNAPSHOT.jar`.

#### WAR

* Build the project and produce `example-adapter-x.x.x-SNAPSHOT.war` file.
* Copy the file to `tomcat.home/webapps` folder.
* Start/restart Tomcat.

### Access the application

After installation (both JAR and WAR) the application is accessible at:

```
http://localhost:8080/example-adapter-x.x.x-SNAPSHOT/Endpoint
```

The WSDL description is accessible at:

```
http://localhost:8080/example-adapter-x.x.x-SNAPSHOT/Endpoint?wsdl
```


### Usage

This section provides examples for calling the service after it is deployed and running. The example commands require curl to be installed.

#### getRandom

An example [SOAP request](examples/xroad-6.4/getRandomRequest.xml) (available in the `examples` directory).

Be sure to replace `x.x.x` in the command with the actual version. If at the project root, the following command will call `getRandom` service: 

```
curl -d @examples/xroad-6.4/getRandomRequest.xml --header "Content-Type: text/xml" -X POST http://localhost:8080/example-adapter-x.x.x-SNAPSHOT/Endpoint
```

An example of the corresponding [SOAP response](examples/xroad-6.4/getRandomResponse.xml) (available in the `examples` directory).

#### helloService

An example [SOAP request](examples/xroad-6.4/helloServiceRequest.xml) (available in the `examples` directory).

Be sure to replace `x.x.x` in the command with the actual version. If at the project root, the following command will call `helloService`:

```
curl -d @examples/xroad-6.4/helloServiceRequest.xml --header "Content-Type: text/xml" -X POST http://localhost:8080/example-adapter-x.x.x-SNAPSHOT/Endpoint
```

An example of the corresponding [SOAP response](examples/xroad-6.4/helloServiceResponse.xml) (available in the `examples` directory).

