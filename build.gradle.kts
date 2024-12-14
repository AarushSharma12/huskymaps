import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.gradleup.shadow") version "8.3.5" // Ensure Shadow plugin is declared here
    java
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MapServer" // Add Main-Class for the entry point
}

java {
    sourceCompatibility = JavaVersion.VERSION_17 // Ensure Java version compatibility
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-codec:commons-codec:1.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("io.javalin:javalin:6.3.0") // Update Javalin version to 6.3.0
    implementation("org.locationtech.spatial4j:spatial4j:0.8")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:unchecked")
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Werror")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging.events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    testLogging.showStandardStreams = true
    testLogging.showExceptions = true
}
