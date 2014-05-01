package org.jpaste.pastebin;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jpaste.exceptions.PasteException;
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
 * A global representation of the pastebin site
 * 
 * <p>
 * Holds various constants and method shortcuts
 * </p>
 * 
 * @author Brian B
 * 
 */
public class Pastebin {
	/**
	 * Used to interact with the pastebin API
	 */
	public static final String API_POST_LINK = "http://pastebin.com/api/api_post.php";
	/**
	 * Used for fetching an user session id
	 */
	public static final String API_LOGIN_LINK = "http://pastebin.com/api/api_login.php";

	/**
	 * Fetches a paste text from pastebin
	 * 
	 * @param pasteKey
	 *            the unique paste key
	 * @return contents of the paste
	 */
	public static String getContents(String pasteKey) {
		return PastebinLink.getContents(pasteKey);
	}

	/**
	 * Generates a paste on pastebin and returns the URL to it
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param contents
	 *            contents of the paste
	 * @return URL to paste
	 * @throws PasteException
	 *             if it failed to push the paste
	 */
	public static URL pastePaste(String developerKey, String contents)
			throws PasteException {
		return pastePaste(developerKey, contents, null);
	}

	/**
	 * Generates a paste on pastebin and returns the URL to it
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param contents
	 *            contents of the paste
	 * @param title
	 *            title of the paste
	 * @return URL to paste
	 * @throws PasteException
	 *             if it failed to push the paste
	 */
	public static URL pastePaste(String developerKey, String contents,
			String title) throws PasteException {
		return newPaste(developerKey, contents, title).paste().getLink();
	}

	/**
	 * Generates a new paste and returns it
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param contents
	 *            contents of the paste
	 * @param title
	 *            title of the paste
	 * @return a new paste
	 */
	public static PastebinPaste newPaste(String developerKey, String contents,
			String title) {
		PastebinPaste paste = new PastebinPaste(developerKey, contents);
		paste.setPasteTitle(title);
		return paste;
	}

	/**
	 * Generates a new paste and returns it
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param contents
	 *            contents of the paste
	 * @return a new paste
	 */
	public static PastebinPaste newPaste(String developerKey, String contents) {
		return newPaste(developerKey, contents, null);
	}

	/**
	 * Gets the current trending pastebin pastes
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @return an array of {@link PastebinLink}
	 * @throws ParseException
	 *             if it failed to parse the trending pastes
	 */
	public static PastebinLink[] getTrending(String developerKey)
			throws ParseException {
		if (developerKey == null || developerKey.isEmpty()) {
			throw new IllegalArgumentException(
					"Developer key can't be null or empty.");
		}
		Post post = new Post();
		post.put("api_dev_key", developerKey);
		post.put("api_option", "trends");

		String response = Web.getContents(API_POST_LINK, post);

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

						PastebinPaste paste = new PastebinPaste();
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

}
