import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer

plugins {
    id("org.jetbrains.kotlin.js") version "1.4.0"
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
    implementation("org.jetbrains:kotlin-react:16.13.1-pre.112-kotlin-1.4.0")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.112-kotlin-1.4.0")
    implementation("dev.fritz2:core:0.8-SNAPSHOT")
    implementation("dev.fritz2:mvp:0.8-SNAPSHOT")
    implementation("org.patternfly:patternfly-fritz2:0.1-SNAPSHOT")
    implementation(npm("@patternfly/patternfly", "4.23.3"))
    implementation(devNpm("file-loader", "6.0.0"))
}

kotlin {
    js {
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
