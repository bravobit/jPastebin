package org.jpaste.utils.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 
 * A representation of a HTTP post
 * 
 * <p>
 * Encodes parameters with the UTF-8 Charset.
 * </p>
 * 
 * <p>
 * <a href="http://en.wikipedia.org/wiki/POST_(HTTP)">Reference manual</a>
 * </p>
 * 
 * @author Brian B
 * 
 */
public class Post {
	private static final String ENCODING = "UTF-8";
	private HashMap<String, String> post;

	/**
	 * Creates a new <code>Post</code> instance.
	 */
	public Post() {
		post = new HashMap<String, String>();
	}

	/**
	 * Adds a key value pair to the post parameters
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void put(String key, String value) {
		try {
			this.post.put(URLEncoder.encode(key, ENCODING),
					URLEncoder.encode(value, ENCODING));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The HTTP post string representation
	 * 
	 * @return HTTP Post contents
	 */
	public String getPost() {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> entry : post.entrySet()) {
			builder.append(entry.getKey()).append('=').append(entry.getValue())
					.append('&');
		}
		builder.deleteCharAt(builder.length() - 1);
		return new String(builder);
	}

}
