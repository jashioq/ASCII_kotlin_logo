plugins {
    id("java")
    kotlin("jvm")
    application
}

group = "com.app"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
    // mine
    implementation("org.joml:joml:1.10.5")
}

application {
    mainClass.set("com/app/MainKt")
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
