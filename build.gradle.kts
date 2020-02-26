plugins {
    id("org.jetbrains.intellij") version "0.4.15"
    java
    kotlin("jvm") version "1.3.61"
}

group = "fastify_decorators.plugin"
version = "0.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
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
        <b>Features added:</b>
        <ul>
          <li>Inspection: Dependency Injection can not work without emitting decorators metadata.</li>
        </ul>

        <b>Fixed:</b>
        <ul>
          <li>Inspection: Controller arguments inspection shows error even class is not annotated.</li>
        </ul>
    """.trimIndent())
}