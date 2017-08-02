/*
 Hive Cameo Framework
 Copyright (c) 2008-2017 Hive Solutions Lda.

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
 __copyright__ = Copyright (c) 2008-2017 Hive Solutions Lda.
 __license__   = Apache License, Version 2.0
 */

package pt.hive.cameo.util;

import android.os.Build;

/**
 * Environment related utilities to be used as short hand calls to more complex
 * behavior or validating functions.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class Environment {

    /**
     * Verifies if the current running Android environment is considered to be
     * an Honeycomb (Android 3.0) or a more up-to-date system.
     *
     * @return If the current environment in running is at least Honeycomb.
     */
    public static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Verifies if the current running Android environment is considered to be
     * an Lollipop (Android 5.0) or a more up-to-date system.
     *
     * @return If the current environment in running is at least Lollipop.
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Verifies if the current running Android environment is considered to be
     * an Marshmallow (Android 6.0) or a more up-to-date system.
     *
     * @return If the current environment in running is at least Marshmallow.
     */
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
