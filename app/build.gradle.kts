plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.rpgapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rpgapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
        // BOM (Bill of Materials) para manter as versões consistentes
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.material3)

        // Dependências do Compose
        implementation(libs.ui)
        implementation(libs.ui.graphics)
        implementation(libs.ui.tooling.preview)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.junit.ktx)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.core.ktx)
        implementation(libs.androidx.core)
        implementation(libs.androidx.core)
        implementation(libs.androidx.room.common)
        implementation(libs.androidx.room.runtime.android)
        testImplementation(libs.junit.junit)
        androidTestImplementation(libs.junit.junit)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt("androidx.room:room-compiler:2.6.1")

    // Coroutines para Android e Core
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)




    // Ferramentas de teste e depuração do Compose
        debugImplementation(libs.ui.tooling)
        debugImplementation(libs.ui.test.manifest)

        // Dependências para testes unitários
        testImplementation(libs.junit)  // Adicione esta linha
    }


