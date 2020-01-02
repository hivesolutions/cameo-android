/*
 Hive Cameo Framework
 Copyright (c) 2008-2020 Hive Solutions Lda.

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
 __copyright__ = Copyright (c) 2008-2020 Hive Solutions Lda.
 __license__   = Apache License, Version 2.0
 */

package pt.hive.cameo;

import org.json.JSONObject;

public interface JSONRequestDelegate {

    /**
     * Delegate method called when the JSON request is completed with
     * success should provide both the reference to the request object
     * and the payload data retrieved from the request.
     *
     * @param request The JSON request object used in the request.
     * @param data    The JSON payload information retrieved from the response.
     */
    void didReceiveJson(JSONRequest request, JSONObject data);

    /**
     * Delegate method called when an error occurs performing the JSON
     * operation, should provide some information on the error.
     *
     * @param request The JSON request object used in the request.
     * @param error   The error object describing the problem.
     */
    void didReceiveError(JSONRequest request, Object error);
}
