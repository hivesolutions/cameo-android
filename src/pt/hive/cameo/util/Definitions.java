/*
 Hive Cameo Framework
 Copyright (c) 2008-2019 Hive Solutions Lda.

 This file is part of Hive Cameo Framework.

 Hive Cameo Framework is free software: you can redistribute it and/or modify
 it under the terms of the Apache License as published by the Apache
 Foundation, either version 2.0 of the License, or (at your option) any
 later version.

 Hive Cameo Framework is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 Apache License for more details.

 You should have received a copy of the Apache License along with
 Hive Cameo Framework. If not, see <http://www.apache.org/licenses/>.

 __author__    = João Magalhães <joamag@hive.pt>
 __version__   = 1.0.0
 __revision__  = $LastChangedRevision$
 __date__      = $LastChangedDate$
 __copyright__ = Copyright (c) 2008-2019 Hive Solutions Lda.
 __license__   = Apache License, Version 2.0
 */

package pt.hive.cameo.util;

import java.util.Map;

/**
 * Should contain a series of global definitions (mostly) string
 * to be used in general purpose operations.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
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
