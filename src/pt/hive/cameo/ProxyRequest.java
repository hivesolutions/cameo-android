package pt.hive.cameo;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.hive.cameo.activities.LoginActivity;
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
    private Context context;

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

    public ProxyRequest(Context context, String path, String loginPath) {
        this();
        this.context = context;
        this.path = path;
        this.loginPath = loginPath;
    }

    static public void logout(Context context, String loginPath) {
        SharedPreferences preferences = context.getSharedPreferences("cameo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("sessionId");
        editor.commit();
        ProxyRequest request = new ProxyRequest();
        request.context = context;
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
        SharedPreferences preferences = this.context.getSharedPreferences(
                "cameo", Context.MODE_PRIVATE);
        String baseUrl = preferences.getString("baseUrl", null);
        String sessionId = preferences.getString("sessionId", null);
        String urlString = String.format("%s%s", baseUrl, this.path);

        if (this.useSession && sessionId == null) {
            this.showLogin();
            return null;
        }

        JsonRequest request = new JsonRequest();
        request.setDelegate(this);
        request.setUrl(urlString);
        request.setParameters(parameters);
        return request.load();
    }

    public void showLogin() {
        // creates the intent object that represents the login
        // activity and then starts it (pushing it into the screen)
        Intent intent = new Intent(this.context, LoginActivity.class);
        intent.putExtra("LOGIN_PATH", this.loginPath);
        this.context.startActivity(intent);
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
