plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
    id("io.kotest") version "0.4.10"
    application
}

group = "org.colsum"
version = "1.0-SNAPSHOT"
val kotestVersion by extra { "5.7.2" }

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    // ======= TESTS ======= //
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest-jvm:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

tasks {
    val fatJar = register<Jar>("executableJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("executable")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}

koverReport {
    filters {
        excludes {
            classes("*Exception*")
        }
    }
}