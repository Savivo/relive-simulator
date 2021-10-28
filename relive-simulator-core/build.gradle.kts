val project_version: String by project

plugins {
    kotlin("multiplatform")
}

group = "xyz.qwewqa.relive.simulator"
version = project_version

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        browser()
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += "-Xir-property-lazy-initialization"
            }
        }
    }
    wasm32()
}

dependencies {
    "commonMainImplementation"(kotlin("stdlib"))
    "commonMainImplementation"("com.github.h0tk3y.betterParse:better-parse:0.4.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.run {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}
