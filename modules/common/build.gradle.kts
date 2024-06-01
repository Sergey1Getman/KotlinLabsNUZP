plugins {
    kotlin("jvm") version Versions.kotlin
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(Versions.jvmLevel)
}
