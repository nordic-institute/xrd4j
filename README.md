# X-Road Library for Java (XRd4J)

[![Go to X-Road Community Slack](https://img.shields.io/badge/Go%20to%20Community%20Slack-grey.svg)](https://jointxroad.slack.com/)
[![Get invited](https://img.shields.io/badge/No%20Slack-Get%20invited-green.svg)](https://x-road.global/community)

X-Road Library for Java (XRd4J) provides a Java library for building X-Road v6 Adapter servers and clients. The library implements X-Road v6 [SOAP profile](https://github.com/nordic-institute/X-Road/blob/master/doc/Protocols/pr-mess_x-road_message_protocol.md) v4.0 and [Service Metadata Protocol](https://github.com/nordic-institute/X-Road/blob/master/doc/Protocols/pr-meta_x-road_service_metadata_protocol.md). The library takes care of serialization and deserialization of SOAP messages offering built-in support for standard X-Road SOAP headers; only processing of application specific request and response content remains to be implemented.

## Library Modules

* `client` : SOAP client that generates X-Road v6 SOAP messages that can be sent to X-Road Security Server. Includes request serializer and response deserializer.
* `server` : Provides an abstract servlet that can be used as a base class for Adapter Server implementations. Includes a request deserializer and a response serializer.
* `common` : General purpose utilities for processing SOAP messages and X-Road v6 message data models.
* `rest` : HTTP clients that can be used for sending requests to web services from Adapter Server.

## Maven Repositories

### Release Repository

All XRd4J release versions are available through the NIIS Maven Repository [https://artifactory.niis.org/xroad-maven-releases](https://artifactory.niis.org/xroad-maven-releases).

Add the NIIS Maven Repository in your POM-file inside the `<repositories>` tag:

```XML
<repository>
    <id>niis-repo</id>
    <name>NIIS's Maven repository</name>
    <url>https://artifactory.niis.org/xroad-maven-releases</url>
</repository>
```

If running `mvn clean install` generates the error

```
Failed to collect dependencies at org.niis.xrd4j:common:jar:0.2.0: Failed to read artifact descriptor for org.niis.xrd4j:common:jar:0.2.0: Could not transfer artifact org.niis.xrd4j:common:pom:0.2.0 from/to NIIS-repo (https://artifactory.niis.org/): sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target -> [Help 1]
```
you can either skip the certificate validation with command

```
mvn install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

or import the NIIS Maven repository certificate as a trusted certificate into `cacerts` keystore. See full [instructions](documentation/Import-a-Certificate-as-a-Trusted-Certificate.md) for more information.

#### Dependency Declaration

Declare the following dependencies in your POM-file:

```XML
<!-- Module: common-->
<dependency>
    <groupId>org.niis.xrd4j</groupId>
    <artifactId>common</artifactId>
    <version>${xrd4j.version}</version>
</dependency>

<!-- Module: client-->
<dependency>
    <groupId>org.niis.xrd4j</groupId>
    <artifactId>client</artifactId>
    <version>${xrd4j.version}</version>
</dependency>

<!-- Module: server-->
<dependency>
    <groupId>org.niis.xrd4j</groupId>
    <artifactId>server</artifactId>
    <version>${xrd4j.version}</version>
</dependency>

<!-- Module: rest-->
<dependency>
    <groupId>org.niis.xrd4j</groupId>
    <artifactId>rest</artifactId>
    <version>${xrd4j.version}</version>
</dependency>
```

#### Snapshot Repository

Snapshot versions of XRd4J are available through the NIIS Maven Snapshot repository in [https://artifactory.niis.org/xroad-maven-snapshots](https://artifactory.niis.org/xroad-maven-snapshots).

Include the snapshot repository in your POM-file inside the `<repositories>` tag:

```XML
<repository>
    <id>niis-snapshot-repo</id>
    <name>NIIS's snapshot Maven repository</name>
    <url>https://artifactory.niis.org/xroad-maven-snapshots</url>
</repository>
```
After this, `x.x.x-SNAPSHOT` versions are available to be declared as dependencies as with release versions above. XRd4J snapshots are meant for development use only. Use release versions for final adapter implementations.

## Development

### XRd4J Development

Instructions for setting up an environment for XRd4J-related development can be found [here](documentation/Setting-up-Development-Environment.md).

Javadocs can be generated with the included script `generate-javadocs.sh`. The script will create `javadoc` folder with HTML documentation.

The most essential classes of the library are:

* `org.niis.xrd4j.common.member.ConsumerMember` : represents an X-Road consumer member that acts as a client that initiates a service call by sending a ServiceRequest.
* `org.niis.xrd4j.common.member.ProducerMember` : represents an X-Road producer member that produces services to X-Road.
* `org.niis.xrd4j.common.message.ServiceRequest<?>` : represents an X-Road service request that is sent by a ConsumerMember and received by a ProviderMember. Contains the sent SOAP request.
* `org.niis.xrd4j.common.message.ServiceResponse<?, ?>` : represents an X-Road service response message that is sent by a ProviderMember and received by a ConsumerMember. Contains the SOAP response.
* `org.niis.xrd4j.client.serializer.AbstractServiceRequestSerializer` : an abstract base class for service request serializers.
* `org.niis.xrd4j.server.deserializer.AbstractCustomRequestDeserializer<?>` : an abstract base class for service request deserializers.
* `org.niis.xrd4j.server.serializer.AbstractServiceResponseSerializer` : an abstract base class for service response serializers.
* `org.niis.xrd4j.client.deserializer.AbstractResponseDeserializer<?, ?>` : an abstract base class for service response deserializers.
* `org.niis.xrd4j.client.SOAPClientImpl` : a SOAP client that offers two methods for sending SOAPMessage and ServiceRequest objects.
* `org.niis.xrd4j.server.AbstractAdapterServlet` : an abstract base class for Servlets that implement SOAP message processing. Can be used as a base class for Adapter Server implementations.

### Adapter Development Using XRd4J

For implementation details and tips for creating an X-Road service adapter using XRd4J see [adapter implementation overview](documentation/adapter-implementation.md).

Testing HTTPS connectivity may require setting up SSL on Tomcat. Basic instructions for this can be found [here](documentation/Setting-up-SSL-on-Tomcat.md).  

### Compatibility Mode With Older X-Road Versions

To enable processing of SOAP Body elements in compatibility mode with older versions of X-Road protocol, method `request.setProcessingWrappers(true)` must be called before serialization or deserialization of messages. This means that request messages must contain `request` wrapper and response messages must contain `request` and `response` wrappers. To skip automatic procession of `request` and `response` wrappers, method `request.setProcessingWrappers(false)` must be called before serialization or deserialization of messages, which is the default. The usage of `setProcessingWrappers` method is demonstrated in the [the adapter implementation examples](documentation/adapter-implementation.md).

## Credits

* XRd4J library was originally developed by Petteri Kivimäki (https://github.com/petkivim) during 2014-2017.
* XRd4J library was maintained and further developed by the Finnish Population Register Centre (VRK) during 06/2017-05/2018.
* In June 2018 it was agreed that Nordic Institute for Interoperability Solutions (NIIS) takes maintenance responsibility.
