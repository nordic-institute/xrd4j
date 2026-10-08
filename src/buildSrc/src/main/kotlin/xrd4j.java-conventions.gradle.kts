plugins {
    `java-library`
    `maven-publish`
    jacoco
    checkstyle
    id("com.github.hierynomus.license")
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "org.niis.xrd4j"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
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
            // 'publish' task expects xroadMavenRepositoryUsername xroadMavenRepositoryPassword gradle project
            // properties to be present
            name = "xroadMavenRepository"
            credentials(PasswordCredentials::class)
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
//            "-Xlint:unchecked",
//            "-Xlint:deprecation",
//            "-Xlint:rawtypes",
            "-Xlint:fallthrough",
            "-Xlint:finally",
            "-parameters"
        )
    )
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.withType<Jar>() {
    from(rootProject.files("../LICENSE", "../3RD-PARTY-NOTICES.txt")) {
        into("META-INF")
    }
}

val testJavaVersion = providers.gradleProperty("testJavaVersion")

tasks.withType<Test>() {
    useJUnitPlatform()
    // testLogging.showStandardStreams = true
    if (testJavaVersion.isPresent) {
        javaLauncher = javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(testJavaVersion.get())
        }
    }
}

// Precompiled script plugins have no type-safe accessor for the catalog
val libs = the<VersionCatalogsExtension>().named("libs")

jacoco {
    toolVersion = libs.findVersion("jacoco").get().requiredVersion
}

checkstyle {
    toolVersion = libs.findVersion("checkstyle").get().requiredVersion
}

license {
    header = rootProject.file("../LICENSE")
    include("**/*.java")
    mapping("java", "SLASHSTAR_STYLE")
    strictCheck = true
}

tasks.named<com.hierynomus.gradle.license.tasks.LicenseCheck>("licenseMain") {
    source = fileTree("src/main")
}

tasks.named<com.hierynomus.gradle.license.tasks.LicenseCheck>("licenseTest") {
    source = fileTree("src/test")
}

tasks.named<com.hierynomus.gradle.license.tasks.LicenseFormat>("licenseFormatMain") {
    source = fileTree("src/main")
}

tasks.named<com.hierynomus.gradle.license.tasks.LicenseFormat>("licenseFormatTest") {
    source = fileTree("src/test")
}

tasks.withType(JacocoReport::class) {
    executionData(tasks.withType<Test>())
    reports {
        xml.required.set(true)
    }
}
