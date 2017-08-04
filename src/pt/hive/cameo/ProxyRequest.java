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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pt.hive.cameo.activities.LoginActivity;

/**
 * Abstract class responsible for the handling of remote JSON request that may
 * or may not require and underlying authentication process.
 * <p>
 * The handling of the authentication should be automatic and the proper panel
 * should be raised upon the invalidation of credentials.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class ProxyRequest implements JSONRequestDelegate {

    /**
     * Constant value that defined the request login intent value that may be
     * used to identify this request intent.
     */
    public static final int LOGIN_REQUEST = 1;

    /**
     * List that contains the complete set of error codes that describe
     * error related with authentication issues.
     */
    public static final List<Integer> AUTH_ERRORS = Arrays.asList(401, 403, 440, 499);

    /**
     * Relative path to the URL that is going to be called in case a login
     * operation is required, note that the login action is always going to be
     * show for such operations.
     */
    private static String loginPath = null;

    /**
     * The reference to the resource that contains the image that is going to be
     * display in the login screen in case it's required for the request.
     */
    private static int loginLogo = 0;

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
    private ProxyRequestDelegate delegate = null;

    /**
     * If a dialog window should be displayed (if possible) during the request
     * lifecycle indicating the progress of the operation.
     */
    private boolean showDialog = false;

    /**
     * Simple flag that controls if a modal window confirming the error should
     * be displayed in case an error occurs for the request. Using that window
     * it's possible for the user to retry the request.
     */
    private boolean confirmError = false;

    /**
     * The number of retries to be executed before the notification of the error
     * is sent to the delegate. Useful way of handling network issues.
     */
    private int retryCount = 0;

    /**
     * The delay in milliseconds to be used in between retry operations of the
     * request. Should allow enough time to avoid subsequent failures.
     */
    private int retryDelay = 1000;

    /**
     * The context object that is going to be used for resolution of global
     * values this is required in order for the request to work properly.
     */
    private Context context = null;

    /**
     * The reference to the activity object that is going to be used for
     * UI related operations that require a visual context.
     */
    private Activity activity = null;

    /**
     * The relative URL path for the request that is going to be performed, this
     * should be JSON based (eg: api/info.json).
     */
    private String path = null;

    /**
     * The various GET operation parameters that are going to be encoded to be
     * part of the request query parameters.
     */
    private List<List<String>> parameters = null;

    /**
     * The HTTP method to be set on the request, this should be an upper-cased
     * string like GET, POST, PUT or DELETE.
     */
    private String requestMethod = null;

    /**
     * The JSON object to be encoded as the body of a a payload based request
     * like POST or PUT requests.
     */
    private JSONObject body = null;

    /**
     * If a proper session must be created before any remote request is done
     * using the proxy request infra-structure.
     */
    private boolean useSession = false;

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
            boolean showDialog,
            boolean confirmError,
            ProxyRequestDelegate delegate
    ) {
        return request(context, path, null, null, showDialog, confirmError, delegate, true);
    }

    public static ProxyRequest request(
            Context context,
            String path,
            String requestMethod,
            JSONObject body,
            ProxyRequestDelegate delegate
    ) {
        return request(context, path, requestMethod, body, false, false, delegate, true);
    }

    public static ProxyRequest request(
            Context context,
            String path,
            String requestMethod,
            JSONObject body,
            boolean showDialog,
            boolean confirmError,
            ProxyRequestDelegate delegate
    ) {
        return request(context, path, requestMethod, body, showDialog, confirmError, delegate, true);
    }

    public static ProxyRequest request(
            Context context,
            String path,
            String requestMethod,
            JSONObject body,
            boolean showDialog,
            boolean confirmError,
            ProxyRequestDelegate delegate,
            boolean execute
    ) {
        ProxyRequest request = new ProxyRequest(context, path);
        request.setRequestMethod(requestMethod);
        request.setBody(body);
        request.setShowDialog(showDialog);
        request.setConfirmError(confirmError);
        request.setDelegate(delegate);
        if (execute) {
            request.execute();
        }
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
    public void didReceiveJson(final JSONRequest request, final JSONObject data) {
        Object meta = request.getMeta();
        if (meta != null && this.activity != null) {
            final ProgressDialog progressDialog = (ProgressDialog) meta;
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }

        this.notifySuccess(data);
    }

    @Override
    public void didReceiveError(final JSONRequest request, final Object error) {
        // tries to retrieve any possible meta attribute associated with the
        // request and if there0s any (progress dialog) dismisses it
        Object meta = request.getMeta();
        if (meta != null && this.activity != null) {
            final ProgressDialog progressDialog = (ProgressDialog) meta;
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }

        // retrieves the status code of the request and tries to determine
        // if it represents a problem with the authentication and if that's
        // the case and there's a valid activity presents the login panel
        int statusCode = request.getResponseCode();
        if (this.AUTH_ERRORS.contains(statusCode) && this.activity != null) {
            this.showLogin();
            return;
        }

        // in case there's retries waiting to be performed, then a new execution
        // operation should be scheduler after a certain delay of sleeping
        if (this.retryCount > 0) {
            this.retryCount--;
            try {
                Thread.sleep(this.retryDelay);
            } catch (InterruptedException exception) {
            }
            this.execute();
            return;
        }

        if (this.confirmError && this.activity != null) {
            final ProxyRequest self = this;
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(self.activity);
                    builder.setMessage(R.string.retry_notice);
                    builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            self.execute();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            self.notifyError(error);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            return;
        }

        this.notifyError(error);
    }

    public String load() throws IOException, JSONException {
        final ProxyRequest self = this;

        Object meta = null;
        final JSONRequest request = new JSONRequest();

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

        // in case the (loading) dialog window should be shown the request
        // in the main UI thread is performed (safely)
        if (this.showDialog && this.activity != null) {
            this.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // verifies if the last response timestamp is set and if that's
                    // the case returns immediately as the response has already been
                    // received (cannot show the progress dialog)
                    if (request.getLastResponse() != 0) {
                        return;
                    }

                    // creates the progress dialog modal box and show it notifying the
                    // end user about the background activity in execution
                    ProgressDialog progressDialog = new ProgressDialog(self.context);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(self.context.getString(R.string.loading));
                    progressDialog.show();

                    // sets the meta field of the request with the progress dialog reference
                    // so that latter it may be used to hide the progress dialog
                    request.setMeta(progressDialog);
                }
            });
        }

        // populates the JSON request object that is going to be used
        // for the current request, sets the multiple parameters and
        // then runs the load operations (triggering the network)
        request.setDelegate(this);
        request.setContext(this.context);
        request.setUrl(urlString);
        request.setParameters(parameters);
        request.setRequestMethod(this.requestMethod);
        request.setBody(this.body);
        request.setMeta(meta);
        return request.load();
    }

    public void execute() {
        ProxyRequestTask task = new ProxyRequestTask(this);
        task.execute();
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

    public boolean isShowDialog() {
        return this.showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public boolean isConfirmError() {
        return this.confirmError;
    }

    public void setConfirmError(boolean confirmError) {
        this.confirmError = confirmError;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryDelay() {
        return this.retryDelay;
    }

    public void setRetryDelay(int retryCount) {
        this.retryDelay = retryDelay;
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

    private void notifySuccess(final JSONObject data) {
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

    private void notifyError(final Object error) {
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
}
