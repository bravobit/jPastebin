import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.account.PastebinAccount;
import org.jpaste.pastebin.exceptions.LoginException;
import org.jpaste.pastebin.exceptions.ParseException;

/**
 * 
 * Example on how to list all your pasted pastes
 * 
 * @author Brian B
 *
 */
public class ListPastesExample {

	public static void main(String[] args) throws LoginException, ParseException {
		String[] credentials = { "YOUR USERNAME HERE", "YOUR PASSWORD HERE" };
		String developerKey = "INSERT DEVELOPER KEY HERE";
		
		PastebinAccount account = new PastebinAccount(developerKey, credentials[0], credentials[1]);
		// fetches an user session id
		account.login();
		
		PastebinLink[] pastes = account.getPastes();
		if(pastes == null) {
			System.out.println("You don't have any pastes!");
			return;
		}
		
		for(PastebinLink paste : pastes) {
			System.out.println("Link: " + paste.getLink());
			System.out.println("Hits: " + paste.getHits());
			System.out.println();
			System.out.println("Title: " + paste.getPaste().getPasteTitle());
			System.out.println();
			System.out.println("[ Contents ]");
			System.out.println();
			paste.fetchContent();
			System.out.println(paste.getPaste().getContents());
			System.out.println();
			System.out.println("[ END ]");
		}
	}

}
