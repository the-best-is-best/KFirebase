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
    coordinates("io.github.the-best-is-best", "kfirebase-messaging", libs.versions.me.get())

    publishToMavenCentral(SonatypeHost.S01, true)
    signAllPublications()

    pom {
        name.set("KFirebaseMessaging")
        description.set("KFirebaseMessaging is a Kotlin Multiplatform Mobile (KMM) package that simplifies the integration of Firebase Cloud Messaging (FCM) across Android and iOS platforms. It provides a unified API for handling push notifications and FCM messaging in a shared codebase, allowing developers to seamlessly implement FCM functionality for both platforms without duplicating code.")
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
            baseName = "KFirebaseMessaging"
            isStatic = true
        }
    }
    cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"

        // Optional properties
        // Configure the Pod name here instead of changing the Gradle project name
        name = "KFirebaseMessaging"

        framework {
            baseName = "KFirebaseMessaging"
        }
        noPodspec()
        ios.deploymentTarget =
            libs.versions.iosDeploymentTarget.get()  // Update this to the required version

        pod("KFirebaseMessaging") {
            version = "0.1.0-rc.1"
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

            implementation(project(":FirebaseAnalytics"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.runtime)

        }



        androidMain.dependencies {
            implementation(libs.firebase.common.ktx)
            implementation(libs.firebase.messaging)
            implementation(libs.gson)
            implementation(libs.kpermissions)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(project(":FirebaseCore"))
            //noinspection GradleDependency
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.messaging.directboot)

        }





        iosMain.dependencies {
        }

    }
}

android {
    namespace = "io.github.KFirebaseMessaging"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

    }
}