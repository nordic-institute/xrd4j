plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(project(":common"))
    api(project(":rest"))

    testImplementation(libs.bundles.testImplementation)
    testImplementation(libs.org.wiremock.wiremock)
    testImplementation(libs.org.assertj.assertjCore)
    testImplementation(libs.org.mockito.mockitoCore)
}


pomSettings {
    name = "XRd4J :: Client"
    description = "This module provides a SOAP client that generates X-Road v7 SOAP messages that can be sent to X-Road Security Server."
}

