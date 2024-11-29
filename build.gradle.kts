plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    //kotlin("plugin.compose") version "2.0.21-firework.28" // <- Use special builds of Kotlin/Compose Compiler

//    kotlin("multiplatform") version "2.0.21-firework.28" apply false
//    kotlin("plugin.compose") version "2.0.21-firework.28" apply false
//    //id("org.jetbrains.compose") apply false
//    id("org.jetbrains.compose-hot-reload") version "1.0.0-dev.28.4" apply false

}