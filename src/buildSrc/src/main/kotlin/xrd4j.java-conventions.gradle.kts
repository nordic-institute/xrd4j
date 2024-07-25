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

group = "org.niis.xrd4j"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

interface PomSettings {
    val name: Property<String>
    val description: Property<String>
}
val pomSettingsExtension = project.extensions.create<PomSettings>("pomSettings")

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        pom {
            url = "https://github.com/nordic-institute/xrd4j"
            licenses {
                license {
                    name = "MIT License"
                    url = "http://www.opensource.org/licenses/mit-license.php"
                }
            }

            scm {
                connection = "scm:git:https://github.com/nordic-institute/xrd4j.git"
                developerConnection = "scm:git:git@github.com:nordic-institute/xrd4j.git"
                url = "https://github.com/nordic-institute/xrd4j"
            }
        }

        afterEvaluate {
            pom {
                name = pomSettingsExtension.name
                description = pomSettingsExtension.description
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri("https://artifactory.niis.org/xroad-maven-releases/")
            val snapshotsRepoUrl = uri("https://artifactory.niis.org/xroad-maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
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
    from(rootProject.files("../LICENSE", "../3RD-PARTY-NOTICES.txt")) {
        into("META-INF")
    }
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
