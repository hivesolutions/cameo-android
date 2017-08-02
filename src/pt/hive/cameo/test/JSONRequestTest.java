package pt.hive.cameo.test;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import pt.hive.cameo.JSONRequest;
import pt.hive.cameo.util.Definitions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JSONRequestTest {

    @Test
    public void basic() throws IOException, JSONException {
        String httpBinUrl = Definitions.getHttpBinUrl();

        JSONRequest jsonRequest = new JSONRequest(httpBinUrl + "/get");
        String result = jsonRequest.execute();
        assertNotEquals(result, null);
        assertNotEquals(result.compareTo(""), 0);

        jsonRequest = new JSONRequest(httpBinUrl + "/get",
                Arrays.asList(Arrays.asList("message", "hello")));
        result = jsonRequest.execute();
        JSONObject data = new JSONObject(result);
        assertNotEquals(result, null);
        assertNotEquals(result.compareTo(""), 0);
        assertEquals(data.getJSONObject("args").getString("message").compareTo("hello"), 0);
    }
}
