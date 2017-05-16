import org.jpaste.pastebin.Pastebin;
import org.jpaste.pastebin.PastebinLink;
import org.jpaste.pastebin.exceptions.ParseException;
import org.jpaste.utils.web.Post;

/**
 * Example about how to get the most recent pastes.
 * 
 * See more on <a href="https://pastebin.com/api_scraping_faq#4">Pastebin
 * Scraping API</a>
 * 
 * @author Felipe V Ramos
 */
public class MostRecentExample {

    public static void main(String[] args) throws ParseException {
        // Retrieving the most recent pastes:

        PastebinLink[] pastebinLinks = Pastebin.getMostRecent(null);
        System.out.println(pastebinLinks.length);

        // retrieving the 10 most recent java pastes:
        Post post = new Post();
        post.put("limit", "10");
        post.put("lang", "java");
        PastebinLink[] pastebinLinkJava = Pastebin.getMostRecent(post);
        System.out.println(pastebinLinkJava.length);

        for (PastebinLink pbl : pastebinLinkJava) {
            System.out.println(pbl.getPaste().getPasteFormat());
        }

    }
}
