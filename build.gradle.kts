import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer

plugins {
    kotlin("js") version "1.4.10"
}

group = "org.wildfly.halos"
version = "0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.jfrog.org/artifactory/jfrog-dependencies")
    jcenter()
}

dependencies {
    implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.112-kotlin-1.4.0")
    implementation("dev.fritz2:core:0.8-SNAPSHOT")
    implementation("dev.fritz2:elemento:0.0.2")
    implementation("dev.fritz2:mvp:0.0.1")
    implementation("org.patternfly:patternfly-fritz2:0.0.1")
    implementation(npm("@github/time-elements", "3.1.1"))
    implementation(npm("@patternfly/patternfly", "4.23.3"))
    implementation(npm("clipboard", "2.0.6"))
    implementation(npm("highlight.js", "10.1.1"))
    implementation(devNpm("file-loader", "6.0.0"))
}

kotlin {
    js {
        compilations.named("main") {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
            }
        }
        browser {
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
                devServer = DevServer(
                    port = 9999,
                    contentBase = listOf("$buildDir/processedResources/js/main")
                )
            }
        }
        binaries.executable()
    }
}
