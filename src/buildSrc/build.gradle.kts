plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.licenseGradlePlugin)
    implementation(libs.dependencyCheckGradlePlugin)
    implementation(libs.javadocAggregateGradlePlugin)
    implementation(libs.sonarqubeGradlePlugin)
}
