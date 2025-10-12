plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.github.luksrn"
version = "0.0.1-SNAPSHOT"
description = "Rest APIs for the PKM Bridge RAG"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Submodule dependencies
    implementation(project(":logseqdb"))
    implementation(project(":obsidian"))
    implementation(project(":rag"))
    implementation(project(":ollama"))

    implementation(libs.bundles.spring.boot.core)
    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.kotlin.core)
    implementation(libs.bundles.langchain4j)
    implementation(libs.okhttp)

    // Testing
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
