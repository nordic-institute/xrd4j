plugins {
    id("xrd4j.java-conventions")
    `jvm-test-suite`
}

dependencies {
    api(project(":common"))
    compileOnly(libs.jakarta.servlet.servletApi)

    testImplementation(libs.bundles.testImplementation)

}

pomSettings {
    name = "XRd4J :: Server"
    description = "This module provides an abstract servlet that can be used as a base class for Adapter Server implementation."
}


testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation(libs.jakarta.servlet.servletApi)
                implementation("org.apache.tomcat.embed:tomcat-embed-core:10.1.30")

                implementation(libs.org.apache.logging.log4j.log4jCore)
                implementation(libs.org.apache.logging.log4j.log4jSlf4j2Impl)
                implementation("org.assertj:assertj-core:3.26.3")
                implementation("org.xmlunit:xmlunit-assertj3:2.10.0")
                implementation("org.xmlunit:xmlunit-placeholders:2.10.0")
                implementation(libs.org.apache.james.mime4jCore)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                        jvmArgs(
                            "--add-opens=java.base/java.lang=ALL-UNNAMED",
                            "--add-opens=java.base/java.io=ALL-UNNAMED",
                            "--add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED"
                        )

                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}
