# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/brian/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify

-verbose
-optimizations !code/simplification/arithmetic,!field

-dontwarn com.fmsirvent.ParallaxEverywhere.**
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
#okio
-dontwarn okio.**

-ignorewarnings
-keep class * {
    public private *;
}

-keep class com.pkmmte.pkrss.Callback{ *; }
-dontwarn com.squareup.okhttp.**
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }
-keep public class android.util.FloatMath

-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn org.bouncycastle.**
-keepattributes InnerClasses,EnclosingMethod
-dontwarn com.felipsecsl.**
-assumenosideeffects class android.util.Log {
   public static boolean isLoggable(java.lang.String, int);
   public static int v(...);
   public static int i(...);
   public static int w(...);
   public static int d(...);
   public static int e(...);
}
#-keepclassmembernames class com.samsung.** {
#    java.lang.Class class$(java.lang.String);
#    java.lang.Class class$(java.lang.String, boolean);
#}
#
#-keepclasseswithmembernames class com.samsung.** {
#    native <methods>;
#}
-dontwarn InnerClasses
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**