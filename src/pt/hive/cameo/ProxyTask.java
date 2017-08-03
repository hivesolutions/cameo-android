package pt.hive.cameo;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

public class ProxyTask extends AsyncTask<Void, Void, String> {

    ProxyRequest proxyRequest;

    public ProxyTask(ProxyRequest proxyRequest) {
        this.proxyRequest = proxyRequest;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return this.proxyRequest.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
    }
}
