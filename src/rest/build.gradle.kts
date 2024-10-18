plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(libs.org.apache.httpcomponents.httpclient)
    api(libs.org.json.json)
    api(libs.org.slf4j.slf4jApi)

    testImplementation(libs.bundles.testImplementation)
    testImplementation(project(":common"))
}

pomSettings {
    name = "XRd4J :: Rest"
    description = "This module provides HTTP clients that can be used for sending requests to web services from Adapter Server."
}
