import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.ifood.logistics"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.spring.boot)
    implementation(libs.bundles.kotlin.core)
    implementation(libs.bundles.langchain4j)
    implementation(libs.okhttp)

    // Testing
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.platform.launcher)

    //implementation("com.vdurmont:emoji-java:5.1.1")
    // https://mvnrepository.com/artifact/org.apache.lucene/lucene-core
    // implementation("org.apache.lucene:lucene-core:10.2.2")
    // testImplementation("org.apache.lucene:lucene-core:10.2.2")

    // https://mvnrepository.com/artifact/org.bsc.langgraph4j/langgraph4j-core
    // implementation("org.bsc.langgraph4j:langgraph4j-core:1.6.0-rc4")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
