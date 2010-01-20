/**
 * 
 */
package de.tixus.eopac.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * @author http://www.siafoo.net/snippet/258
 * 
 */
public class ProxyImpl extends HttpServlet {

	/**
     * 
     */
	private static final long serialVersionUID = 8L;

	private static final String targetServer = "http://localhost:5000";

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		handleRequest(req, resp, false);
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		handleRequest(req, resp, true);
	}

	@SuppressWarnings("unchecked")
	protected void handleRequest(final HttpServletRequest req,
			final HttpServletResponse resp, final boolean isPost)
			throws ServletException, IOException {

		final HttpClient httpclient = new DefaultHttpClient();

		final StringBuffer sb = new StringBuffer();

		sb.append(targetServer);
		sb.append(req.getRequestURI());

		if (req.getQueryString() != null) {
			sb.append("?" + req.getQueryString());
		}

		HttpRequestBase targetRequest = null;

		if (isPost) {
			System.out.println("POST");
			final HttpPost post = new HttpPost(sb.toString());

			final Enumeration<String> paramNames = req.getParameterNames();

			String paramName = null;

			final List<NameValuePair> params = new ArrayList<NameValuePair>();

			while (paramNames.hasMoreElements()) {
				paramName = paramNames.nextElement();
				params.add(new BasicNameValuePair(paramName, req
						.getParameterValues(paramName)[0]));
			}

			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			targetRequest = post;
		} else {
			System.out.println("GET");

			final HttpGet get = new HttpGet(sb.toString());
			targetRequest = get;
		}

		// // This copies the headers but I never really cared to get it to work
		// properly
		// System.out.println("Request Headers");
		// Enumeration<String> headerNames = req.getHeaderNames();
		// String headerName = null;
		// while(headerNames.hasMoreElements()){
		// headerName = headerNames.nextElement();
		// targetRequest.addHeader(headerName, req.getHeader(headerName));
		// System.out.println(headerName + " : " + req.getHeader(headerName));
		// }

		final HttpResponse targetResponse = httpclient.execute(targetRequest);
		final HttpEntity entity = targetResponse.getEntity();

		// Send the Response
		final InputStream input = entity.getContent();
		final OutputStream output = resp.getOutputStream();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				input));
		final BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(output));
		String line = reader.readLine();

		while (line != null) {
			writer.write(line + "\n");
			line = reader.readLine();
		}

		reader.close();
		writer.close();
		httpclient.getConnectionManager().shutdown();
	}

}