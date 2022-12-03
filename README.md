# [Cameo Android](http://cameo-android.hive.pt/)

The Android version of the Cameo Framework.

## Usage

```gradle
allprojects {
    repositories {
        maven {
            url "https://dl.bintray.com/joamag-hive/maven/"
        }
    }
}

dependencies {
    compile "pt.hive.cameo:cameo-android:0.2.0"
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

You should be able to deploy the Cameo Framework to the [bintray.com](http://bintray.com) repository by using:

```bash
gradle bintrayUpload
```

Make sure that you have the proper GPG key installed and that the `local.properties` is configured like:

```txt
bintray.user=$BINTRAY_USER
bintray.apikey=$BINTRAY_API_KEY
bintray.gpg.password=$GPG_KEY_PASSWORD
```

## References

* [How to upload library to jCenter](https://inthecheesefactory.com/blog/how-to-upload-library-to-jcenter-maven-central-as-dependency/en)
* [How to Publish Your Android Studio Library to JCenter](https://medium.com/@daniellevass/how-to-publish-your-android-studio-library-to-jcenter-5384172c4739)

## License

Cameo Android is currently licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/).

## Build Automation

[![Build Status](https://app.travis-ci.com/hivesolutions/cameo-android.svg?branch=master)](https://travis-ci.com/github/hivesolutions/cameo-android)
[![Build Status GitHub](https://github.com/hivesolutions/cameo-android/workflows/Main%20Workflow/badge.svg)](https://github.com/hivesolutions/cameo-android/actions)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://www.apache.org/licenses/)
