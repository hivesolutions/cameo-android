package pt.hive.cameo.test;

import org.junit.Test;

import pt.hive.cameo.ProxyRequest;

import static org.junit.Assert.assertEquals;

public class ProxyRequestTest {

    @Test
    public void basic() throws Exception {
        String path = ProxyRequest.getLoginPath();
        assertEquals(path, null);
    }
}
