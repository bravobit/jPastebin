import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.PasteExpireDate;
import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.PastebinPaste;

/**
 * 
 * Example on how to generate a simple paste with some settings
 * 
 * @author Brian B
 * 
 */
public class BasicSettingsPasteExample {

	public static void main(String[] args) throws PasteException {
		String developerKey = "INSERT DEVELOPER KEY HERE";
		String title = "My first jPastebin paste!"; // insert your own title
		String contents = "Hello world"; // insert your own paste contents

		PastebinPaste paste = new PastebinPaste();
		// required
		paste.setDeveloperKey(developerKey);
		paste.setContents(contents);
		
		// optional
		paste.setPasteTitle(title);
		paste.setPasteExpireDate(PasteExpireDate.ONE_MONTH); // default=never
		paste.setVisibility(PastebinPaste.VISIBILITY_UNLISTED); // default=public
		paste.setPasteFormat("java"); // default=text
		
		// push paste
		PastebinLink pastebinLink = paste.paste();
		
		// print link
		System.out.println(pastebinLink.getLink());
	}

}
