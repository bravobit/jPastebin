package org.jpaste;

import java.net.URL;

/**
 * 
 * An representation of an abstract PasteLink
 * 
 * <p>
 * An AbstractPasteLink holds the link/URL to a paste.
 * </p>
 * 
 * @author Brian B
 * 
 */
public abstract class AbstractPasteLink {

	/**
	 * Gets the URL to this paste
	 * 
	 * @return URL to paste
	 */
	public abstract URL getLink();

}
