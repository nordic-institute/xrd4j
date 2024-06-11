plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(project(":common"))
    api(project(":rest"))
}

group = "org.niis.xrd4j"
description = "XRd4J :: Client"

