plugins {
    id("org.jetbrains.intellij") version "0.5.0"
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

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """
        """.trimIndent()
    )
}
