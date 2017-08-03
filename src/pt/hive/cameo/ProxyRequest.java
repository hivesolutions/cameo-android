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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pt.hive.cameo.activities.LoginActivity;

/**
 * Abstract class responsible for the handling of remote json request that may
 * or may not require and underlying authentication process.
 * <p>
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
     * The class that is going to be used as reference for the login prompt,
     * this may be overridden to provide extra functionality.
     */
    private static Class<? extends LoginActivity> loginActivity = LoginActivity.class;

    /**
     * The reference to the delegate object that is going to be used for the
     * calling of the various callback functions for the request. This is
     * required in order to ensure a proper asynchronous approach.
     */
    private ProxyRequestDelegate delegate;

    /**
     * If a dialog window should be displayed (if possible) during the request
     * lifecycle indicating the progress of the operation.
     */
    private boolean showDialog;

    /**
     * The context object that is going to be used for resolution of global
     * values this is required in order for the request to work properly.
     */
    private Context context;

    /**
     * The reference to the activity object that is going to be used for
     * UI related operations that require a visual context.
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
     * like POST or PUT requests.
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

    public ProxyRequest(Context context) {
        this();
        boolean isActivity = Activity.class.isAssignableFrom(context.getClass());
        this.context = context;
        this.activity = isActivity ? (Activity) context : null;
    }

    public ProxyRequest(Context context, String path) {
        this(context);
        this.path = path;
    }

    public static ProxyRequest request(Context context, String path, ProxyRequestDelegate delegate) {
        return request(context, path, null, null, delegate);
    }

    public static ProxyRequest request(
            Context context,
            String path,
            String requestMethod,
            JSONObject body,
            ProxyRequestDelegate delegate
    ) {
        return request(context, path, requestMethod, body, false, delegate);
    }

    public static ProxyRequest request(
            Context context,
            String path,
            String requestMethod,
            JSONObject body,
            boolean showDialog,
            ProxyRequestDelegate delegate
    ) {
        ProxyRequest request = new ProxyRequest(context, path);
        request.setRequestMethod(requestMethod);
        request.setBody(body);
        request.setShowDialog(showDialog);
        request.setDelegate(delegate);
        request.execute();
        return request;
    }

    public static boolean isReady(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        String baseUrl = preferences.getString("baseUrl", null);
        String sessionId = preferences.getString("sessionId", null);
        return baseUrl != null && sessionId != null;
    }

    public static void logout(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("sessionId");
        editor.commit();
        ProxyRequest request = new ProxyRequest(context);
        request.showLogin();
    }

    public static void setBaseUrl(Context context, String url) {
        SharedPreferences preferences = context.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("baseUrl", url);
        editor.commit();
    }

    public static void setLoginActivity(Class<? extends LoginActivity> loginActivity) {
        ProxyRequest.loginActivity = loginActivity;
    }

    public static String getSessionValue(Activity activity, String key) {
        return ProxyRequest.getSessionValue(activity, key, null);
    }

    public static String getSessionValue(Context context, String key, String fallback) {
        SharedPreferences preferences = context.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        return preferences.getString(key, fallback);
    }

    public static String getLoginPath() {
        return ProxyRequest.loginPath;
    }

    public static void setLoginPath(String loginPath) {
        ProxyRequest.loginPath = loginPath;
    }

    public static int getLoginLogo() {
        return ProxyRequest.loginLogo;
    }

    public static void setLoginLogo(int loginLogo) {
        ProxyRequest.loginLogo = loginLogo;
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
    public void didReceiveJson(final JSONRequest request, final JSONObject data) {
        Object meta = request.getMeta();
        if (meta != null) {
            ProgressDialog progressDialog = (ProgressDialog) meta;
            progressDialog.dismiss();
        }

        if (this.delegate == null) {
            return;
        }

        if (this.activity == null) {
            this.delegate.didReceiveJson(this, data);
        } else {
            final ProxyRequest self = this;
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.delegate.didReceiveJson(self, data);
                }
            });
        }
    }

    @Override
    public void didReceiveError(final JSONRequest request, final Object error) {
        Object meta = request.getMeta();
        if (meta != null) {
            ProgressDialog progressDialog = (ProgressDialog) meta;
            progressDialog.dismiss();
        }

        if (this.delegate == null) {
            return;
        }

        if (this.activity == null) {
            this.delegate.didReceiveError(this, error);
        } else {
            final ProxyRequest self = this;
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.delegate.didReceiveError(self, error);
                }
            });
        }
    }

    public String load() throws IOException, JSONException {
        Object meta = null;
        SharedPreferences preferences = this.context.getSharedPreferences("cameo", Context.MODE_PRIVATE);
        String baseUrl = preferences.getString("baseUrl", null);
        String sessionId = preferences.getString("sessionId", null);
        String urlString = String.format("%s%s", baseUrl, this.path);

        if (this.useSession && ProxyRequest.isReady(this.context) == false) {
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

        if (this.showDialog) {
            ProgressDialog progressDialog = new ProgressDialog(this.context);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(this.context.getString(R.string.loading));
            progressDialog.show();
            meta = progressDialog;
        }

        // creates the JSON request object that is going to be used
        // for the current request, sets the multiple parameters and
        // then runs the load operations, triggering the network
        JSONRequest request = new JSONRequest();
        request.setDelegate(this);
        request.setContext(this.context);
        request.setUrl(urlString);
        request.setParameters(parameters);
        request.setRequestMethod(this.requestMethod);
        request.setBody(this.body);
        request.setMeta(meta);
        return request.load();
    }

    public void showLogin() {
        // verifies if the activity value is set and if that's not
        // the case returns immediately as it's not possible to show
        // the login for the current context information
        if (this.activity == null) {
            return;
        }

        // creates the intent object that represents the login
        // activity and then starts it (pushing it into the screen)
        Intent intent = new Intent(this.context, ProxyRequest.loginActivity);
        intent.putExtra("LOGIN_PATH", ProxyRequest.loginPath);
        intent.putExtra("LOGO_ID", ProxyRequest.loginLogo);
        this.activity.startActivityForResult(intent, ProxyRequest.LOGIN_REQUEST);
    }

    public ProxyRequestDelegate getDelegate() {
        return this.delegate;
    }

    public void setDelegate(ProxyRequestDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean getShowDialog() {
        return this.showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public List<List<String>> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<List<String>> parameters) {
        this.parameters = parameters;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public void setRequestMethod(String method) {
        this.requestMethod = method;
    }

    public JSONObject getBody() {
        return this.body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }

    public boolean isUseSession() {
        return this.useSession;
    }

    public void setUseSession(boolean useSession) {
        this.useSession = useSession;
    }
}
