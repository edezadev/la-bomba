plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.labombav2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.labombav2"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    //Implementar NestedScroll
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    /* Importar el Firebase BoM, con esto la app siempre utilizará las versiones compatibles
     * de las bibliotecas de Firebase para Android */
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    // Dependencia para Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Dependencia para Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    // Dependencia para Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")
    // Dependencias para Media3 ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}