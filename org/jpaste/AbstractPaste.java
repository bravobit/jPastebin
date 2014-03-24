package org.jpaste;

import org.jpaste.exceptions.PasteException;

/**
 * 
 * An abstract representation of a paste.
 * 
 * <p>
 * An abstract paste holds the paste contents and the {@link #paste()}
 * operation.
 * </p>
 * 
 * @author Brian B
 * 
 * @param <P>
 *            PasteResult implementation
 */
public abstract class AbstractPaste<P extends AbstractPasteLink> {
	private String contents;

	/**
	 * Creates a new abstract <code>AbstractPaste</code> instance.
	 * 
	 * @param contents
	 *            the contents of the paste
	 */
	public AbstractPaste(String contents) {
		this.contents = contents;
	}

	/**
	 * Gets paste contents
	 * 
	 * @return paste contents
	 */
	public String getContents() {
		return contents;
	}

	/**
	 * Sets the paste contents
	 * 
	 * @param contents
	 *            contents of the paste
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	/**
	 * Attempts to paste this paste and returns the results
	 * 
	 * @return paste result
	 * @throws PasteException if it failed to paste the paste
	 */
	public abstract P paste() throws PasteException;

}
