import org.jpaste.pastebin.Pastebin;
import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.exceptions.ParseException;

/**
 * 
 * Example on how to get the most popular trending paste
 * 
 * @author Brian B
 *
 */
public class TrendingExample {
	
	public static void main(String[] args) throws ParseException {
		// INSERT DEVELOPER KEY
		String developerKey = "INSERT DEVELOPER KEY HERE";
		
		PastebinLink mostPopular = null;
		// get pastebin link with most hits
		for(PastebinLink pasteLink : Pastebin.getTrending(developerKey)) {
			if(mostPopular == null || mostPopular.getHits() < pasteLink.getHits()) {
				mostPopular = pasteLink;
			}
		}
		
		// fetch the contents from the paste
		mostPopular.fetchContent(); // this method fetches the content of the paste
		
		// print the result
		System.out.println("Link: " + mostPopular.getLink());
		System.out.println("Hits: " + mostPopular.getHits());
		System.out.println();
		System.out.println("Title: " + mostPopular.getPaste().getPasteTitle());
		System.out.println();
		System.out.println("[ Contents ]");
		System.out.println();
		System.out.println(mostPopular.getPaste().getContents());
	}

}
