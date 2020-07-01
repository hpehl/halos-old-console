plugins {
    id("dev.fritz2.fritz2-gradle") version "0.5"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    kotlin("plugin.serialization") version "1.3.70"
}

group = "org.wildfly.halos"
version = "0.0.1"
val fritzVersion = "0.5"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

ktlint {
    disabledRules.set(setOf("import-ordering"))
}

kotlin {
    kotlin {
        jvm()
        js {
            browser {
                runTask {
                    devServer = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer(
                        port = 9999,
                        contentBase = listOf("$buildDir/processedResources/js/main")
                    )
                }
            }
        }

        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(kotlin("stdlib"))
                }
            }
            val jvmMain by getting {
                dependencies {
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.109-kotlin-1.3.72")
                    implementation("org.jetbrains:kotlin-react:16.13.1-pre.109-kotlin-1.3.72")
                    implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.109-kotlin-1.3.72")
                    implementation(npm("@patternfly/patternfly", "4.10.31"))
                    implementation(npm("@patternfly/react-charts", "6.3.9"))
                    // dev dependencies
                    implementation(npm("css-loader", "3.6.0"))
                    implementation(npm("file-loader", "6.0.0"))
                    implementation(npm("style-loader", "1.2.1"))
                }
            }
        }
    }
}
