# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Kotlin metadata / coroutines
-keep class kotlin.Metadata { *; }
-dontwarn kotlinx.coroutines.**
