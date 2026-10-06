import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java
import org.springframework.boot.gradle.tasks.bundling.BootWar

plugins {
    java
    war
    `maven-publish`
    id("org.owasp.dependencycheck") version "12.2.2"
    id("org.springframework.boot") version "3.5.16"
    id("io.mateo.cxf-codegen") version "2.5.0"
    id("com.github.hierynomus.license") version "0.16.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://artifactory.niis.org/xroad-maven-releases")
    }

    maven {
        url = uri("https://artifactory.niis.org/xroad-maven-snapshots")
    }

}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.5.16")
    implementation("org.apache.cxf:cxf-spring-boot-starter-jaxws:4.1.8")
    // Spring Boot 3.5 manages Jackson and Log4j patch versions with open CVEs
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.21.7"))
    constraints {
        implementation("org.apache.logging.log4j:log4j-api:2.26.1")
    }

    implementation("org.niis.xrd4j:common:0.9.0-SNAPSHOT")
    implementation("org.niis.xrd4j:server:0.9.0-SNAPSHOT")


    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat:3.5.16")
    providedRuntime("org.apache.tomcat.embed:tomcat-embed-jasper:10.1.60")

    cxfCodegen("org.apache.cxf:cxf-rt-transports-http:4.1.8")
}

group = "org.niis"
version = "0.0.10-SNAPSHOT"
description = "Example Adapter for X-Road"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.named<BootWar>("bootWar").get()) {
                classifier = "boot"
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://artifactory.niis.org/xroad-maven-releases/")
            val snapshotsRepoUrl = uri("https://artifactory.niis.org/xroad-maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            // 'publish' task expects xroadMavenRepositoryUsername xroadMavenRepositoryPassword gradle project
            // properties to be present
            name = "xroadMavenRepository"
            credentials(PasswordCredentials::class)
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.withType<Jar>() {
    from(rootProject.files("../LICENSE", "3RD-PARTY-NOTICES.txt")) {
        into("META-INF")
    }
}

tasks.named<War>("war") {
    archiveClassifier = ""
}

tasks.named<BootWar>("bootWar") {
    archiveClassifier = "boot"
}

cxfCodegen {
    cxfVersion = "4.1.8"
}

license {
    header = rootProject.file("../LICENSE")
    include("src/**/*.java")
    mapping("java", "SLASHSTAR_STYLE")
    strictCheck = true
}

tasks.register("wsdlSources", Wsdl2Java::class) {
    toolOptions {
        wsdl = "${projectDir}/src/main/resources/mtomservice.wsdl"
        markGenerated = true
    }
}

dependencyCheck {
    suppressionFile = "dependency-check-suppressions.xml"
    formats = listOf("HTML", "XML")
    nvd.validForHours = 24

    skipConfigurations = listOf("cxfCodegen")

    if (project.hasProperty("nvdApiKey")) {
        nvd.apiKey = project.property("nvdApiKey") as String
    }
}
