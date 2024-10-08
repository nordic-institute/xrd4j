plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(project(":common"))
    api(project(":rest"))

    testImplementation(libs.bundles.testImplementation)
}


pomSettings {
    name = "XRd4J :: Client"
    description = "This module provides a SOAP client that generates X-Road v7 SOAP messages that can be sent to X-Road Security Server."
}

