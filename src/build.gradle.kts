plugins {
    id("org.owasp.dependencycheck")
    id("io.freefair.aggregate-javadoc")
    id("org.sonarqube")
}

repositories {
    mavenCentral()
}

dependencies {
    javadoc(project(":client"))
    javadoc(project(":common"))
    javadoc(project(":server"))
    javadoc(project(":rest"))

    javadocClasspath(libs.jakarta.servlet.servletApi)
}

tasks.withType<Javadoc>() {
    outputs.dirs(file("$projectDir/../javadoc"))
    options.encoding = "UTF-8"
    options.destinationDirectory = file("$projectDir/../javadoc")
}

dependencyCheck {
    suppressionFile = "config/dependency-check-suppressions.xml"
    formats = listOf("HTML", "XML")
    nvd.validForHours = 24
    skipConfigurations = listOf("checkstyle")

    if (project.hasProperty("nvdApiKey")) {
        nvd.apiKey = project.property("nvdApiKey") as String
    }
}
