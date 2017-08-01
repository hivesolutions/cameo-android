package pt.hive.cameo.test;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

import pt.hive.cameo.JSONRequest;

import static org.junit.Assert.assertNotEquals;

public class JSONRequestTest {

    @Test
    public void basic() throws IOException, JSONException {
        JSONRequest jsonRequest = new JSONRequest("http://httpbin.stage.hive.pt/ip");
        String result = jsonRequest.execute();
        assertNotEquals(result, null);
        assertNotEquals(result.compareTo(""), 0);
    }
}
