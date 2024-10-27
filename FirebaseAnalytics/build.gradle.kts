import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
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
    coordinates("io.github.the-best-is-best", "kfirebase-analytics", libs.versions.me.get())

    publishToMavenCentral(SonatypeHost.S01 , true)
    signAllPublications()

    pom {
        name.set("KFirebaseAnalytics")
        description.set("KFirebaseAnalytics is a Kotlin Multiplatform Mobile (KMM) package designed to provide seamless integration with Firebase Analytics across both Android and iOS platforms. This package allows developers to easily track user events, monitor app performance, and gain insights into user behavior through a unified API, without duplicating code for each platform.")
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
            baseName = "KFirebaseAnalytics"
            isStatic = true
        }
    }
    cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"

        // Optional properties
        // Configure the Pod name here instead of changing the Gradle project name
        name = "KFirebaseAnalytics"

        framework {
            baseName = "KFirebaseAnalytics"
        }
        noPodspec()
        ios.deploymentTarget =
            libs.versions.iosDeploymentTarget.get()  // Update this to the required version

        pod("FirebaseAnalytics") {
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

        }



        androidMain.dependencies {

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.common.ktx)
            implementation(libs.firebase.analytics)
            implementation(project(":FirebaseCore"))

        }





        iosMain.dependencies {
        }

    }
}

android {
    namespace = "io.github.KFirebaseAnalytics"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

    }
}
