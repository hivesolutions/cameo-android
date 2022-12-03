# [Cameo Android](http://cameo-android.hive.pt/)

The Android version of the Cameo Framework.

## Usage

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    implementation "com.github.hivesolutions:cameo-android:0.4.2"
}
```

```java
ProxyRequest.setBaseUrl(this, "http://api.service.com/")
ProxyRequest.setLoginPath("api/login.json");
ProxyRequest.setLoginLogo(R.drawable.logo);
ProxyRequest.request(this, "api/ping.json", new ProxyRequestDelegate() {
    @Override
    public void didReceiveJson(JSONObject data) {
    }

    @Override
    public void didReceiveError(Object error) {
    }
});
```

## Deploy

The current deployment strategy uses [JitPack](https://jitpack.io), making use of GitHub releases.

This is an easy to go solution as it only requires tagging a certain Git commit and a version is automatically made available at [https://jitpack.io/#hivesolutions/cameo-android](https://jitpack.io/#hivesolutions/cameo-android).

## References

* [How to upload library to jCenter](https://inthecheesefactory.com/blog/how-to-upload-library-to-jcenter-maven-central-as-dependency/en)
* [How to Publish Your Android Studio Library to JCenter](https://medium.com/@daniellevass/how-to-publish-your-android-studio-library-to-jcenter-5384172c4739)

## License

Cameo Android is currently licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/).

## Build Automation

[![Build Status](https://github.com/hivesolutions/cameo-android/workflows/Main%20Workflow/badge.svg)](https://github.com/hivesolutions/cameo-android/actions)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/)
