import org.jpaste.pastebin.Pastebin;
import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.exceptions.ParseException;
/**
 * This class shows how to use getPaste.
 * In order to get a paste, it's necessary to have the IP white-listed.
 * See more at <a href="https://pastebin.com/api_scraping_faq">Pastebin Scraping API</a>
 * 
 * @author Felipe
 */
public class GetPaste {
    public static void main(String[] args) throws ParseException {
        String key = "pastebinKey";
        PastebinLink pastebinLink = Pastebin.getPaste(key);
        pastebinLink.fetchContent();
        System.out.println(pastebinLink.getPaste().getContents());
    }
}