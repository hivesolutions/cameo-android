/*
 Hive Cameo Framework
 Copyright (c) 2008-2015 Hive Solutions Lda.

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
 __copyright__ = Copyright (c) 2008-2015 Hive Solutions Lda.
 __license__   = GNU General Public License (GPL), Version 3
 */

package pt.hive.cameo.net;

import org.apache.http.conn.scheme.SocketFactory;

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
        return ClientFactory.getHttpClient(true);
    }

    public static DefaultHttpClient getHttpClient(boolean strict) {
        // creates a new instance of the basic http parameters and then
        // updates the parameter with a series of pre-defined options that
        // are defined as the default ones for this factory
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.strict-transfer-encoding",
                false);
        params.setBooleanParameter("http.protocol.expect-continue", true);

        // constructs both the plain and the secure factory objects that are
        // going to be used in the registration of the plain and secure http
        // schemes, operation to be performed latter on
        SocketFactory plainFactory = PlainSocketFactory.getSocketFactory();
        SocketFactory secureFactory = strict ? org.apache.http.conn.ssl.SSLSocketFactory
                .getSocketFactory() : SSLSocketFactory.getSocketFactory();

        // creates the registry object and registers both the secure and the
        // insecure http mechanisms for the respective socket factories
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", plainFactory, 80));
        registry.register(new Scheme("https", secureFactory, 443));

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
