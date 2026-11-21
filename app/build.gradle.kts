plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dz.ooredoo.connectionkeeper"
    compileSdk = 35

    defaultConfig {
        applicationId = "dz.ooredoo.connectionkeeper"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
