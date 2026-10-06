import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java
import nl.javadude.gradle.plugins.license.License
import org.springframework.boot.gradle.tasks.bundling.BootWar

plugins {
    java
    war
    `maven-publish`
    alias(libs.plugins.org.owasp.dependencycheck)
    alias(libs.plugins.org.springframework.boot)
    alias(libs.plugins.io.mateo.cxf.codegen)
    alias(libs.plugins.com.github.hierynomus.license)
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
    implementation(libs.org.springframework.boot.springBootStarterWeb)
    implementation(libs.org.apache.cxf.cxfSpringBootStarterJaxws)
    // Spring Boot 3.5 manages Jackson and Log4j patch versions with open CVEs
    implementation(platform(libs.com.fasterxml.jackson.jacksonBom))
    constraints {
        implementation(xrd4j.org.apache.logging.log4j.log4jApi)
    }

    implementation(libs.org.niis.xrd4j.common)
    implementation(libs.org.niis.xrd4j.server)


    compileOnly(xrd4j.jakarta.servlet.servletApi)

    providedRuntime(libs.org.springframework.boot.springBootStarterTomcat)
    providedRuntime(xrd4j.org.apache.tomcat.embed.jasper)

    cxfCodegen(libs.org.apache.cxf.cxfRtTransportsHttp)

    testImplementation(libs.org.springframework.boot.springBootStarterTest)
    testImplementation(xrd4j.org.xmlunit.xmlunitAssertj3)
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

tasks.withType<Test>() {
    useJUnitPlatform()
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
    cxfVersion = libs.versions.cxf.get()
}

license {
    header = rootProject.file("../LICENSE")
    include("**/*.java")
    mapping("java", "SLASHSTAR_STYLE")
    strictCheck = true
}

tasks.named<License>("licenseMain") {
    source = fileTree("src/main/java")
}

tasks.named<License>("licenseTest") {
    source = fileTree("src/test/java")
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
