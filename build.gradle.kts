plugins {
    id("org.jetbrains.intellij") version "0.6.2"
    kotlin("jvm") version "1.4.10"
}

group = "fastify_decorators.plugin"
version = "0.9"

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
    updateSinceUntilBuild = false
    pluginName = "Fastify decorators"
}
intellij.setPlugins("JavaScriptLanguage")

tasks.runPluginVerifier {
    ideVersions(
        listOf(
            "IU-203.5600.34",
            "IU-202.7660.26",
            "IU-201.8743.12",
            "IU-193.7288.26"
        )
    )
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """
        <b>Features added:</b>
        <ul>
          <li>Core: Compatibility with IDEA 2020.3 (EAP)</li>
          <li>Inspections: Detect redundant initializer dependencies</li>
        </ul>
        
        <b>Miscellaneous:</b>
        <ul>
          <li>Core: updated plugin icon</li>
          <li>Dependencies: update Gradle to 6.7.0 (was 6.6.1)</li>
          <li>Dependencies: update Kotlin to 1.4.10 (was 1.4.0)</li>
          <li>Dependencies: update org.jetbrains.intellij to 0.6.2 (was 0.4.21)</li>
        </ul>
    """.trimIndent()
    )
}
