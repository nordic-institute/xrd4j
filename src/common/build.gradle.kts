plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(libs.com.sun.xml.messaging.saaj.saajImpl)
    api(libs.org.slf4j.slf4jApi)

    testImplementation(libs.bundles.testImplementation)
    testImplementation(libs.org.assertj.assertjCore)
    testImplementation(libs.org.xmlunit.xmlunitAssertj3)
}

pomSettings {
    name = "XRd4J :: Common"
    description = "This module provides general purpose utilities for processing SOAP messages and it also contains X-Road v7 message data models."
}
