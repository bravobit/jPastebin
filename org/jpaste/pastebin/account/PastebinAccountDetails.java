package org.jpaste.pastebin.account;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jpaste.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * 
 * Holds information and settings of a pastebin account.
 * 
 * @author Brian B
 * 
 */
public class PastebinAccountDetails {
	private String username, format, expiration, avatarURL, website, email, location;
	private int userPrivate, accountType;

	/**
	 * Creates a new <code>PastebinAccountDetails</code> instance.
	 * 
	 * @param userElement
	 *            the 'user' xml elements received from the pastebin API
	 */
	public PastebinAccountDetails(Element userElement) {
		this.username = XMLUtils.getText(userElement, "user_name");
		this.format = XMLUtils.getText(userElement, "user_format_short");
		this.expiration = XMLUtils.getText(userElement, "user_expiration");
		this.avatarURL = XMLUtils.getText(userElement, "user_avatar_url");
		this.userPrivate = Integer.parseInt(XMLUtils.getText(userElement, "user_private"));
		this.website = XMLUtils.getText(userElement, "user_website");
		this.email = XMLUtils.getText(userElement, "user_email");
		this.location = XMLUtils.getText(userElement, "user_location");
		this.accountType = Integer.parseInt(XMLUtils.getText(userElement, "user_account_type"));
	}
	
	/**
	 * Gets the username of this account
	 * @return username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Gets user text format
	 * @return user text format
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * Gets this account expiration time
	 * <p>
	 * <code>N = never (default)</code>
	 * </p>
	 * @return account expiration time
	 */
	public String getExpiration() {
		return this.expiration;
	}
	
	/**
	 * Gets URL to avatar image
	 * @return URL to avatar image
	 * @throws MalformedURLException
	 */
	public URL getAvatarURL() throws MalformedURLException {
		return new URL(this.avatarURL);
	}
	
	/**
	 * Gets user visibility
	 * <pre>
	 * 0 = public
	 * 1 = unlisted
	 * 2 = private
	 * </pre>
	 * @return visibility of account
	 */
	public int getPrivate() {
		return this.userPrivate;
	}
	
	/**
	 * Gets the user's set website
	 * @return url to website
	 * @throws MalformedURLException
	 */
	public URL getWebsite() throws MalformedURLException {
		if(this.website.isEmpty()) {
			return null;
		}
		return new URL(this.website);
	}
	
	/**
	 * Gets the user e-mail
	 * @return user account e-mail
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * Gets the user's set location
	 * @return location, city
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Determines if this account is a 'pro' account
	 * @return <code>true</code> if this account is a pro account, otherwise <code>false</code>.
	 */
	public boolean isPro() {
		return accountType == 1;
	}
	
	/**
	 * Fetches the user his avatar from {@link #getAvatarURL()}
	 * @return image
	 * @throws IOException if image was not read
	 */
	public BufferedImage getAvatar() throws IOException {
		return ImageIO.read(getAvatarURL());
	}
}
