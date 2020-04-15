plugins {
    id("org.jetbrains.intellij") version "0.4.18"
    java
    kotlin("jvm") version "1.3.72"
}

group = "fastify_decorators.plugin"
version = "0.5"

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
          <li>Quick-fix: enable emitting decorator metadata when it disabled.</li>
          <li>Inspection: highlight constructor if emitting decorator metadata disabled</li>
        </ul>

        <b>Miscellaneous:</b>
        <ul>
          <li>Upgrade gradle to 6.3 (was 5.1.1)</li>
          <li>Dependencies: update org.jetbrains.intellij to 0.4.18 (was 0.4.16)</li>
          <li>Dependencies: update kotlin to 1.3.72 (was 1.3.70)</li>
        </ul>
    """.trimIndent())
}