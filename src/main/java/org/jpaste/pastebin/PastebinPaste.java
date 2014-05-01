package org.jpaste.pastebin;

import java.net.MalformedURLException;
import java.net.URL;

import org.jpaste.AbstractPaste;
import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.account.PastebinAccount;
import org.jpaste.utils.web.Post;
import org.jpaste.utils.web.Web;

/**
 * 
 * A representation of a new or existing paste.
 * 
 * <p>
 * This class holds the contents of the paste itself. You can get and modify
 * settings and then 'push' this paste onto <a
 * href="http://pastebin.com/">pastebin.</a>
 * </p>
 * 
 * <p>
 * This class has been programmed with the help of the <a
 * href="http://pastebin.com/api/">pastebin API manual.</a>
 * </p>
 * 
 * @author Brian B
 * 
 */
public class PastebinPaste extends AbstractPaste<PastebinLink> {
	/**
	 * Makes a paste public.
	 */
	public static final int VISIBILITY_PUBLIC = 0;
	/**
	 * Makes a paste unlisted.
	 */
	public static final int VISIBILITY_UNLISTED = 1;
	/**
	 * Makes a paste private.
	 * <p>
	 * Requires an {@link PastebinAccount
	 * org.jpaste.pastebin.account.PastebinAccount PastebinAccount}
	 * </p>
	 */
	public static final int VISIBILITY_PRIVATE = 2;
	private String developerKey;
	private PastebinAccount account;
	private String pasteTitle;
	private String pasteFormat;
	private PasteExpireDate expireDate;
	private int visibility;

	/**
	 * Creates a new empty <code>PastebinPaste</code> instance.
	 */
	public PastebinPaste() {
		this(null, null, null);
	}

	/**
	 * Creates a new <code>PastebinPaste</code> instance.
	 * 
	 * @param contents
	 *            the paste contents
	 */
	public PastebinPaste(String contents) {
		this(null, contents, null);
	}

	/**
	 * Creates a new <code>PastebinPaste</code> instance.
	 * 
	 * @param account
	 *            a pastebin account
	 */
	public PastebinPaste(PastebinAccount account) {
		this(account.getDeveloperKey(), null, account);
	}

	/**
	 * Creates a new <code>PastebinPaste</code> instance.
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param contents
	 *            the contents of the paste
	 */
	public PastebinPaste(String developerKey, String contents) {
		this(developerKey, contents, null);
	}

	/**
	 * Creates a new <code>PastebinPaste</code> instance.
	 * 
	 * @param developerKey
	 *            a developer key which can be fetched from the pastebin API
	 *            page
	 * @param contents
	 *            the contents of the paste
	 * @param account
	 *            a pastebin account
	 */
	public PastebinPaste(String developerKey, String contents,
			PastebinAccount account) {
		super(contents);
		this.developerKey = developerKey;
		this.account = account;
	}

	/**
	 * Sets the pastebin account If you set an account the pastes will be listed
	 * on your account.
	 * 
	 * @param account
	 *            a pastebin account
	 */
	public void setAccount(PastebinAccount account) {
		this.account = account;
	}

	/**
	 * Gets the pastebin account
	 * 
	 * @return pastebin account
	 */
	public PastebinAccount getAccount() {
		return this.account;
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
	 * Sets the paste expire date
	 * 
	 * @param date
	 *            date when the paste will be removed
	 */
	public void setPasteExpireDate(PasteExpireDate date) {
		this.expireDate = date;
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
	 * Sets the paste title
	 * 
	 * @param title
	 *            title of the paste
	 */
	public void setPasteTitle(String title) {
		this.pasteTitle = title;
	}

	/**
	 * Gets paste title
	 * 
	 * @return paste title
	 */
	public String getPasteTitle() {
		return this.pasteTitle;
	}
	
	/**
	 * Gets paste expire date
	 * @return paste expire date
	 */
	public PasteExpireDate getPasteExpireDate() {
		return this.expireDate;
	}

	/**
	 * Sets the paste format The format is used for syntax highlighting
	 * 
	 * @see <a href="http://pastebin.com/api#5">available syntax highlighting
	 *      formats</a>
	 * @param format
	 *            format of the paste
	 */
	public void setPasteFormat(String format) {
		this.pasteFormat = format;
	}

	/**
	 * Gets paste format
	 * 
	 * @return paste format
	 */
	public String getPasteFormat() {
		return this.pasteFormat;
	}

	/**
	 * Makes this paste private, unlisted or public Default visibility is public
	 * <p>
	 * <strong>Valid visibilities</strong>
	 * </p>
	 * <p>
	 * {@link PastebinPaste#VISIBILITY_PUBLIC}
	 * </p>
	 * <p>
	 * {@link PastebinPaste#VISIBILITY_UNLISTED}
	 * </p>
	 * <p>
	 * {@link PastebinPaste#VISIBILITY_PRIVATE}
	 * </p>
	 * 
	 * @param visibility
	 *            the paste it's visibility
	 */
	public void setVisibility(int visibility) {
		if (visibility < 0 || visibility > 2) {
			throw new IllegalArgumentException("Unknown visibility: "
					+ visibility);
		}
		this.visibility = visibility;
	}

	/**
	 * Gets this paste visibility
	 * <p>
	 * <strong>Valid visibilities</strong>
	 * </p>
	 * <p>
	 * {@link PastebinPaste#VISIBILITY_PUBLIC}
	 * </p>
	 * <p>
	 * {@link PastebinPaste#VISIBILITY_UNLISTED}
	 * </p>
	 * <p>
	 * {@link PastebinPaste#VISIBILITY_PRIVATE}
	 * </p>
	 * 
	 * @return visibility of this paste
	 */
	public int getVisibility() {
		return this.visibility;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PastebinLink paste() throws PasteException {
		if (getContents() == null || getContents().isEmpty()) {
			throw new IllegalStateException("Paste can not be null or empty.");
		}
		if (getDeveloperKey() == null || getDeveloperKey().isEmpty()) {
			throw new IllegalStateException("Developer key is missing.");
		}

		Post post = new Post();

		// required parameters
		post.put("api_dev_key", getDeveloperKey());
		post.put("api_option", "paste");
		post.put("api_paste_code", getContents());

		// optional parameters
		if (this.account != null && this.account.getUserSessionId() != null) {
			post.put("api_user_key", this.account.getUserSessionId());
		}
		if (this.pasteTitle != null) {
			post.put("api_paste_name", getPasteTitle());
		}
		if (this.pasteFormat != null) {
			post.put("api_paste_format", getPasteFormat());
		}
		post.put("api_paste_private", Integer.toString(getVisibility()));
		if (this.expireDate != null) {
			post.put("api_paste_expire_date", expireDate.getValue());
		}

		try {
			String pageResponse = Web.getContents(Pastebin.API_POST_LINK, post);
			if (pageResponse.startsWith("http")) {
				// success
				PastebinLink result = new PastebinLink(this, new URL(
						pageResponse));
				return result;
			}
			throw new PasteException("Failed to generate paste: "
					+ pageResponse);
		} catch (MalformedURLException e) {
			// shouldn't happen
			throw new PasteException("Failed to generate paste: " + e);
		}
	}

}
