plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    kotlin("jvm") version "1.4.0"
}

group = "fastify_decorators.plugin"
version = "0.8"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    type = "IU"
    version = "IU-LATEST-EAP-SNAPSHOT"
    updateSinceUntilBuild = false
    pluginName = "Fastify decorators"
}
intellij.setPlugins("JavaScriptLanguage")

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        """
        <b>Features added:</b>
        <ul>
          <li>Intention(refactoring): Actions to easy swap between DI usage (@Inject, getInstanceByToken or constructor)</li>
        </ul>
        
        <b>Miscellaneous:</b>
        <ul>
          <li>Dependencies: update Gradle to 6.6.1 (was 6.5.1)</li>
          <li>Dependencies: update Kotlin to 1.4.0 (was 1.3.72)</li>
        </ul>
    """.trimIndent()
    )
}
