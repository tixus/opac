package de.tixus.eopac.server;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class StandaloneHttpClient {

	public final static void main(final String[] args) throws Exception {
		final HttpClient httpclient = new DefaultHttpClient();

		final ArrayList<BasicNameValuePair> qparams = new ArrayList<BasicNameValuePair>();
		qparams.add(new BasicNameValuePair("q", "httpclient"));
		qparams.add(new BasicNameValuePair("btnG", "Google Search"));
		qparams.add(new BasicNameValuePair("aq", "f"));
		qparams.add(new BasicNameValuePair("oq", null));
		final URI uri = URIUtils.createURI("http", "www.google.com", -1,
				"/search", URLEncodedUtils.format(qparams, "UTF-8"), null);
		final HttpGet httpget = new HttpGet(uri);
		System.out.println(httpget.getURI());

		final HttpGet httpGet = new HttpGet(
				"https://www.buecherhallen.de/alswww2.dll/APS_QUICK_SEARCH?Style=Portal2&SubStyle=Advanced&Theme=&Lang=GER&ResponseEncoding=utf-8&Style=Portal2&BrowseAsHloc=69");

		System.out.println("executing request " + httpget.getURI());

		final ResponseHandler<String> basicSearchHandler = new ResponseHandler<String>() {
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return parseTagSoup(entity.getContent());
					return null;
				} else {
					return null;
				}
			}
		};

		final String responseBody = httpclient.execute(httpget,
				basicSearchHandler);

		System.out.println("----------------------------------------");
		// document.getElementById(arg0)
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();

	}

}