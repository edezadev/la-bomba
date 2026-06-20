import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
val properties = Properties()
val propertiesFile = project.rootProject.file("local.properties")
if (propertiesFile.exists()) {
    properties.load(propertiesFile.inputStream())
}

fun getProperty (key: String, default: String = ""): String = properties.getProperty(key) ?: default

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.edeza.labomba"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.edeza.labomba"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // El manifest usará por defecto el ID de debug
        manifestPlaceholders["admobAppId"] = getProperty("ADMOB_APP_ID_DEBUG", "ca-app-pub-3940256099942544~3347511713")
    }

    buildTypes {
        debug {
            val testId = "\"${getProperty("ADMOB_INTERSTITIAL_DEBUG", "ca-app-pub-3940256099942544/1033173712")}\""
            buildConfigField("String", "ID_ADS_INSTRUCTIONS", testId)
            buildConfigField("String", "ID_ADS_STARTGAME", testId)
            buildConfigField("String", "ID_ADS_RESULTS", testId)
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            manifestPlaceholders["admobAppId"] = getProperty("ADMOB_APP_ID")
            buildConfigField("String", "ID_ADS_INSTRUCTIONS", "\"${getProperty("ADMOB_INTERSTITIAL_INSTRUCTIONS")}\"")
            buildConfigField("String", "ID_ADS_STARTGAME", "\"${getProperty("ADMOB_INTERSTITIAL_STARTGAME")}\"")
            buildConfigField("String", "ID_ADS_RESULTS", "\"${getProperty("ADMOB_INTERSTITIAL_RESULTS")}\"")

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

    kotlin{
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.core:core-splashscreen:1.0.1")
    //Implementar NestedScroll
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    /* Importar el Firebase BoM, con esto la app siempre utilizará las versiones compatibles
     * de las bibliotecas de Firebase para Android */
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    // Dependencia para Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Dependencia para Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    // Dependencia para Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")
    // Dependencias para Media3 ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    // Dependencia para AdMob
    implementation("com.google.android.gms:play-services-ads:24.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}