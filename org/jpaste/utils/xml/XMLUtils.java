package org.jpaste.utils.xml;

import org.w3c.dom.Element;

/**
 * 
 * Holds various XML utility methods
 * 
 * @author Brian B
 * 
 */
public class XMLUtils {

	/**
	 * Fetches text from a element
	 * 
	 * @param parent
	 *            the parent of the element you want to fetch text from
	 * @param tagName
	 *            name of the element you want to fetch text from
	 * @return text of tag
	 */
	public static String getText(Element parent, String tagName) {
		return parent.getElementsByTagName(tagName).item(0).getTextContent();
	}

}
