import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.native.cocoapods)

    id("maven-publish")
    id("signing")
    alias(libs.plugins.maven.publish)
}




apply(plugin = "maven-publish")
apply(plugin = "signing")


tasks.withType<PublishToMavenRepository> {
    val isMac = getCurrentOperatingSystem().isMacOsX
    onlyIf {
        isMac.also {
            if (!isMac) logger.error(
                """
                    Publishing the library requires macOS to be able to generate iOS artifacts.
                    Run the task on a mac or use the project GitHub workflows for publication and release.
                """
            )
        }
    }
}



mavenPublishing {
    coordinates("io.github.the-best-is-best", "kfirebase-auth", libs.versions.me.get())

    publishToMavenCentral(SonatypeHost.S01, true)
    signAllPublications()

    pom {
        name.set("KFirebaseAuth")
        description.set("KFirebaseAuth is a Kotlin Multiplatform library designed to streamline the integration of Firebase services in your mobile applications. With this library, developers can effortlessly initialize Firebase for both Android and iOS, enabling a unified and efficient development experience.")
        url.set("https://github.com/the-best-is-best/KFirebase")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        issueManagement {
            system.set("GITGUB")
            url.set("https://github.com/the-best-is-best/KFirebase")
        }
        scm {
            connection.set("https://github.com/the-best-is-best/KFirebase.git")
            url.set("https://github.com/the-best-is-best/KFirebase")
        }
        developers {
            developer {
                id.set("MichelleRaouf")
                name.set("Michelle Raouf")
                email.set("eng.michelle.raouf@gmail.com")

            }
        }
    }

}


signing {
    useGpgCmd()
    sign(publishing.publications)
}


kotlin {
    jvmToolchain(17)
    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    //  jvm()
//
//    js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "KFirebaseAuth"
            isStatic = true
        }
    }
    cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"

        // Optional properties
        // Configure the Pod name here instead of changing the Gradle project name
        name = "KFirebaseAuth"

        framework {
            baseName = "KFirebaseAuth"
        }
        noPodspec()
        ios.deploymentTarget =
            libs.versions.iosDeploymentTarget.get()  // Update this to the required version

        pod("FirebaseAuth") {
            version = libs.versions.podFirebase.get()
            extraOpts += listOf("-compiler-option", "-fmodules")

        }


    }

    sourceSets {
        all {
            languageSettings.apply {
                this.apiVersion = libs.versions.settings.api.get()
                this.languageVersion = libs.versions.settings.language.get()
                progressiveMode = true
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                if (name.lowercase().contains("ios")) {
                    optIn("kotlinx.cinterop.ExperimentalForeignApi")
                    optIn("kotlinx.cinterop.BetaInteropApi")
                }
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(project(":FirebaseAnalytics"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.runtime)


        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.firebase.common.ktx)
            //noinspection GradleDependency
            implementation(project.dependencies.platform(libs.firebase.bom))

            implementation(libs.firebase.auth)

        }





        iosMain.dependencies {
        }


    }
}

android {
    namespace = "io.github.KFirebaseAuth"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        buildFeatures {
            //enables a Compose tooling support in the AndroidStudio
            compose = true
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.KFirebaseAuth.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}

tasks.register<Copy>("updateReadme") {
    doLast {
        val version = libs.versions.me.get()
        val readmeFile = file("README.md")
        val content =
            readmeFile.readText().replace(Regex("Me Library: .*"), "Me Library: `$version`")
        readmeFile.writeText(content)
    }
}