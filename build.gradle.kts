plugins {
    id("org.jetbrains.intellij") version "0.6.2"
    kotlin("jvm") version "1.4.10"
}

group = "fastify_decorators.plugin"
version = "0.10"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type = "IU"
    version = "IU-LATEST-EAP-SNAPSHOT"
    setPlugins("JavaScriptLanguage")
    pluginName = "Fastify decorators"
}

tasks {
    runPluginVerifier {
        ideVersions(
            listOf(
                "IU-203.5600.34",
                "IU-202.7660.26",
                "IU-201.8743.12",
                "IU-193.7288.26"
            )
        )
    }

    patchPluginXml {
        setSinceBuild("193.*")
        changeNotes(
            """
            """.trimIndent()
        )
    }
}