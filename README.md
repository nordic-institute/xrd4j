# XRd4J

XRd4J is a Java library for building X-Road v6 Adapter servers and clients. The library implements X-Road v6 [SOAP profile](https://github.com/vrk-kpa/X-Road/blob/develop/doc/Protocols/pr-mess_x-road_message_protocol.md) v4.0 and [Service Metadata Protocol](https://github.com/vrk-kpa/X-Road/blob/develop/doc/Protocols/pr-meta_x-road_service_metadata_protocol.md). The library takes care of serialization and deserialization of SOAP messages: built-in support for standard X-Road SOAP headers, only processing of application specific request and response content must be implemented.

## Library Modules

* `client` : SOAP client that generates X-Road v6 SOAP messages that can be sent to X-Road Security Server. Includes request serializer and response deserializer.
* `server` : Provides an abstract servlet that can be used as a base class for Adapter Server implementations. Includes a request deserializer and a response serializer.
* `common` : General purpose utilities for processing SOAP messages and X-Road v6 message data models.
* `rest` : HTTP clients that can be used for sending requests to web services from Adapter Server.

## Maven Repositories

### Releases

All XRd4J release versions are available through the CSC Maven Repository [https://maven.csc.fi/repository/internal/](https://maven.csc.fi/repository/internal/).

Add the CSC Maven Repository in your POM-file inside the `<repositories>` tag:

```XML
<repository>
  <id>csc-repo</id>
  <name>CSC's Maven repository</name>
  <url>https://maven.csc.fi/repository/internal/</url>
</repository>
```

If running `mvn clean install` generates the error

```
Failed to collect dependencies at fi.vrk.xrd4j:common:jar:0.2.0: Failed to read artifact descriptor for fi.vrk.xrd4j:common:jar:0.2.0: Could not transfer artifact fi.vrk.xrd4j:common:pom:0.2.0 from/to csc-repo (https://maven.csc.fi/repository/internal/): sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target -> [Help 1]
```
you can either skip the certificate validation with command

```
mvn install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

or import the CSC Maven repository certificate as a trusted certificate into `cacerts` keystore. See full [instructions](documentation/Import-a-Certificate-as-a-Trusted-Certificate.md) for more information.

#### Dependency Declaration

Declare the following dependencies in your POM-file:

```XML
<!-- Module: common-->
<dependency>
  <groupId>fi.vrk.xrd4j</groupId>
  <artifactId>common</artifactId>
  <version>0.2.0</version>
</dependency>

<!-- Module: client-->
<dependency>
  <groupId>fi.vrk.xrd4j</groupId>
  <artifactId>client</artifactId>
  <version>0.2.0</version>
</dependency>

<!-- Module: server-->
<dependency>
  <groupId>fi.vrk.xrd4j</groupId>
  <artifactId>server</artifactId>
  <version>0.2.0</version>
</dependency>

<!-- Module: rest-->
<dependency>
  <groupId>fi.vrk.xrd4j</groupId>
  <artifactId>rest</artifactId>
  <version>0.2.0</version>
</dependency>
```

#### Snapshot Repository

Snapshot versions of XRd4J are available through the CSC Maven Snapshot repository in [https://maven.csc.fi/repository/snapshots/](https://maven.csc.fi/repository/snapshots/).

Include the snapshot repository in your POM-file inside the `<repositories>` tag:

```XML
<repository>
  <id>csc-snapshot-repo</id>
  <name>CSC's snapshot Maven repository</name>
  <url>https://maven.csc.fi/repository/snapshots/</url>
</repository>
```
After this, `x.x.x-SNAPSHOT` versions are available to be declared as dependencies as with release versions above. XRd4J snapshots are meant for development use only. Use release versions for final adapter implementations.

## Development

### XRd4J Development

Instructions for setting up an environment for XRd4J-related development can be found [here](documentation/Setting-up-Development-Environment.md).

Javadocs can be generated with the included script `generate-javadocs.sh`. The script will create `javadoc` folder with HTML documentation.

The most essential classes of the library are:

* `fi.vrk.xrd4j.common.member.ConsumerMember` : represents an X-Road consumer member that acts as a client that initiates a service call by sending a ServiceRequest.
* `fi.vrk.xrd4j.common.member.ProducerMember` : represents an X-Road producer member that produces services to X-Road.
* `fi.vrk.xrd4j.common.message.ServiceRequest<?>` : represents an X-Road service request that is sent by a ConsumerMember and received by a ProviderMember. Contains the sent SOAP request.
* `fi.vrk.xrd4j.common.message.ServiceResponse<?, ?>` : represents an X-Road service response message that is sent by a ProviderMember and received by a ConsumerMember. Contains the SOAP response.
* `fi.vrk.xrd4j.client.serializer.AbstractServiceRequestSerializer` : an abstract base class for service request serializers.
* `fi.vrk.xrd4j.server.deserializer.AbstractCustomRequestDeserializer<?>` : an abstract base class for service request deserializers.
* `fi.vrk.xrd4j.server.serializer.AbstractServiceResponseSerializer` : an abstract base class for service response serializers.
* `fi.vrk.xrd4j.client.deserializer.AbstractResponseDeserializer<?, ?>` : an abstract base class for service response deserializers.
* `fi.vrk.xrd4j.client.SOAPClientImpl` : a SOAP client that offers two methods for sending SOAPMessage and ServiceRequest objects.
* `fi.vrk.xrd4j.server.AbstractAdapterServlet` : an abstract base class for Servlets that implement SOAP message processing. Can be used as a base class for Adapter Server implementations.

### Adapter Development Using XRd4J

For implementation details and tips for creating an X-Road service adapter using XRd4J see [adapter implementation overview](documentation/adapter-implementation.md).

Testing HTTPS connectivity may require setting up SSL on Tomcat. Basic instructions for this can be found [here](documentation/Setting-up-SSL-on-Tomcat.md).  

### Compatibility Mode With Older X-Road Versions

To enable processing of SOAP Body elements in compatibility mode with older versions of X-Road protocol, method ```request.setProcessingWrappers(true)``` must be called before serialization or deserialization of messages. This means that request messages must contain ```request``` wrapper and response messages must contain ```request``` and ```response``` wrappers. To skip automatic procession of ```request``` and ```response``` wrappers, method ```request.setProcessingWrappers(false)``` must be called before serialization or deserialization of messages, which is the default. The usage of ```setProcessingWrappers``` method is demonstrated in the examples below.

## Credits

XRd4J library was originally developed by Petteri Kivimäki (https://github.com/petkivim) during 2014-2017. In June 2017 it was agreed that Population Register Centre (VRK) takes maintenance responsibility.

