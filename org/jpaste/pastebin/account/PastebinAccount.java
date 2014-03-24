package org.jpaste.pastebin.account;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jpaste.pastebin.PasteExpireDate;
import org.jpaste.pastebin.Pastebin;
import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.PastebinPaste;
import org.jpaste.pastebin.exceptions.LoginException;
import org.jpaste.pastebin.exceptions.ParseException;
import org.jpaste.utils.web.Post;
import org.jpaste.utils.web.Web;
import org.jpaste.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * Represents an account on <a href="http://pastebin.com/">Pastebin</a>.
 * 
 * <p>
 * This class can fetch a non-expiring user session id. This session id is used
 * for generating private pasted, fetching user details, fetching user his
 * pastes, adding pastes to his account & more account based interactions.
 * </p>
 * 
 * <p>
 * A reference manual for generating a user session id can be found <a
 * href="http://pastebin.com/api#8">here</a>.
 * 
 * @author Brian B
 * 
 */
public class PastebinAccount {
	private String username, password, userSessionId, developerKey;

	/**
	 * Creates a new empty <code>PastebinAccount</code> instance.
	 */
	public PastebinAccount() {
		this(null, null, null);
	}

	/**
	 * Creates a new <code>PastebinAccount</code> instance.
	 * 
	 * When you use this constructor, you'll need to use the {@link #login()} to
	 * fetch an user session id
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param username
	 *            the username of the pastebin account
	 * @param password
	 *            the password of the pastebin account
	 */
	public PastebinAccount(String developerKey, String username, String password) {
		this.developerKey = developerKey;
		this.username = username;
		this.password = password;
	}

	/**
	 * Creates a new <code>PastebinAccount</code> instance.
	 * 
	 * @param userSessionId
	 *            the user session id of the pastebin account.
	 */
	public PastebinAccount(String userSessionId) {
		this(null, userSessionId);
	}

	/**
	 * Creates a new <code>PastebinAccount</code> instance.
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param userSessionId
	 *            the user session id of the pastebin account.
	 */
	public PastebinAccount(String developerKey, String userSessionId) {
		this.developerKey = developerKey;
		this.userSessionId = userSessionId;
	}

	/**
	 * Sets the user session id The user session id can be used to paste private
	 * pastes and will add pastes to your account
	 * 
	 * @param userSessionId
	 *            the user session id of the pastebin account
	 */
	public void setUserSessionId(String userSessionId) {
		this.userSessionId = userSessionId;
	}

	/**
	 * Gets the user session id
	 * 
	 * @return user session id
	 */
	public String getUserSessionId() {
		return this.userSessionId;
	}

	/**
	 * Sets the username
	 * 
	 * @param username
	 *            the username of the pastebin account
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets username of the pastebin account
	 * 
	 * @return username of the pastebin account. Could be null if only an user
	 *         session is was provided.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Sets the password
	 * 
	 * @param password
	 *            the password of the pastebin account
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets password of the pastebin account
	 * 
	 * @return password of the pastebin account. Could be null if only an user
	 *         session id was provided.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the developer key The developer key is required to paste contents on
	 * pastebin
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 */
	public void setDeveloperKey(String developerKey) {
		if (developerKey == null || developerKey.isEmpty()) {
			throw new IllegalArgumentException(
					"Developer key can not be null or empty.");
		}
		this.developerKey = developerKey;
	}

	/**
	 * Gets the developer key
	 * 
	 * @return developer key
	 */
	public String getDeveloperKey() {
		return this.developerKey;
	}

	/**
	 * Fetches an user session id.
	 * 
	 * @throws LoginException
	 *             if fetching the user session id failed
	 */
	public void login() throws LoginException {
		if (getUserSessionId() != null) {
			throw new IllegalStateException("Already logged in.");
		}
		if (getUsername() == null || getPassword() == null) {
			throw new IllegalStateException("Username or password null.");
		}
		if (getDeveloperKey() == null || getDeveloperKey().isEmpty()) {
			throw new IllegalStateException("Developer key is missing.");
		}

		Post post = new Post();

		post.put("api_dev_key", getDeveloperKey());
		post.put("api_user_name", username);
		post.put("api_user_password", password);

		String response = Web.getContents(Pastebin.API_LOGIN_LINK, post);
		if (response == null || response.isEmpty()) {
			throw new LoginException("Empty response from login API server.");
		}
		if (response.toLowerCase().startsWith("bad")) {
			throw new LoginException("Failed to login: " + response);
		}

		this.userSessionId = response;
	}

	/**
	 * Gets all pasted pastes by this user
	 * 
	 * @param limit
	 *            maximum amount of pastes to receive
	 *            <p>
	 *            <code>0 > limit > 1000</code>
	 *            </p>
	 * @return all pasted pastes made by this user/account
	 * @throws ParseException
	 *             if it failed to parse the pastes
	 */
	public PastebinLink[] getPastes(int limit) throws ParseException {
		if (limit > 1000) {
			limit = 1000;
		}
		if (limit < 1) {
			limit = 1;
		}
		if (getUserSessionId() == null) {
			throw new IllegalStateException("User session id missing.");
		}
		if (getDeveloperKey() == null || getDeveloperKey().isEmpty()) {
			throw new IllegalStateException("Developer key is missing.");
		}

		Post post = new Post();

		post.put("api_dev_key", getDeveloperKey());
		post.put("api_user_key", getUserSessionId());
		post.put("api_results_limit", Integer.toString(limit));
		post.put("api_option", "list");

		String response = Web.getContents(Pastebin.API_POST_LINK, post);
		if (response.equals("No pastes found.")) {
			return null;
		}

		if (response.startsWith("<paste>")) {
			// success
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				response = "<dummy>" + response + "</dummy>"; // requires root
																// element
				Document doc = dBuilder.parse(new InputSource(
						new ByteArrayInputStream(response.getBytes("utf-8"))));
				doc.getDocumentElement().normalize();

				NodeList nodes = doc.getElementsByTagName("paste");
				ArrayList<PastebinLink> pastes = new ArrayList<PastebinLink>(
						nodes.getLength());
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;

						String pasteFormat = XMLUtils.getText(element,
								"paste_format_short");
						String title = XMLUtils.getText(element, "paste_title");
						int visibility = Integer.parseInt(XMLUtils.getText(
								element, "paste_private"));
						int hits = Integer.parseInt(XMLUtils.getText(element,
								"paste_hits"));

						long expireDate = Long.parseLong(XMLUtils.getText(
								element, "paste_expire_date"));
						long pasteDate = Long.parseLong(XMLUtils.getText(
								element, "paste_date"));

						URL pasteURL = new URL(XMLUtils.getText(element,
								"paste_url"));

						PastebinPaste paste = new PastebinPaste(this);
						paste.setPasteFormat(pasteFormat);
						paste.setPasteTitle(title);
						paste.setVisibility(visibility);
						paste.setPasteExpireDate(expireDate == 0L ? PasteExpireDate.NEVER
								: PasteExpireDate
										.getExpireDate((int) (expireDate - pasteDate)));

						PastebinLink pastebinLink = new PastebinLink(paste,
								pasteURL, new Date(pasteDate * 1000));
						pastebinLink.setHits(hits);

						pastes.add(pastebinLink);
					}
				}

				return pastes.toArray(new PastebinLink[pastes.size()]);
			} catch (Exception e) {
				throw new ParseException("Failed to parse pastes: "
						+ e.getMessage());
			}

		}

		throw new ParseException("Failed to parse pastes: " + response);
	}

	/**
	 * Gets pasted pastes (max 50) by this user
	 * 
	 * @return all pasted pastes made by this user/account
	 * @throws ParseException
	 *             if it failed to parse the pastes
	 */
	public PastebinLink[] getPastes() throws ParseException {
		return getPastes(50);
	}

	/**
	 * Fetches the account details of this account
	 * 
	 * @return account details
	 * @throws ParseException
	 *             if it failed to parse the account details
	 */
	public PastebinAccountDetails getAccountDetails() throws ParseException {
		if (getUserSessionId() == null) {
			throw new IllegalStateException("User session id missing.");
		}
		if (getDeveloperKey() == null || getDeveloperKey().isEmpty()) {
			throw new IllegalStateException("Developer key is missing.");
		}

		Post post = new Post();
		post.put("api_dev_key", getDeveloperKey());
		post.put("api_user_key", getUserSessionId());
		post.put("api_option", "userdetails");

		String response = Web.getContents(Pastebin.API_POST_LINK, post);

		if (!response.startsWith("<user>")) {
			throw new ParseException("Failed to parse account details: "
					+ response);
		}

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(
					new ByteArrayInputStream(response.getBytes("utf-8"))));
			doc.getDocumentElement().normalize();

			return new PastebinAccountDetails((Element) doc
					.getElementsByTagName("user").item(0));
		} catch (Exception e) {
			throw new ParseException("Failed to parse account details: " + e);
		}
	}

}
