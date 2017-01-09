# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/cab404/.asdk/tools/proguard/proguard-android.txt
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
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keep class org.apache.log4j.**
-keepclassmembers class org.apache.http.HttpConnection
-keepclassmembers class org.apache.http.HttpInetConnection
-keepclassmembers class org.apache.http.client.HttpClient

-dontwarn org.apache.commons.logging.**
-dontwarn com.android.internal.http.multipart.MultipartEntity

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes *Annotation*,EnclosingMethod,Signature