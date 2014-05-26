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

package pt.hive.cameo;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.hive.cameo.activities.LoginActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

/**
 * Abstract class responsible for the handling of remote json request that may
 * or may not require and underlying authentication process.
 *
 * The handling of the authentication should be automatic and the proper panel
 * should be raised upon the invalidation of credentials.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class ProxyRequest extends AsyncTask<Void, Void, String> implements
        JsonRequestDelegate {

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
     * Relative path to the URL that is going to be called in case a login
     * operation is required, note that the login action is always going to be
     * show for such operations.
     */
    private String loginPath;

    /**
     * The various GET operation parameters that are going to be encoded to be
     * part of the request query parameters.
     */
    private List<List<String>> parameters;

    /**
     * If a proper session must be created before any remote request is done
     * using the proxy request infra-structure.
     */
    private boolean useSession;

    public ProxyRequest() {
        this.useSession = true;
    }

    public ProxyRequest(Activity activity, String path, String loginPath) {
        this();
        this.activity = activity;
        this.path = path;
        this.loginPath = loginPath;
    }

    static public void logout(Activity activity, String loginPath) {
        SharedPreferences preferences = activity.getSharedPreferences("cameo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("sessionId");
        editor.commit();
        ProxyRequest request = new ProxyRequest();
        request.activity = activity;
        request.loginPath = loginPath;
        request.showLogin();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return this.load();
        } catch (ClientProtocolException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return null;
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

    public String load() throws ClientProtocolException, IOException,
            JSONException {
        SharedPreferences preferences = this.activity.getSharedPreferences(
                "cameo", Context.MODE_PRIVATE);
        String baseUrl = preferences.getString("baseUrl", null);
        String sessionId = preferences.getString("sessionId", null);
        String urlString = String.format("%s%s", baseUrl, this.path);

        if (this.useSession && sessionId == null) {
            this.showLogin();
            return null;
        }

        List<List<String>> parameters = new LinkedList<List<String>>();
        if (this.parameters != null) {
            parameters.addAll(this.parameters);
        }
        parameters.add(new LinkedList<String>(Arrays.asList("session_id",
                sessionId)));

        JsonRequest request = new JsonRequest();
        request.setDelegate(this);
        request.setActivity(this.activity);
        request.setUrl(urlString);
        request.setParameters(parameters);
        return request.load();
    }

    public void showLogin() {
        // creates the intent object that represents the login
        // activity and then starts it (pushing it into the screen)
        Intent intent = new Intent(this.activity, LoginActivity.class);
        intent.putExtra("LOGIN_PATH", this.loginPath);
        this.activity.startActivity(intent);
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

    public boolean isUseSession() {
        return useSession;
    }

    public void setUseSession(boolean useSession) {
        this.useSession = useSession;
    }
}
