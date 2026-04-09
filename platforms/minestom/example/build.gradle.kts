plugins {
    application
}

import org.gradle.api.JavaVersion
import org.gradle.api.attributes.java.TargetJvmVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

val javaMainClass = "com.dfsek.terra.minestom.TerraMinestomExample"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

configurations.configureEach {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

dependencies {
    shadedApi(project(":platforms:minestom"))

    implementation("net.minestom", "minestom", Versions.Minestom.minestom)
    implementation("org.slf4j", "slf4j-simple", Versions.Libraries.slf4j)
}

tasks.withType<Jar> {
    entryCompression = ZipEntryCompression.STORED
    manifest {
        attributes(
            "Main-Class" to javaMainClass,
        )
    }
}

application {
    mainClass.set(javaMainClass)
}

tasks.getByName("run").setProperty("workingDir", file("./run"))
addonDir(project.file("./run/terra/addons"), tasks.named("run").get())
