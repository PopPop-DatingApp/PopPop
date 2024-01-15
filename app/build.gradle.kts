plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.poppop"
    compileSdk = 34

    viewBinding{
        enable = true
    }


    defaultConfig {
        applicationId = "com.example.poppop"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures{
        viewBinding = true
        viewBinding{
            enable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }


}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.fragment:fragment:1.6.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.firebaseui:firebase-ui-storage:7.2.0")
    implementation("androidx.cardview:cardview:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.api-client:google-api-client:2.2.0")
    implementation ("com.google.http-client:google-http-client-jackson2:1.43.1")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.20.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.github.yuyakaido:CardStackView:v2.3.4")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.stripe:stripe-android:20.36.1")
    implementation ("com.github.yuyakaido:CardStackView:v2.3.4")
}