plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bookmoth"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bookmoth"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("BookMoth.jks")
            storePassword = "123456"
            keyAlias = "bookmoth"
            keyPassword = "123456"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    // xử lý xung đột file META-INF
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("mozilla/public-suffix-list.txt")
    }
}

dependencies {

    implementation(libs.play.services.auth)
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Firebase dependencies
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics.buildtools)

    // Android libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // OkHttp & JSON
    implementation(libs.okhttp)

    // Other libraries
    implementation(libs.glide)
    implementation(libs.security.crypto)
    annotationProcessor(libs.compiler)
    implementation(files("zpdk-release-v3.1.aar"))

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // retrofit

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.8")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.8")

    implementation("androidx.security:security-crypto:1.0.0")

    // zoomview
    implementation("com.jsibbold:zoomage:1.3.1")

    //recyclerview
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    //picasso
    implementation ("com.squareup.picasso:picasso:2.8")
}
