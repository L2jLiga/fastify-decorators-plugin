plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    kotlin("jvm") version "1.3.72"
}

group = "fastify_decorators.plugin"
version = "0.7"

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
    changeNotes("""
        <b>Bugs fixed:</b>
        <ul>
          <li>type cast exception when @Inject argument is literal (string, number etc)</li>
        </ul>
    """.trimIndent())
}
