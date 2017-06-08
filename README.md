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
    compile "pt.hive.cameo:cameo_android:0.1.4"
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
