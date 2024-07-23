plugins {
    `java-library`
    `maven-publish`
    checkstyle
    id("com.github.hierynomus.license")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api("com.sun.xml.messaging.saaj:saaj-impl:1.3.28")
    api("org.slf4j:slf4j-api:1.7.30")

    testImplementation("org.slf4j:slf4j-log4j12:1.7.30")
    testImplementation("org.apache.logging.log4j:log4j-core:2.17.1")
    testImplementation("junit:junit:4.13.1")
}

group = "org.niis"
version = "0.5.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

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

checkstyle {
    // TODO: update to latest version
    toolVersion = "6.11.2"

}

license {
    header = rootProject.file("../LICENSE")
    include("**/*.java")
    mapping("java", "SLASHSTAR_STYLE")
}
