plugins {
    `java-library`
    `maven-publish`
    jacoco
    checkstyle
    id("com.github.hierynomus.license")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "org.niis.xrd4j"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(11)
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

tasks.withType<Test>() {
    useJUnitPlatform()
    // testLogging.showStandardStreams = true
}

checkstyle {
    toolVersion = "10.18.1"
}

license {
    header = rootProject.file("../LICENSE")
    include("**/*.java")
    mapping("java", "SLASHSTAR_STYLE")
    strictCheck = true
}


tasks.withType(JacocoReport::class) {
    executionData(tasks.withType<Test>())
    reports {
        xml.required.set(true)
    }
}
