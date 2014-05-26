package pt.hive.cameo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonRequest {

	private JsonRequestDelegate delegate;
	private String url;
	private List<List<String>> parameters;

	public String load() throws ClientProtocolException, IOException,
			JSONException {
		String result = null;

		String url = this.constructUrl();

		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();

		try {
			result = JsonRequest.convertStreamToString(stream);
		} finally {
			stream.close();
		}

		JSONObject data = new JSONObject(result);
		if (this.delegate != null) {
			this.delegate.didReceiveJson(data);
		}

		return result;
	}

	private String constructUrl() {
		if (this.parameters == null) {
			return this.url;
		}
		String parameters = this.constructParameters();
		String url = String.format("%s?%s", this.url, parameters);
		return url;
	}

	private String constructParameters() {
		StringBuffer buffer = new StringBuffer();
		for (List<String> parameter : this.parameters) {
			String parameterS = String.format("%s=%s&", parameter.get(0),
					parameter.get(1));
			buffer.append(parameterS);
		}
		return buffer.toString();
	}

	private static String convertStreamToString(InputStream stream)
			throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		StringBuilder builder = new StringBuilder();

		try {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				builder.append(line + "\n");
			}
		} finally {
			stream.close();
		}
		return builder.toString();
	}
}
