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

import android.app.Activity;
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

public class JSONRequest {

    private JSONRequestDelegate delegate;
    private Context context;
    private Activity activity;
    private String url;
    private List<List<String>> parameters;
    private String requestMethod;
    private JSONObject body;

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
                final JSONRequest self = this;
                if (this.activity == null) {
                    self.delegate.didReceiveError(exception);
                } else {
                    this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            self.delegate.didReceiveError(exception);
                        }
                    });
                }
            }
        }
        return null;
    }

    public String execute() throws IOException, JSONException {
        String result = null;
        String url = this.constructUrl();
        URL _url = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) _url.openConnection();

        if (this.requestMethod != null) {
            urlConnection.setRequestMethod(this.requestMethod);
        }

        if (this.body != null) {
            this.writeBody(urlConnection);
        }

        InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

        try {
            result = JSONRequest.convertStreamToString(stream);
        } finally {
            stream.close();
        }

        final JSONObject data = new JSONObject(result);
        if (this.delegate != null) {
            final JSONRequest self = this;
            if (this.activity == null) {
                self.delegate.didReceiveJson(data);
            } else {
                this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        self.delegate.didReceiveJson(data);
                    }
                });
            }
        }

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

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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
