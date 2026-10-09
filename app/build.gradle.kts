plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev.androiddock.prototype"
    compileSdk = 36

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
    testImplementation(libs.junit)
}
