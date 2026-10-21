plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "vn.tuongduong.lichviet"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.tuongduong.lichviet"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "1.3.0"
    }

    // Ký bản phát hành: chỉ bật khi có biến môi trường (xem README.txt, mục CH Play).
    val ksFile = System.getenv("KEYSTORE_FILE")
    signingConfigs {
        if (ksFile != null && file(ksFile).exists()) {
            create("release") {
                storeFile = file(ksFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfigs.findByName("release")?.let { signingConfig = it }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
}
