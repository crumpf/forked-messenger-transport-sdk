import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version "1.4.31"
    id("com.android.library")
    id("org.jetbrains.dokka") version "1.4.30"
    id("org.jmailen.kotlinter")
    id("maven-publish")
    id("signing")
    id("transportValidationPlugin")
}

android {
    compileSdk = Deps.Android.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Deps.Android.minSdk
        targetSdk = Deps.Android.targetSdk
        consumerProguardFiles("transport-proguard-rules.txt")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
    packagingOptions {
        resources {
            excludes += "META-INF/*.kotlin_module"
        }
    }
}

val iosFrameworkName = "MessengerTransport"
version = project.rootProject.version
group = project.rootProject.group

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = Deps.Android.jvmTarget
            }
        }
        publishLibraryVariants("release", "debug")
        publishLibraryVariantsGroupedByFlavor = true
    }

    val xcf = XCFramework(iosFrameworkName)
    listOf(
        iosX64(),
        iosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
    }

    cocoapods {
        summary = "Genesys Cloud Messenger Transport Framework - Development podspec for use with local testbed app."
        homepage = "https://github.com/MyPureCloud/genesys-messenger-transport-mobile-sdk"
        license = "MIT"
        authors = "Genesys Cloud Services, Inc."
        ios.deploymentTarget = "11.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            // The default name for an iOS framework is `<project name>.framework`. To set a custom name, use the `baseName` option. This will also set the module name.
            baseName = iosFrameworkName
            // To specify a custom Objective-C prefix/name for the Kotlin framework, use the `-module-name` compiler option or matching Gradle DSL statement.
            freeCompilerArgs += listOf("-module-name", "GCM")
        }
        pod("jetfire", "~> 0.1.5")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(Deps.Libs.Kotlinx.serializationJson)
                implementation(Deps.Libs.Kotlinx.coroutinesCore)
                implementation(Deps.Libs.Kotlinx.dateTime)
                implementation(Deps.Libs.Ktor.core)
                implementation(Deps.Libs.Ktor.serialization)
                implementation(Deps.Libs.Ktor.json)
                implementation(Deps.Libs.Ktor.logging)
                implementation(Deps.Libs.logback)
                implementation(Deps.Libs.kermit)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Deps.Libs.Assertk.common)
                implementation(Deps.Libs.Ktor.mock)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Deps.Libs.OkHttp.client)
                implementation(Deps.Libs.OkHttp.loggingInterceptor)
                api(Deps.Libs.Ktor.android)
                implementation(Deps.Libs.Ktor.loggingJvm)
                implementation(Deps.Libs.Kotlinx.coroutinesAndroid)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Deps.Libs.junit)
                implementation(Deps.Libs.Assertk.jvm)
                implementation(Deps.Libs.OkHttp.mockWebServer)
                implementation(Deps.Libs.mockk)
                implementation(Deps.Libs.Kotlinx.coroutinesTest)
            }
        }

        val iosMain = sourceSets.findByName("iosMain") ?: sourceSets.create("iosMain")
        val iosX64Main by getting
        val iosArm64Main by getting

        with(iosMain) {
            dependsOn(commonMain)
            dependencies {
                implementation(Deps.Libs.Ktor.ios)
            }

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }
    }
}

tasks {
    create<Jar>("fakeJavadocJar") {
        archiveClassifier.set("javadoc")
        from("./deployment")
    }

    register("generateGenesysCloudMessengerTransportPodspec") {
        val podspecFileName = "GenesysCloudMessengerTransport.podspec"
        group = "publishing"
        description = "Generates the $podspecFileName file for publication to CocoaPods."
        doLast {
            val content = file("${podspecFileName}_template").readText()
                .replace(oldValue = "<VERSION>", newValue = version.toString())
                .replace(oldValue = "<SOURCE_HTTP_URL>", newValue = "https://github.com/MyPureCloud/genesys-messenger-transport-mobile-sdk/releases/download/v${version}/MessengerTransport.xcframework.zip")
            file(podspecFileName, PathValidation.NONE).writeText(content)
        }
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            artifact(tasks["fakeJavadocJar"])
            pom {
                name.set("Genesys Cloud Mobile Messenger Transport SDK")
                description.set("This library provides methods for connecting to Genesys Cloud Messenger chat APIs and WebSockets from mobile applications.")
                url.set("https://github.com/MyPureCloud/genesys-messenger-transport-mobile-sdk")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    developer {
                        name.set("Genesys Cloud Mobile Dev")
                        email.set("GenesysCloudMobileDev@genesys.com")
                    }
                }

                scm {
                    url.set("https://github.com/MyPureCloud/genesys-messenger-transport-mobile-sdk.git")
                }
            }
        }
    }
}

afterEvaluate {
    configure<PublishingExtension> {
        publications.all {
            val mavenPublication = this as? MavenPublication
            mavenPublication?.artifactId =
                "messenger-transport-mobile-sdk${"-$name".takeUnless { "kotlinMultiplatform" in name }.orEmpty()}"
        }
    }
}

signing {
    // Signing configuration is setup in the ~/.gradle/gradle.properties file on the Jenkins machine
    isRequired = true

    sign(publishing.publications)
}

apply(from = "${rootDir}/jacoco.gradle.kts")