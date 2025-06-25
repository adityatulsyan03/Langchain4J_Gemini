plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// https://mvnrepository.com/artifact/dev.langchain4j/langchain4j-google-ai-gemini
	implementation("dev.langchain4j:langchain4j-google-ai-gemini:0.34.0")
	// https://mvnrepository.com/artifact/dev.langchain4j/langchain4j-core
	implementation("dev.langchain4j:langchain4j-core:0.34.0")
	// https://mvnrepository.com/artifact/dev.langchain4j/langchain4j
	implementation("dev.langchain4j:langchain4j:0.34.0")

	implementation("org.apache.poi:poi-ooxml:5.2.5")
	implementation("org.apache.commons:commons-collections4:4.4") // Optional but recommended

}

tasks.withType<Test> {
	useJUnitPlatform()
}
