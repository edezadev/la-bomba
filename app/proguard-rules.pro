# ===== FIREBASE RULES =====
-keepnames class com.firebase.** { *; }
-keepnames class com.google.firebase.** { *; }
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-keepclasseswithmembers class * {
    @com.google.firebase.firestore.PropertyName *;
}

# ===== APP MODELS =====
-keep class com.edeza.labomba.models.** { *; }
-keep class com.edeza.labomba.config.** { *; }

# ===== KOTLIN METADATA =====
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# ===== CRASH REPORTING =====
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== GOOGLE LIBRARIES =====
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-keep class com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }

# ===== VIEW BINDING =====
-keepclasseswithmembernames class * {
    native <methods>;
}

# ===== ADS & MEDIA3 =====
-keep class com.google.android.gms.ads.** { *; }
-keep class androidx.media3.** { *; }