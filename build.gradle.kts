import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id("org.jetbrains.kotlin.js") version "1.3.72"
}

group = "org.wildfly.halos"
version = "0.0.1"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.5")
    implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
    implementation("io.github.microutils:kotlin-logging-js:1.7.9")

    implementation(npm("styled-components"))
    implementation(npm("inline-style-prefixer"))
    implementation(npm("css-loader", "3.5.3"))
    implementation(npm("file-loader", "6.0.0"))
    implementation(npm("style-loader", "1.2.1"))
    implementation(npm("react", "16.13.1"))
    implementation(npm("react-dom", "16.13.1"))
    implementation(npm("@patternfly/patternfly", "2.71.6"))
}

kotlin.target.browser {
    runTask {
        devServer = KotlinWebpackConfig.DevServer(
            port = 9999,
            contentBase = listOf("$buildDir/processedResources/Js/main")
        )
    }
}