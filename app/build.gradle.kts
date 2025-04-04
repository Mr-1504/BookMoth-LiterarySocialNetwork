import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

val properties = Properties()
properties.load(File(rootProject.rootDir, "local.properties").inputStream())

android {
    namespace = "com.example.bookmoth"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bookmoth"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "STORE_PASSWORD", "\"${properties["storePassword"]}\"")
        buildConfigField("String", "KEY_ALIAS", "\"${properties["keyAlias"]}\"")
        buildConfigField("String", "KEY_PASSWORD", "\"${properties["keyPassword"]}\"")
        buildConfigField("String", "MAC_KEY", "\"${properties["macKey"]}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        create("release") {
            storeFile = file("BookMoth.jks")
            storePassword = properties["storePassword"] as String
            keyAlias = properties["keyAlias"] as String
            keyPassword = properties["keyPassword"] as String
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
        buildConfig = true
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
    implementation("com.google.firebase:firebase-messaging:24.1.0")

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
    implementation(libs.legacy.support.v4)
    annotationProcessor(libs.compiler)
    implementation(files("zpdk-release-v3.1.aar"))

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // retrofit

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.logging.interceptor)

    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.8")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.8")

    implementation("androidx.security:security-crypto:1.0.0")

    // zoomview
    implementation("com.jsibbold:zoomage:1.3.1")

    //recyclerview
    implementation(libs.recyclerview)

    //room database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    //picasso
    implementation("com.squareup.picasso:picasso:2.8")

    //processLifecycle
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")

    //cardview
    implementation(libs.cardview)


    //markwon
    implementation ("io.noties.markwon:core:4.6.2")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")


}
