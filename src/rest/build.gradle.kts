plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(libs.org.apache.httpcomponents.httpclient)
    api(libs.org.apache.httpcomponents.httpcore)
    api(libs.org.json.json)
    testImplementation(project(":common"))
}

pomSettings {
    name = "XRd4J :: Rest"
    description = "This module provides HTTP clients that can be used for sending requests to web services from Adapter Server."
}
