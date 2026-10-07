# Changelog

## 0.9.0 - unreleased
- **Breaking:** Java 21 is now the minimum supported Java version. Java 11 and 17 are no longer supported.
- Added Java 25 support
- Updated dependencies, including `httpclient5` 5.3.1 → 5.6.4 which the `rest` module exposes to consumers
- Signature creation and verification in `CryptoHelper` now always use UTF-8 instead of the platform default charset
- `SOAPHelper.xmlStrToDoc` now parses the string as characters, so the result no longer depends on the platform default charset or on the XML encoding declaration
- `ClientUtil.getResponseString` now decodes the response using the charset declared in its `Content-Type` header, falling back to UTF-8 instead of the platform default charset
- Improved generics support on `SOAPClient` and `LoadBalancedSOAPClient`, including the request serializers and response deserializers they use
- Improved generics support in the server module: `CustomRequestDeserializer`, `ServiceRequestDeserializer`, `ServiceResponseSerializer` and `AdapterUtils`
- Code using these types as raw types still compiles, but now produces unchecked warnings until type parameters are added
- Marked constructors that should no longer be used as `@Deprecated` in `ProducerMember`
- Relaxed validation in the `ProducerMember` constructor taking both subsystem code and service code: at least one of the two must be provided, where previously the service code was always required
- Added `ValidationHelper.validateAtLeastOneNotNullOrEmpty` to `common`

## 0.8.0 - 2025-06-03
- XRDDEV-2911 Fix issue with SOAPHelper.removeNamespaces
- **Breaking:** To use `removeNamespaces`, the provided element needs to be replaced by the returned element. Also, the input element of the function is modified. 
This comes due changes in jakarta.xml.soap.

## 0.7.0 - 2025-05-26
- XRDDEV-2911 Fix issue with moveChildren and updateNamespaceAndPrefix using Node incorrect implementation in certain cases 
- XRDDEV-2911 Make moveChildren and updateNamespaceAndPrefix catch, wrap and throw DOMException in SOAPException to avoid overlooking the error

## 0.6.0 - 2024-10-18
- Build migrated from Maven to Gradle 
- Update dependencies from javax.* to jakarta.*
- Java 17 and 21 support 
- Added integration tests for the server module

## 0.5.0-SNAPSHOT 2023-02-24
- Add JDK 11 support
- Update dependencies
 
## 0.4.0 - 2022-01-05
- XRDDEV-1874 Release version 0.4.0

## 0.4.0-SNAPSHOT 2022-01-04
- XRDDEV-1874 Add JRE 11 support
- Update dependencies

## 0.4.0-SNAPSHOT 2021-09-11
- Add `3RD-PARTY-NOTICES.txt` file.
- Include `LICENSE` and `3RD-PARTY-NOTICES.txt` in `jar` files. 

## 0.4.0-SNAPSHOT 2020-09-15
- XRDDEV-1349 Update dependencies.

## 0.3.0 - 2018-09-05
- XRDDEV-89 Release version 0.3.0

## 0.3.0-SNAPSHOT 2018-09-04
- XRDDEV-37 Update to use NIIS maven repository
- XRDDEV-81 Add proxy support

## 0.3.0-SNAPSHOT 2018-08-15
- XRDDEV-90 Fix critical errors and bugs reported by SonarQube.

## 0.3.0-SNAPSHOT 2018-07-04
- XRDDEV-44 Change copyright owner from VRK to NIIS and artifact package names from `fi.vrk.xrd4j` to `org.niis.xrd4j`.

## 0.2.0-SNAPSHOT 2018-02-12
- PVAYLADEV-1095 The use of request and response wrappers is disabled by default.
- #5 Add support for security token SOAP header.
- #7 Fix a bug regarding the SOAP header order of the response message.
- PVAYLADEV-1060 Add checkstyle, sonar and owasp checks and fix reported problems.
