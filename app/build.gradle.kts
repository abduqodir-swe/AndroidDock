plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.androiddock.prototype"
    compileSdk = 36
    buildFeatures { compose = true }

    defaultConfig {
        applicationId = "dev.androiddock.prototype"
        // Temporary prototype values; the final decision belongs in ADR-002.
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.viewmodel)
    testImplementation(libs.junit)
}
