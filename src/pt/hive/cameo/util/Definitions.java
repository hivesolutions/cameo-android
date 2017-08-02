package pt.hive.cameo.util;

import java.util.Map;

public class Definitions {

    /**
     * The default URL to be used to server th httpbin
     * service, should be public and available.
     */
    public static final String HTTPBIN_HOST = "httpbin.org";

    public static String getHttpBinUrl() {
        return getHttpBinUrl("http://");
    }

    public static String getHttpBinUrl(boolean secure) {
        return secure ? getHttpBinUrl("https://") : getHttpBinUrl("http://");
    }

    public static String getHttpBinUrl(String prefix) {
        Map<String, String> environ = System.getenv();
        String httpBinHost = environ.containsKey("HTTPBIN") ?
                environ.get("HTTPBIN") : HTTPBIN_HOST;
        return prefix + httpBinHost;
    }

}
