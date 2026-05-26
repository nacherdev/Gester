import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "com.example.gester"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gester"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Formato estricto para Kotlin DSL
            buildConfigField("String", "DB_ADMIN_USER", "\"${localProperties.getProperty("DB_ADMIN_USER") ?: ""}\"")
            buildConfigField("String", "DB_ADMIN_PASS", "\"${localProperties.getProperty("DB_ADMIN_PASS") ?: ""}\"")
        }
        getByName("debug") {
            buildConfigField("String", "DB_ADMIN_USER", "\"${localProperties.getProperty("DB_ADMIN_USER") ?: ""}\"")
            buildConfigField("String", "DB_ADMIN_PASS", "\"${localProperties.getProperty("DB_ADMIN_PASS") ?: ""}\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)

    implementation("mysql:mysql-connector-java:5.1.49")
    implementation("com.google.code.gson:gson:2.10.1")
}