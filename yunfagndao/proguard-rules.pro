# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/tsang/Downloads/android/sdk/tools/proguard/proguard-android.txt
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

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

 #retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

 #高德地图
 -dontwarn com.amap.**
 -keep  class com.amap.api.mapcore.**{*;}
 -keep  class com.amap.api.maps.**{*;}
 -keep  class com.autonavi.amap.mapcore.*{*;}
 -keep  class com.amap.api.services.**{*;}
 -keep  class com.amap.api.location.**{*;}
 -keep  class com.amap.api.fence.**{*;}
 -keep  class com.autonavi.aps.amapapi.model.**{*;}
 -keep  class com.amap.api.navi.**{*;}
 -keep  class com.autonavi.**{*;}

 -dontwarn rx.**
 -keep class rx.** { *; }
 -dontwarn okhttp3.**
 -keep class okhttp3.** { *; }
 -dontwarn okio.**
 -keep class okio.** { *; }

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

-keep class com.vrd.tech.yfd.entity.** {*;}
-keep class cn.aigestudio.** {*;}

 -dontwarn com.google.gson.**

 -dontwarn android.support.**

 -dontoptimize
 -dontpreverify

 -dontwarn cn.jpush.**
 -keep class cn.jpush.** { *; }

 -dontwarn com.google.**
 -keep class com.google.protobuf.** {*;}

