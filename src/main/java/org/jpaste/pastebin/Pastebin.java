package org.jpaste.pastebin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.exceptions.ParseException;
import org.jpaste.utils.web.Post;
import org.jpaste.utils.web.Web;
import org.jpaste.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * A global representation of the pastebin site
 * 
 * <p>
 * Holds various constants and method shortcuts
 * </p>
 * 
 * @author Brian B
 * 
 */
public class Pastebin {
    /**
     * Used to interact with the pastebin API
     */
    public static final String API_POST_LINK = "https://pastebin.com/api/api_post.php";
    /**
     * Used for fetching an user session id
     */
    public static final String API_LOGIN_LINK = "https://pastebin.com/api/api_login.php";
    /**
     * Scraping api. Must be lifetime pro in otrder to use it.
     */
    public static final String API_SCRAPING_LINK = "https://pastebin.com/api_scraping.php";
    /**
     * Scrape paste metadata link. Must be pro lifetime in order to use it.
     */
    public static final String SCRAPE_PASTE_METADATA_URL = "https://pastebin.com/api_scrape_item_meta.php?i=";

    /**
     * Fetches a paste text from pastebin
     *
     * @param pasteKey
     *            the unique paste key
     * @return contents of the paste
     */
    public static String getContents(String pasteKey) {
        return PastebinLink.getContents(pasteKey);
    }

    /**
     * Generates a paste on pastebin and returns the URL to it
     * 
     * @param developerKey
     *            a developer key which can be fetched from the pastebin API
     *            page
     * @param contents
     *            contents of the paste
     * @return URL to paste
     * @throws PasteException
     *             if it failed to push the paste
     */
    public static URL pastePaste(String developerKey, String contents) throws PasteException {
        return pastePaste(developerKey, contents, null);
    }

    /**
     * Generates a paste on pastebin and returns the URL to it
     * 
     * @param developerKey
     *            a developer key which can be fetched from the pastebin API
     *            page
     * @param contents
     *            contents of the paste
     * @param title
     *            title of the paste
     * @return URL to paste
     * @throws PasteException
     *             if it failed to push the paste
     */
    public static URL pastePaste(String developerKey, String contents, String title) throws PasteException {
        return newPaste(developerKey, contents, title).paste().getLink();
    }

    /**
     * Generates a new paste and returns it
     * 
     * @param developerKey
     *            a developer key which can be fetched from the pastebin API
     *            page
     * @param contents
     *            contents of the paste
     * @param title
     *            title of the paste
     * @return a new paste
     */
    public static PastebinPaste newPaste(String developerKey, String contents, String title) {
        PastebinPaste paste = new PastebinPaste(developerKey, contents);
        paste.setPasteTitle(title);
        return paste;
    }

    /**
     * Generates a new paste and returns it
     * 
     * @param developerKey
     *            a developer key which can be fetched from the pastebin API
     *            page
     * @param contents
     *            contents of the paste
     * @return a new paste
     */
    public static PastebinPaste newPaste(String developerKey, String contents) {
        return newPaste(developerKey, contents, null);
    }

    /**
     * Gets the current trending pastebin pastes
     * 
     * @param developerKey
     *            a developer key which can be fetched from the pastebin API
     *            page
     * @return an array of {@link PastebinLink}
     * @throws ParseException
     *             if it failed to parse the trending pastes
     */
    public static PastebinLink[] getTrending(String developerKey) throws ParseException {
        if (developerKey == null || developerKey.isEmpty()) {
            throw new IllegalArgumentException("Developer key can't be null or empty.");
        }
        Post post = new Post();
        post.put("api_dev_key", developerKey);
        post.put("api_option", "trends");

        String response = Web.getContents(API_POST_LINK, post);

        if (response.startsWith("<paste>")) {
            // success
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                response = "<dummy>" + response + "</dummy>"; // requires root
                                                              // element
                Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));
                doc.getDocumentElement().normalize();

                NodeList nodes = doc.getElementsByTagName("paste");
                ArrayList<PastebinLink> pastes = new ArrayList<PastebinLink>(nodes.getLength());
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        String pasteFormat = XMLUtils.getText(element, "paste_format_short");
                        String title = XMLUtils.getText(element, "paste_title");
                        int visibility = Integer.parseInt(XMLUtils.getText(element, "paste_private"));
                        int hits = Integer.parseInt(XMLUtils.getText(element, "paste_hits"));

                        long expireDate = Long.parseLong(XMLUtils.getText(element, "paste_expire_date"));
                        long pasteDate = Long.parseLong(XMLUtils.getText(element, "paste_date"));

                        URL pasteURL = new URL(XMLUtils.getText(element, "paste_url"));

                        PastebinPaste paste = new PastebinPaste();
                        paste.setPasteFormat(pasteFormat);
                        paste.setPasteTitle(title);
                        paste.setVisibility(visibility);
                        paste.setPasteExpireDate(expireDate == 0L ? PasteExpireDate.NEVER
                                : PasteExpireDate.getExpireDate((int) (expireDate - pasteDate)));

                        PastebinLink pastebinLink = new PastebinLink(paste, pasteURL, new Date(pasteDate * 1000));
                        pastebinLink.setHits(hits);

                        pastes.add(pastebinLink);
                    }
                }

                return pastes.toArray(new PastebinLink[pastes.size()]);
            } catch (Exception e) {
                throw new ParseException("Failed to parse pastes: " + e.getMessage());
            }

        }

        throw new ParseException("Failed to parse paste: " + response);
    }

    /**
     * Gets the most recent pastes. In order to use it, it's necessary to have a
     * <i><a href="https://pastebin.com/pro">pro</a></i> account and white-list your IP. Se more on
     * <a href="https://pastebin.com/api_scraping_faq">Pastebin Scraping Api</a>
     *
     * The options for the post are:
     * <ul>
     * <li>limit: up to 250 (default is 50)</li>
     * <li>lang: <a href="https://pastebin.com/languages">all the pastebin accepted formates</a></li>
     * </ul>
     *
     * @author Felipe
     * @param post
     *            the <code>Post</code> with the options
     * @return the pastes.
     * @throws ParseException
     */
    public static PastebinLink[] getMostRecent(Post post) throws ParseException {
        String url = API_SCRAPING_LINK;
        if (post != null && !post.getPost().isEmpty()) {
            url += "?" + post.getPost();
        }

        String response = Web.getContents(url);

        if (response == null || response.isEmpty()
                || !(response.charAt(0) == '[' && response.charAt(response.length() - 2) == ']')) {
            throw new ParseException("Failed to parse pastes: " + response);
        }

        ArrayList<Map<String, Object>> listData = getListJSonData(response);

        ArrayList<PastebinLink> listPastebinLink = new ArrayList<>(listData.size());
        for (Map<String, Object> tempMap : listData) {
            PastebinLink pastebinLink = jSonMapToPastebinLink(tempMap);

            if (pastebinLink != null) {
                listPastebinLink.add(pastebinLink);
            }
        }

        return listPastebinLink.toArray(new PastebinLink[listPastebinLink.size()]);
    }

    /**
     * Converts the <code>Map</code> with the json informations into a
     * <code>PastebinLink</code>.
     * 
     * Important: can't retrieve visibility and, once most of the technics from
     * API only alow parse public pasts, the resultant paste is tagged as
     * Public.
     * 
     * @author Felipe
     * @param tempMap
     *            the Json Map
     * @return PastebinLink with all the informations
     */
    private static PastebinLink jSonMapToPastebinLink(Map<String, Object> tempMap) {
        PastebinPaste pastebinPaste = new PastebinPaste();
        pastebinPaste.setPasteFormat(tempMap.get("syntax").toString());
        String pasteTitle = tempMap.get("title").toString();
        pastebinPaste.setPasteTitle(pasteTitle == null ? "" : pasteTitle);
        pastebinPaste.setPasteAuthor(tempMap.get("user").toString());
        long pasteExpireDate = Long.parseLong(tempMap.get("expire").toString());
        long pasteDate = Long.parseLong(tempMap.get("date").toString());
        pastebinPaste.setPasteExpireDate(pasteExpireDate == 0L ? PasteExpireDate.NEVER
                : PasteExpireDate.getExpireDate((int) (pasteExpireDate - pasteDate)));
        pastebinPaste.setVisibility(PastebinPaste.VISIBILITY_PUBLIC);
        // All the pastes retrieved from this api are public.

        PastebinLink pastebinLink = null;
        try {
            pastebinLink = new PastebinLink(pastebinPaste, new URL(tempMap.get("full_url").toString()),
                    new Date(pasteDate * 1000));
            String hits = tempMap.get("hits").toString();
            pastebinLink.setHits(hits != null && !hits.isEmpty() ? Integer.parseInt(hits) : 0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return pastebinLink;
    }

    /**
     * Returns the JSON response as a <code>ArrayList</code> of /code>Map</code>
     * 
     * @author Felipe
     * @param response
     *            the pastebin JSON response
     * @return the list of maps with the content
     */
    private static ArrayList<Map<String, Object>> getListJSonData(String response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<Map<String, Object>> data = mapper.readValue(response,
                    new TypeReference<ArrayList<Map<String, Object>>>() {
                    });
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all available informations from a paste, not only its contents,
     * generating a <code>PastebinLink</code>. Part of the
     * <a href="https://pastebin.com/api_scraping_faq">Scraping API</a>. Must be
     * <a href="https://pastebin.com/pro">Pro user</a> to use. The pastebinKey is the 7-8 size identifier of any
     * paste.
     * 
     * @author Felipe
     * @param key
     *            the pastebin key
     * @return the <code>PastebinLink</code> with the informations.
     * @throws ParseException
     */
    public static PastebinLink getPaste(String key) throws ParseException {
        String url = SCRAPE_PASTE_METADATA_URL + key;
        String response = Web.getContents(url);

        if (response == null || response.isEmpty()
                || !(response.charAt(0) == '[' && response.charAt(response.length() - 1) == ']')) {
            throw new ParseException("Failed to parse paste: " + response);
        }

        ArrayList<Map<String, Object>> listData = getListJSonData(response);

        Map<String, Object> tempMap = listData.get(0);
        PastebinLink pastebinLink = jSonMapToPastebinLink(tempMap);
        return pastebinLink;
    }
}