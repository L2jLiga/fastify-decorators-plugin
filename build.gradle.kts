plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    kotlin("jvm") version "1.4.21"
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
            <b>Features added:</b>
            <ul>
              <li>Quick-fix: Remove redundant initializer dependencies</li>
              <li>Quick-fix: Take format settings into account</li>
            </ul>

            <b>Bugs fixed:</b>
            <ul>
              <li>NPE in several inspections</li>
            </ul>

            <b>Miscellaneous:</b>
            <ul>
              <li>Dependencies: update Gradle to 6.8.0 (was 6.7.0)</li>
              <li>Dependencies: update Kotlin to 1.4.21 (was 1.4.10)</li>
              <li>Dependencies: update org.jetbrains.intellij to 0.6.5 (was 0.6.2)</li>
            </ul>
            """.trimIndent()
        )
    }
}