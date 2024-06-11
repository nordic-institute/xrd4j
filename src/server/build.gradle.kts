plugins {
    id("xrd4j.java-conventions")
}

dependencies {
    api(project(":common"))
    compileOnly(libs.javax.servlet.servlet.api)
}

group = "org.niis.xrd4j"
description = "XRd4J :: Server"
