plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(libs.org.apache.httpcomponents.httpclient)
    api(libs.org.apache.httpcomponents.httpcore)
    api(libs.org.json.json)
    testImplementation(project(":common"))
}

group = "org.niis.xrd4j"
description = "XRd4J :: Rest"
