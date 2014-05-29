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

package pt.hive.cameo.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class SSLSocketFactory implements SocketFactory, LayeredSocketFactory {

    private SSLContext sslContext;

    @Override
    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(SSLSocketFactory.class));
    }

    @Override
    public int hashCode() {
        return SSLSocketFactory.class.hashCode();
    }

    public static SocketFactory getSocketFactory() {
        return new SSLSocketFactory();
    }

    public Socket connectSocket(Socket sock, String host, int port,
            InetAddress localAddress, int localPort, HttpParams params)
            throws IOException, UnknownHostException, ConnectTimeoutException {
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        SSLSocket sslSocket = (SSLSocket) ((sock != null) ? sock
                : createSocket());

        if ((localAddress != null) || (localPort > 0)) {
            if (localPort < 0) {
                localPort = 0;
            }
            InetSocketAddress isa = new InetSocketAddress(localAddress,
                    localPort);
            sslSocket.bind(isa);
        }

        sslSocket.connect(remoteAddress, connTimeout);
        sslSocket.setSoTimeout(soTimeout);
        return sslSocket;

    }

    public Socket createSocket() throws IOException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        return this.getSSLContext().getSocketFactory()
                .createSocket(socket, host, port, autoClose);
    }

    public boolean isSecure(Socket socket) throws IllegalArgumentException {
        return true;
    }

    private static SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null,
                    new TrustManager[] { new TrivialTrustManager() }, null);
            return context;
        } catch (Exception exception) {
            throw new IOException(exception.getMessage());
        }
    }

    private SSLContext getSSLContext() throws IOException {
        if (this.sslContext == null) {
            this.sslContext = SSLSocketFactory.createEasySSLContext();
        }
        return this.sslContext;
    }
}
