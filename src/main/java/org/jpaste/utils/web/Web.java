package org.jpaste.utils.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * Web utility class
 * 
 * @author Brian B
 * 
 */
public class Web {

	/**
	 * Submits a HTTP post and fetches and returns the response
	 * 
	 * @param link
	 *            The link/URL
	 * @param post
	 *            the HTTP post representation
	 * @return response of the web page
	 */
	public static String getContents(String link, Post post) {
		try {
			URL url = new URL(link);

			URLConnection connection = url.openConnection();

			if(post != null) {
				connection.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(
						connection.getOutputStream());
				wr.write(post.getPost());
				wr.flush();
				wr.close();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (builder.length() > 0) {
					builder.append('\n');
				}
				builder.append(line);
			}
			reader.close();
			return new String(builder);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed link: " + e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to fetch contents from link: "
					+ e);
		}
	}
	
	/**
	 * Gets text from a link
	 * 
	 * @param link
	 *            The link/URL
	 * @return response of the web page
	 */
	public static String getContents(String link) {
		return getContents(link, null);
	}

}
