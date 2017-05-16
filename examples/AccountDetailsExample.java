import java.net.MalformedURLException;

import org.jpaste.pastebin.account.PastebinAccount;
import org.jpaste.pastebin.account.PastebinAccountDetails;
import org.jpaste.pastebin.exceptions.LoginException;
import org.jpaste.pastebin.exceptions.ParseException;

/**
 * 
 * An example on how to fetch account details from a pastebin account
 * 
 * @author Brian B
 *
 */
public class AccountDetailsExample {
	
	public static void main(String[] args) throws LoginException, ParseException, MalformedURLException {
		String[] credentials = { "YOUR USERNAME HERE", "YOUR PASSWORD HERE" };
		String developerKey = "INSERT DEVELOPER KEY HERE";
				
		PastebinAccount account = new PastebinAccount(developerKey, credentials[0], credentials[1]);
		// fetches an user session id
		account.login();
		
		// fetch account details
		PastebinAccountDetails details = account.getAccountDetails();
		
		// print results
		System.out.println("Username: " + details.getUsername());
		System.out.println("User format: " + details.getFormat());
		System.out.println("User expiration: " + details.getExpiration());
		System.out.println("User avatar URL: " + details.getAvatarURL());
		System.out.println("User website URL: " + details.getWebsite());
		System.out.println("User e-mail: " + details.getEmail());
		System.out.println("User location: " + details.getLocation());
		System.out.println("Pro: " + String.valueOf(details.isPro()));
	}

}
