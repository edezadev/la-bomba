# ==============================================================
# PROGUARD RULES - La Bomba
# App: com.edeza.labomba
# ==============================================================

# ===== KOTLIN =====
-keepattributes *Annotation*
-keepattributes Signature
-keep class kotlin.Metadata { *; }
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# ===== KOTLIN COROUTINES =====
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ===== CRASH REPORTING =====
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== APP MODELS Y CONFIG =====
-keep class com.edeza.labomba.models.** { *; }
-keep class com.edeza.labomba.config.** { *; }

# ===== FIRESTORE =====
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.IgnoreExtraProperties *;
    @com.google.firebase.firestore.ThrowOnExtraProperties *;
}
-keepclassmembers class com.edeza.labomba.models.** {
    public <init>();
}

# ===== ADMOB =====
-dontwarn com.google.android.gms.ads.**

# ===== MEDIA3 / EXOPLAYER =====
-keepclassmembers class androidx.media3.exoplayer.audio.AudioSink { *; }
-keep class androidx.media3.common.util.LibraryLoader { *; }
-dontwarn androidx.media3.**

# ===== EXCEPTIONS =====
-keepattributes Exceptions

# ===== VIEW BINDING =====
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# ===== PARCELABLES =====
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ===== ENUMS =====
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}