import org.gradle.kotlin.dsl.dependencies

val group = "cn.nukkitmot.example"

val pluginName = "ExamplePlugin"
val pluginVersion = "1.0.0"
val pluginAuthor = "Nukkit Project"
val pluginWebsite = "https://github.com/MemoriesOfTime//"
val pluginDescription = "Example plugin showing the API"
val pluginMain = "cn.nukkitmot.exampleplugin.ExamplePlugin"
val pluginApi = listOf("1.0.5")

plugins {
    java
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.2.0"
    kotlin("plugin.lombok") version "2.2.0"
    id("io.freefair.lombok") version "8.12.1"
}

repositories {
    mavenCentral()
    maven("https://nexus.cyanbukkit.cn/repository/maven-public/") {
        content {
            excludeGroup("com.mojang")
        }
    }
    maven("https://repo.lanink.cn/repository/maven-public/")
    maven("https://www.jitpack.io")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("cn.nukkit:Nukkit:MOT-SNAPSHOT")
    // compileOnly("com.github.angga7togk:PlaceholderAPI-NK:Latest")
}


val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}


tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "pluginName" to pluginName,
                "pluginVersion" to pluginVersion,
                "pluginAuthor" to pluginAuthor,
                "pluginWebsite" to pluginWebsite,
                "pluginDescription" to pluginDescription,
                "pluginMain" to pluginMain,
                "pluginApi" to pluginApi.joinToString(", ")
            )
        }
    }

    jar {
        archiveFileName.set("${pluginName}-${pluginVersion}.jar")
    }
}