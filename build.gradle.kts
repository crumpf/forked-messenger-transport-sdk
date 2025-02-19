buildscript {
    val compose_version by extra("1.2.0-rc02")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jmailen.gradle:kotlinter-gradle:3.4.0")
        classpath("com.vanniktech:gradle-android-junit-jacoco-plugin:0.16.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

// CocoaPods requires the podspec to have a `version`.
val buildVersion = "1.2.2"
val snapshot = System.getenv("SNAPSHOT_BUILD") ?: ""
version = "${buildVersion}${snapshot}"
group = "cloud.genesys"

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}