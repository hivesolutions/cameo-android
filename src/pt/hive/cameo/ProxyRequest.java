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

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import pt.hive.cameo.activities.LoginActivity;

/**
 * Abstract class responsible for the handling of remote json request that may
 * or may not require and underlying authentication process.
 *
 * The handling of the authentication should be automatic and the proper panel
 * should be raised upon the invalidation of credentials.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class ProxyRequest extends AsyncTask<Void, Void, String> implements JSONRequestDelegate {

    /**
     * Constant value that defined the request login intent value that may be
     * used to identify this request intent.
     */
    public static final int LOGIN_REQUEST = 1;

    /**
     * Relative path to the URL that is going to be called in case a login
     * operation is required, note that the login action is always going to be
     * show for such operations.
     */
    private static String loginPath;

    /**
     * The reference to the resource that contains the image that is going to be
     * display in the login screen in case it's required for the request.
     */
    private static int loginLogo;

    /**
     * The reference to the delegate object that is going to be used for the
     * calling of the various callback functions for the request. This is
     * required in order to ensure a proper asynchronous approach.
     */
    private ProxyRequestDelegate delegate;

    /**
     * The context object that is going to be used for resolution of global
     * values this is required in order for the request to work properly.
     */
    private Activity activity;

    /**
     * The relative URL path for the request that is going to be performed, this
     * should be JSON based (eg: api/info.json).
     */
    private String path;

    /**
     * The various GET operation parameters that are going to be encoded to be
     * part of the request query parameters.
     */
    private List<List<String>> parameters;

    /**
     * The HTTP method to be set on the request, this should be an upper-cased
     * string like GET, POST, PUT or DELETE.
     */
    private String requestMethod;

    /**
     * The JSON object to be encoded as the body of a a payload based request
     * liek POST or PUT requests.
     */
    private JSONObject body;

    /**
     * If a proper session must be created before any remote request is done
     * using the proxy request infra-structure.
     */
    private boolean useSession;

    public ProxyRequest() {
        this.useSession = true;
    }

    public ProxyRequest(Activity activity, String path) {
        this();
        this.activity = activity;
        this.path = path;
    }

    public static ProxyRequest request(Activity activity, String path, ProxyRequestDelegate delegate) {
        ProxyRequest request = new ProxyRequest(activity, path);
        request.setDelegate(delegate);
        request.execute();
        return request;
    }

    public static boolean isReady(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        String baseUrl = preferences.getString("baseUrl", null);
        String sessionId = preferences.getString("sessionId", null);
        return baseUrl != null && sessionId != null;
    }

    public static void logout(Activity activity, String loginPath) {
        SharedPreferences preferences = activity.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("sessionId");
        editor.commit();
        ProxyRequest request = new ProxyRequest();
        request.activity = activity;
        request.showLogin();
    }

    public static void setBaseUrl(Context context, String url) {
        SharedPreferences preferences = context.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("baseUrl", url);
        editor.commit();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return this.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void didReceiveJson(JSONObject data) {
        if (this.delegate != null) {
            this.delegate.didReceiveJson(data);
        }
    }

    @Override
    public void didReceiveError(Object error) {
        if (this.delegate != null) {
            this.delegate.didReceiveError(error);
        }
    }

    public String load() throws IOException, JSONException {
        SharedPreferences preferences = this.activity.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        String baseUrl = preferences.getString("baseUrl", null);
        String sessionId = preferences.getString("sessionId", null);
        String urlString = String.format("%s%s", baseUrl, this.path);

        if (this.useSession && ProxyRequest.isReady(this.activity) == false) {
            this.showLogin();
            return null;
        }

        List<List<String>> parameters = new LinkedList<List<String>>();
        if (this.parameters != null) {
            parameters.addAll(this.parameters);
        }

        if (this.useSession) {
            parameters.add(new LinkedList<String>(Arrays.asList("session_id", sessionId)));
        }

        JSONRequest request = new JSONRequest();
        request.setDelegate(this);
        request.setActivity(this.activity);
        request.setUrl(urlString);
        request.setParameters(parameters);
        request.setRequestMethod(this.requestMethod);
        request.setBody(this.body);
        return request.load();
    }

    public void showLogin() {
        // creates the intent object that represents the login
        // activity and then starts it (pushing it into the screen)
        Intent intent = new Intent(this.activity, LoginActivity.class);
        intent.putExtra("LOGIN_PATH", ProxyRequest.loginPath);
        intent.putExtra("LOGO_ID", ProxyRequest.loginLogo);
        this.activity.startActivityForResult(intent, ProxyRequest.LOGIN_REQUEST);
    }

    public static String getLoginPath() {
        return loginPath;
    }

    public static void setLoginPath(String loginPath) {
        ProxyRequest.loginPath = loginPath;
    }

    public static int getLoginLogo() {
        return loginLogo;
    }

    public static void setLoginLogo(int loginLogo) {
        ProxyRequest.loginLogo = loginLogo;
    }

    public ProxyRequestDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(ProxyRequestDelegate delegate) {
        this.delegate = delegate;
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

    public boolean isUseSession() {
        return useSession;
    }

    public void setUseSession(boolean useSession) {
        this.useSession = useSession;
    }
}
