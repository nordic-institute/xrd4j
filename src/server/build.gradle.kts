plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(project(":common"))
    compileOnly(libs.javax.servlet.servletApi)

    testImplementation(libs.bundles.testImplementation)
}

pomSettings {
    name = "XRd4J :: Server"
    description = "This module provides an abstract servlet that can be use as a base class for Adapter Server implementation."
}
