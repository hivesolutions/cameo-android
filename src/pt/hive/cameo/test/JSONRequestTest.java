package pt.hive.cameo.test;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

import pt.hive.cameo.JSONRequest;
import pt.hive.cameo.util.Definitions;

import static org.junit.Assert.assertNotEquals;

public class JSONRequestTest {

    @Test
    public void basic() throws IOException, JSONException {
        String httpBinUrl = Definitions.getHttpBinUrl();
        JSONRequest jsonRequest = new JSONRequest(httpBinUrl + "/get");
        String result = jsonRequest.execute();
        assertNotEquals(result, null);
        assertNotEquals(result.compareTo(""), 0);
    }
}
