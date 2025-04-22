plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.demo"
        minSdk = 24
        targetSdk = 35
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

    signingConfigs {
        create("release") {
            storeFile = file("../key.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD") ?: getLocalProperty("STORE_PASSWORD", "")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: getLocalProperty("KEY_ALIAS", "")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD") ?: getLocalProperty("KEY_PASSWORD", "")
        }
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

}

// Helper function to read local properties
fun getLocalProperty(key: String, defaultValue: String): String {
    val properties = java.util.Properties()
    val localProperties = file("../local.properties")
    if (localProperties.exists()) {
        properties.load(localProperties.inputStream())
        return properties.getProperty(key, defaultValue)
    }
    return defaultValue
}
