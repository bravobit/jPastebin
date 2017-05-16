import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.Pastebin;

/**
 * 
 * Example on how to generate a simple paste
 * 
 * @author Brian B
 *
 */
public class SimplePasteExample {
	
	public static void main(String[] args) throws PasteException {
		String developerKey = "INSERT DEVELOPER KEY HERE";
		String title = "My first jPastebin paste!"; // insert your own title
		String contents = "Hello world"; // insert your own paste contents
		
		// paste, get URL & print
		System.out.println(Pastebin.pastePaste(developerKey, contents, title));
	}

}
