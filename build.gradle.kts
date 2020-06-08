import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer

plugins {
    id("org.jetbrains.kotlin.js") version "1.4-M2"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

group = "org.wildfly.halos"
version = "0.0.1"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    maven("https://kotlin.bintray.com/kotlinx")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.7")
    implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
    implementation("io.github.microutils:kotlin-logging-js:1.7.9")

    implementation(npm("@patternfly/patternfly", "2.71.6"))
    implementation(npm("@patternfly/react-charts", "5.3.21"))
    implementation(npm("file-loader", "6.0.0"))
    implementation(npm("react", "16.13.1"))
    implementation(npm("react-dom", "16.13.1"))
    implementation(npm("styled-components", "5.1.1"))
}

ktlint {
    disabledRules.set(setOf("import-ordering"))
}

kotlin {
    js {
        browser {
            runTask {
                devServer = DevServer(
                    port = 9999,
                    contentBase = listOf("$buildDir/processedResources/Js/main")
                )
            }
        }
        binaries.executable()
    }
}
