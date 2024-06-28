plugins {
    alias(libs.plugins.dependencyCheck)
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
