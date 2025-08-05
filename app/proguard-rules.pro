# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Dagger Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-dontwarn dagger.**
-keep class javax.inject.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep class com.cabovianco.remindme.data.local.entity.** { *; }
-keep interface com.cabovianco.remindme.data.local.dao.** { *; }
-keepattributes *Annotation*
-dontwarn androidx.room.**

# ViewModels
-keep class com.cabovianco.remindme.presentation.viewmodel.** { *; }

# Domain
-keep class com.cabovianco.remindme.domain.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity { *; }
-dontwarn androidx.compose.**

# Entry points
-keep class com.cabovianco.remindme.App { *; }
-keep class com.cabovianco.remindme.MainActivity { *; }
