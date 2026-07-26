# ProGuard/R8 rules for RemindMe

-keepattributes SourceFile,LineNumberTable,Signature,EnclosingMethod,AnnotationDefault
-keep public class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

-keepattributes *Annotation*, InnerClasses
-keepclassmembers class **$serializer {
    public static ** INSTANCE;
}
-keepclassmembers class * {
    *** serializer(...);
}
-keepclassmembers class com.cabovianco.remindme.presentation.navigation.Screen** {
    *** Companion;
}

-keep class com.cabovianco.remindme.data.local.entity.** { *; }
-dontwarn androidx.room.**

-dontwarn dagger.**
-keep class javax.inject.** { *; }

-keep class com.cabovianco.remindme.domain.model.** { *; }
