import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.security.MessageDigest

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.dokka)
}

android {
    namespace = "com.kollider.engine"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvm()
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    js(IR) { browser {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("kollider") {
            groupId = "com.kollider"
            artifactId = "engine"
            version = "1.0.0"
            from(components["kotlin"])
        }
    }
}

val dokkaHtml by tasks.existing(DokkaTask::class) {
    outputDirectory.set(rootProject.layout.projectDirectory.dir("docs/api"))

    val sourcesTree = project.layout.projectDirectory.dir("src").asFileTree.matching {
        include("**/*.kt", "**/*.kts", "**/*.java")
    }
    val sourcesHashFile = rootProject.layout.buildDirectory.file("dokka/${project.name}-sources.sha")
    var cachedHash: String? = null

    fun computeSourcesHash(): String {
        cachedHash?.let { return it }

        val digest = MessageDigest.getInstance("SHA-256")
        val files = sourcesTree.files
            .filter { it.isFile }
            .sortedBy { it.relativeTo(project.projectDir).path }

        for (file in files) {
            val relativePath = file.relativeTo(project.projectDir).path.toByteArray()
            digest.update(relativePath)
            digest.update(file.readBytes())
        }

        val hash = digest.digest().joinToString("") { byte -> "%02x".format(byte) }
        cachedHash = hash
        return hash
    }

    onlyIf {
        val currentHash = computeSourcesHash()
        val storedHash = sourcesHashFile.get().asFile.takeIf { it.exists() }?.readText()
        val hasChanges = currentHash != storedHash
        if (!hasChanges) {
            logger.lifecycle("Skipping dokkaHtml; Kotlin sources unchanged.")
        }
        hasChanges
    }

    doFirst {
        val output = outputDirectory.get().asFile
        if (output.exists()) {
            output.deleteRecursively()
        }
        output.mkdirs()
    }

    doLast {
        val hashFile = sourcesHashFile.get().asFile
        hashFile.parentFile.mkdirs()
        hashFile.writeText(computeSourcesHash())
    }
}

tasks.named("build") {
    dependsOn(dokkaHtml)
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    finalizedBy(dokkaHtml)
}
