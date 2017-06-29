If HTTPS is used between the client and the Security Server, the public key certificate of the Security Server MUST be imported into "cacerts" keystore. 

The first step is to obtain the Security Server's public certificate. That can be done in a variety of ways, such as contacting the server admin and asking for it, using openssl to download it, or, since it's an HTTP server, connecting to it with any browser, viewing the page's security info, and saving a copy of the certificate. 

```
https://mysecurityserverver.com/
```

Now that you have the certificate saved in a file, you need to add it to your JVM's trust store. At ```$JAVA_HOME/jre/lib/security/``` for JDKs or ```$JAVA_HOME/lib/security``` for JREs, there's a file named cacerts, which comes with Java and contains the public certificates of the well-known Certifying Authorities. To import the new cert, run keytool as a user who has permission to write to cacerts:

```
keytool -import -alias <some meaningful name> -file <the cert file> -keystore <path to cacerts file>
```

The password is "changeit".

If you see the below error message after importing the certificate when trying to publish a connection from the client to the Security Server, the certificate wasn't imported correctly.

```
Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

**N.B.** Self signed or untrusted SSL certificates may cause some extra problems, that occur when the common name (CN) in the certificate doesn't match the host name of service URL, e.g. when IP address is used in service URL instead of host name.  If you see the following error message after adding the certificate into keystore when trying to publish a connection from the client to the Security Server, it's caused by this issue.

```
javax.net.ssl.SSLHandshakeException: java.security.cert.CertificateException: No subject alternative names matching IP address x.x.x.x found
```

The default host name verifier checks if a host name matches the names stored inside the server's X.509 certificate. In the development environment the problem can be solved by implementing a custom host name verifier that verifies that the host name is an acceptable match with the server's authentication scheme. In fact, ```ClientUtil``` class provides a custom host name verifier that can be used by calling ```ClientUtil.setCustomHostNameVerifier()``` method. The method must be called prior to connecting to the Security Server.

It's also possible to turn off certificate validation by calling ```ClientUtil.doTrustToCertificates()``` method. In this case the certificate doesn't have to be imported into keystore. **However, this is not recommended.**