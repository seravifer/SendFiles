import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "sendFiles"
version = "0.1"

val kotlinVersion = "1.1.2-5"
val coroutinesVersion = "0.15"
buildscript {
    repositories {
        gradleScriptKotlin()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

apply { plugin("kotlin") }

repositories {
    mavenCentral()
    gradleScriptKotlin()
}

dependencies {
    compile(kotlinModule("stdlib", kotlinVersion))
    compile(kotlinxModule("coroutines-core", coroutinesVersion))
    compile(kotlinxModule("coroutines-jdk8", coroutinesVersion))
    compile(kotlinxModule("coroutines-javafx", coroutinesVersion))
    compile("no.tornado:tornadofx:1.7.5")
    compile(files("lib/portmapper-2.0.0.jar"))
}

tasks {
    "compileKotlin"(KotlinCompile::class) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

fun DependencyHandler.kotlinxModule(module: String, version: String = coroutinesVersion) =
        "org.jetbrains.kotlinx:kotlinx-$module:$version"