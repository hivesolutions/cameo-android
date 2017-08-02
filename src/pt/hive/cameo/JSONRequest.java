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

package pt.hive.cameo;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Main class for the remote HTTP request that is responsible
 * for handling the connection and the serialization/deserialization.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class JSONRequest {

    /**
     * The delegate that is going to be notified about the changes
     * in the state for the connection (success and failure).
     */
    private JSONRequestDelegate delegate;

    /**
     * The (Android) context that is going to be used for the retrieval
     * of some global (application) values (eg: settings).
     */
    private Context context;

    /**
     * The string based URL to be used in the JSON request, should be a full
     * and canonical URL value.
     */
    private String url;

    /**
     * A list contain a series of list for the complete parameters to be sent
     * in the request, this is going to be either sent via GET parameter if
     * there's no payload or as URLEncoded if it's a POST request.
     */
    private List<List<String>> parameters;

    /**
     * The name of the HTTP method (eg: GET, POST, DELETE, etc.) that is going
     * to be used in the JSON request.
     */
    private String requestMethod;

    /**
     * The body as a JSON object to be sent as the payload of the request.
     */
    private JSONObject body;

    public JSONRequest() {
    }

    public JSONRequest(String url) {
        this();
        this.url = url;
    }

    public JSONRequest(String url, List<List<String>> parameters) {
        this(url);
        this.parameters = parameters;
    }

    /**
     * Converts the provided input stream into a valid string sequence eligible
     * to be used by more conventional method.
     *
     * @param stream The stream that is going to be used for reading the complete
     *               set of data and convert it into a "simple" string value.
     * @return The final string value retrieved from the input stream.
     * @throws IOException
     */
    private static String convertStreamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line + "\n");
            }
        } finally {
            stream.close();
        }
        return builder.toString();
    }

    public String load() {
        try {
            return this.execute();
        } catch (final Exception exception) {
            if (this.delegate != null) {
                this.delegate.didReceiveError(exception);
            }
        }
        return null;
    }

    public String execute() throws IOException, JSONException {
        // defines the default result value as simple null value
        String result = null;

        // constructs the final URL string value according to the provided
        // set of parameters defined in the JSON request instance
        String url = this.constructUrl();

        // uses the constructed URL string value to create a URL instance
        // and then uses that same instance to build an HTTP URL connection
        URL _url = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) _url.openConnection();

        // sets a series of general diagnostics purpose headers related with
        // the current device (eg: OS version, manufacturer, model, etc.)
        if (android.os.Build.MODEL != null) {
            urlConnection.setRequestProperty("X-Android-Model", android.os.Build.MODEL);
        }
        if (android.os.Build.MANUFACTURER != null) {
            urlConnection.setRequestProperty("X-Android-Manufacturer", android.os.Build.MANUFACTURER);
        }
        if (android.os.Build.PRODUCT != null) {
            urlConnection.setRequestProperty("X-Android-Product", android.os.Build.PRODUCT);
        }
        if (android.os.Build.VERSION.BASE_OS != null) {
            urlConnection.setRequestProperty("X-Android-Version", android.os.Build.VERSION.BASE_OS);
        }

        // in case the request method is defined sets it on the
        // current URL connection value
        if (this.requestMethod != null) {
            urlConnection.setRequestMethod(this.requestMethod);
        }

        // in case there's a valid body payload defined sets that same payload
        // in the current URL connection
        if (this.body != null) {
            this.writeBody(urlConnection);
        }

        // creates the buffered input stream from the input stream that is
        // "exposed" by the URL connection
        InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

        try {
            // retrieves the contents from the input stream and then converts
            // this same result into a string based result value
            result = JSONRequest.convertStreamToString(stream);
        } finally {
            // closes the base stream as it's not longer going to be used for
            // any kind of read operation
            stream.close();
        }

        // creates a JSON object from the provided data (may raise exception)
        // this object may be safely used for JSON structured operations
        JSONObject data = new JSONObject(result);

        // verifies if there's a delegate currently defined and if that's
        // the case calls it using the proper strategy
        if (this.delegate != null) {
            this.delegate.didReceiveJson(data);
        }

        // returns the final string based result (contents) to
        // the caller method
        return result;
    }

    /**
     * Constructs the complete URL value taking into account the
     * currently defined parameters that are going to be part as
     * GET based values/parameters.
     *
     * @return The string containing the complete URL with the
     * GET parameters already included.
     */
    private String constructUrl() {
        if (this.parameters == null || this.parameters.isEmpty()) {
            return this.url;
        }
        String parameters = this.constructParameters();
        String url = String.format("%s?%s", this.url, parameters);
        return url;
    }

    private String constructParameters() {
        StringBuffer buffer = new StringBuffer();
        for (List<String> parameter : this.parameters) {
            String parameterS = String.format("%s=%s&", parameter.get(0), parameter.get(1));
            buffer.append(parameterS);
        }
        return buffer.toString();
    }

    private void writeBody(URLConnection urlConnection) throws IOException {
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        OutputStream output = urlConnection.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(output);
        writer.write(body.toString());
        writer.flush();
    }

    public JSONRequestDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(JSONRequestDelegate delegate) {
        this.delegate = delegate;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(List<List<String>> parameters) {
        this.parameters = parameters;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String method) {
        this.requestMethod = method;
    }

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }
}
