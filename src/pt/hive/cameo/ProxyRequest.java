package pt.hive.cameo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
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
public class ProxyRequest extends AsyncTask<Void, Void, String> {

    /**
     * The size of the buffer that is going to be used in the reading of the
     * various message maintained by the proxy request.
     */
    private int BUFFER_SIZE = 4096;

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

    public ProxyRequest(Context context, String path, String loginPath) {
        this.context = context;
        this.path = path;
        this.loginPath = path;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return this.load();
        } catch (ClientProtocolException exception) {
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Starts the loading of the request, this is considered to be the trigger
     * of the remote call operation, the proper callback methods will be called
     * upon the proper loading of the operations.
     *
     * @return The resulting string value, resulting from the loading of the
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String load() throws ClientProtocolException, IOException {
        String result = null;

        SharedPreferences preferences = this.context.getSharedPreferences(
                "cameo", Context.MODE_PRIVATE);
        String baseUrl = preferences.getString("baseUrl", null);
        String urlString = String.format("%s%s", baseUrl, this.path);

        HttpGet get = new HttpGet(urlString);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();

        try {
            result = ProxyRequest.convertStreamToString(stream);
        } finally {
            stream.close();
        }

        return result;
    }

    public void showLogin() {
    }

    private static String convertStreamToString(InputStream stream) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return builder.toString();
    }
}
