package pt.hive.cameo.test;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

import pt.hive.cameo.JSONRequest;
import pt.hive.cameo.ProxyRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProxyRequestTest {

    @Test
    public void basic() throws Exception {
        String path = ProxyRequest.getLoginPath();
        assertEquals(path, null);

        int logo = ProxyRequest.getLoginLogo();
        assertEquals(logo, 0);
    }

    @Test
    public void request() throws IOException, JSONException {
        JSONRequest jsonRequest = new JSONRequest("https://httpbin.stage.hive.pt/ip");
        String result = jsonRequest.execute();
        assertNotEquals(result, null);
    }
}
