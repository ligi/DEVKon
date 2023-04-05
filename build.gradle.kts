plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.devcon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url=uri("https://jitpack.io") }
}

dependencies {
    implementation("com.beust:klaxon:5.5")
    implementation("com.github.ligi:ipfs-api-kotlin:0.15")
    testImplementation(kotlin("test"))
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