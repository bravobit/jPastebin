package org.jpaste.pastebin;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import org.jpaste.AbstractPasteLink;
import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.account.PastebinAccount;
import org.jpaste.utils.web.Post;
import org.jpaste.utils.web.Web;

/**
 * 
 * A representation of an online pastebin paste.
 * 
 * 
 * <p>
 * This class represents an existing pastebin paste URL
 * </p>
 * 
 * @author Brian B
 * 
 */
public class PastebinLink extends AbstractPasteLink {
	private PastebinPaste paste;
	private URL link;
	private int hits;
	private String key;
	private Date pasteDate;

	/**
	 * Creates a new <code>PastebinLink</code> object, representing an existing
	 * paste
	 * 
	 * @param paste
	 *            paste details
	 * @param url
	 *            link to paste
	 */
	public PastebinLink(PastebinPaste paste, URL url) {
		this(paste, url, new Date((System.currentTimeMillis() / 1000) * 1000));
	}

	/**
	 * Creates a new <code>PastebinLink</code> object, representing an existing
	 * paste
	 * 
	 * @param paste
	 *            paste details
	 * @param url
	 *            link to paste
	 * @param pasteDate
	 *            date the paste has been pasted
	 */
	public PastebinLink(PastebinPaste paste, URL url, Date pasteDate) {
		this.paste = paste;
		this.link = url;
		this.pasteDate = pasteDate;
		try {
			this.key = url.toURI().getPath().substring(1);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getLink() {
		return link;
	}

	/**
	 * Gets pastebin paste details
	 * 
	 * @return paste details
	 */
	public PastebinPaste getPaste() {
		return paste;
	}
	
	/**
	 * Gets pastebin unique paste key
	 * @return unique paste key
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * Fetches the pastebin link content
	 * <p>
	 * After parsing use the following methods: {@link #getPaste()} {@link PastebinPaste#getContents()}
	 */
	public void fetchContent() {
		if(getPaste().getContents() != null) {
			throw new IllegalStateException("Contents already fetched.");
		}
		getPaste().setContents(getContents(getKey()));
	}

	/**
	 * Sets the paste page hits
	 * 
	 * @param hits
	 *            amount of times paste has been visited
	 */
	public void setHits(int hits) {
		if (hits < 0) {
			throw new IllegalArgumentException("Hits must be positive: " + hits);
		}
		this.hits = hits;
	}

	/**
	 * Gets the paste page hits
	 * 
	 * @return paste page hits
	 */
	public int getHits() {
		return this.hits;
	}

	/**
	 * Gets the paste date
	 * 
	 * @return paste date
	 */
	public Date getPasteDate() {
		return this.pasteDate;
	}

	/**
	 * Deletes this paste
	 * 
	 * @param account
	 *            the account which was used to create this paste
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @throws PasteException if it failed to delete the paste
	 */
	public void delete(String developerKey, PastebinAccount account) throws PasteException {
		if(developerKey == null || developerKey.isEmpty()) {
			throw new IllegalArgumentException("Developer key can't be null or empty.");
		}
		if(account.getUserSessionId() == null || account.getUserSessionId().isEmpty()) {
			throw new IllegalArgumentException("Account user session id is missing.");
		}
		Post post = new Post();
		post.put("api_dev_key", developerKey);
		post.put("api_user_key", account.getUserSessionId());
		post.put("api_paste_key", getKey());
		post.put("api_option", "delete");
		
		String response = Web.getContents(Pastebin.API_POST_LINK, post);
		if(response.equals("Paste Removed")) {
			return;
		}
		throw new PasteException("Failed to delete paste: " + response);
	}

	/**
	 * Deletes this paste
	 * @throws PasteException if it failed to delete the paste
	 */
	public void delete() throws PasteException {
		delete(getPaste().getDeveloperKey(), getPaste().getAccount());
	}

	/**
	 * Fetches a paste text from pastebin
	 * 
	 * @param pasteKey
	 *            the unique paste key
	 * @return contents of the paste
	 */
	public static String getContents(String pasteKey) {
		return Web.getContents("http://pastebin.com/raw.php?i=" + pasteKey);
	}

}
