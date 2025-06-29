import com.android.build.gradle.internal.cxx.io.removeDuplicateFiles
import com.diffplug.gradle.spotless.SpotlessPlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) version "2.1.20" apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.spotless) apply false
    id("com.google.devtools.ksp") version "2.1.20-1.0.32" apply false
    id("com.google.dagger.hilt.android") version "2.56.1" apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}

subprojects {
    apply<SpotlessPlugin>()
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        format("misc") {
            target("*.gradle", "*.md", ".gitignore")
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }

        java {
            target("**/*.java")
            googleJavaFormat().formatJavadoc(true)
            removeUnusedImports()
            formatAnnotations()
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlin {
            target("**/*.kt")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}