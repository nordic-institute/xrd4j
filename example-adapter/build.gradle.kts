import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    `java-library`
    `maven-publish`
    id("io.mateo.cxf-codegen") version "2.4.0"
}

repositories {
    mavenLocal()
    mavenCentral()
//    maven {
//        url = uri("https://artifactory.niis.org/xroad-maven-releases")
//    }
//
//    maven {
//        url = uri("https://artifactory.niis.org/xroad-maven-snapshots")
//    }

}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("org.apache.cxf:cxf-spring-boot-starter-jaxws:4.0.5")

    implementation("org.niis.xrd4j:common:0.5.0-SNAPSHOT")
    implementation("org.niis.xrd4j:server:0.5.0-SNAPSHOT")

    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("org.slf4j:slf4j-reload4j:2.0.13")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    cxfCodegen("org.apache.cxf:cxf-rt-transports-http:4.0.5")
}

group = "org.niis"
version = "0.0.6-SNAPSHOT"
description = "Example Adapter for X-Road"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

cxfCodegen {
    cxfVersion = "4.0.5"
}

tasks.register("wsdlSources", Wsdl2Java::class) {
    toolOptions {
        wsdl = "${projectDir}/src/main/resources/mtomservice.wsdl"
        markGenerated = true
    }
}
