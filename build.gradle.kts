plugins {
    id("java")
    kotlin("jvm")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

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

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
    standardOutput = System.out
    systemProperty("kotlin.daemon.jvm.options", "-Dfile.encoding=UTF-8")
}
