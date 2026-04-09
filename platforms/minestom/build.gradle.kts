import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.JavaVersion
import org.gradle.api.attributes.java.TargetJvmVersion

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
    shadedApi(project(":common:implementation:base"))
    shadedApi("com.github.ben-manes.caffeine", "caffeine", Versions.Libraries.caffeine)
    shadedImplementation("com.google.guava", "guava", Versions.Libraries.Internal.guava)

    compileOnly("net.minestom", "minestom", Versions.Minestom.minestom)
}

tasks.named("jar") {
    finalizedBy("installAddonsIntoDefaultJar")
}
