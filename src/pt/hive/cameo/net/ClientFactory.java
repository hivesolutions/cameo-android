/*
 Hive Cameo Framework
 Copyright (C) 2008-2014 Hive Solutions Lda.

 This file is part of Hive Cameo Framework.

 Hive Cameo Framework is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Hive Cameo Framework is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Hive Cameo Framework. If not, see <http://www.gnu.org/licenses/>.

 __author__    = João Magalhães <joamag@hive.pt>
 __version__   = 1.0.0
 __revision__  = $LastChangedRevision$
 __date__      = $LastChangedDate$
 __copyright__ = Copyright (c) 2008-2014 Hive Solutions Lda.
 __license__   = GNU General Public License (GPL), Version 3
 */

package pt.hive.cameo.net;

import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import pt.hive.cameo.ssl.SSLSocketFactory;

public class ClientFactory {

    public static DefaultHttpClient getHttpClient() {
        // creates a new instance of the basic http parameters and then
        // updates the parameter with a series of pre-defined options that
        // are defined as the default ones for this factory
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.strict-transfer-encoding",
                false);
        params.setBooleanParameter("http.protocol.expect-continue", false);

        // creates the registry object and registers both the secure and the
        // insecure http mechanisms for the respective socket factories
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("https", new SSLSocketFactory(), 443));

        // creates the manager entity using the creates parameters structure
        // and the registry for socket factories
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
                params, registry);

        // creates the default http client using the created thread safe manager
        // with the provided parameters and registry (as expected) and then
        // returns the new client to the caller method so that it may be used
        DefaultHttpClient client = new DefaultHttpClient(manager, params);
        return client;
    }
}
