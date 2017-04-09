import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.PastebinPaste;
import org.jpaste.pastebin.account.PastebinAccount;
import org.jpaste.pastebin.exceptions.LoginException;

/**
 * 
 * Example on how to generate a private paste
 * 
 * @author Brian B
 *
 */
public class PrivatePasteExample {
	
	public static void main(String[] args) throws PasteException, LoginException {
		// private pastes requires a pastebin account
		String[] credentials = { "YOUR USERNAME HERE", "YOUR PASSWORD HERE" };
		String developerKey = "INSERT DEVELOPER KEY HERE";
		
		String title = "My first jPastebin private paste!"; // insert your own title
		String contents = "This is a secret"; // insert your own paste contents
		int visibility = PastebinPaste.VISIBILITY_PRIVATE; // makes paste private
		
		PastebinAccount account = new PastebinAccount(developerKey, credentials[0], credentials[1]);
		// fetches an user session id
		account.login();
		
		// create paste
		PastebinPaste paste = new PastebinPaste(account);
		paste.setContents(contents);
		paste.setPasteTitle(title);
		paste.setVisibility(visibility);
		
		// push paste
		PastebinLink link = paste.paste();
		System.out.println(link.getLink());
	}

}
