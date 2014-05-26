package pt.hive.cameo;

import org.json.JSONObject;

public interface JsonRequestDelegate {
    public void didReceiveJson(JSONObject data);

    public void didReceiveError(Object error);
}
