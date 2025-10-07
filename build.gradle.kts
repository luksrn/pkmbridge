plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.serialization") version "2.2.0"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"

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

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("dev.langchain4j:langchain4j-ollama:1.7.1")
	implementation("dev.langchain4j:langchain4j-easy-rag:1.7.1-beta14")
	implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:1.7.1-beta14")
    implementation("dev.langchain4j:langchain4j-onnx-scoring:1.7.1-beta14")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
	implementation("com.squareup.okhttp3:okhttp:4.12.0")
	implementation("com.vdurmont:emoji-java:5.1.1")
	// https://mvnrepository.com/artifact/org.apache.lucene/lucene-core
	//implementation("org.apache.lucene:lucene-core:10.2.2")

	testImplementation("org.apache.lucene:lucene-core:10.2.2")

	// https://mvnrepository.com/artifact/org.bsc.langgraph4j/langgraph4j-core
	//implementation("org.bsc.langgraph4j:langgraph4j-core:1.6.0-rc4")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
