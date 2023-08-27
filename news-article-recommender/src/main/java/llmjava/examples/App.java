package llmjava.examples;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.llm4j.api.ChatHistory;
import org.llm4j.api.LLM4J;
import org.llm4j.api.LanguageModel;
import org.llm4j.palm.PaLMLanguageModel;

import java.util.*;

/**
 * news-article-recommender application
 */
public class App {

    /**
     * Errors:
     * io.grpc.StatusRuntimeException: INVALID_ARGUMENT: Request payload size exceeds the limit: 10000 bytes
     */
    public static void embed(LanguageModel llm, Article article) {
        String text = article.getText();
        // if text too long take a subset from the right
        if(text.length()>1000) {
            text = text.substring(text.length()-1000);
        }
        List<Float> embeddings = llm.embed(text);
        article.setEmbeddings(embeddings);
    }

    public static void extractTags(LanguageModel llm, Article article) {
        try {
            extractTagsUnsafe(llm, article);
        } catch (Exception e) {
            System.out.println("Failed: " + article);
            System.out.println(article.text);
            e.printStackTrace();
        }
    }
    public static void extractTagsUnsafe(LanguageModel llm, Article article) {
        String prompt = "Given a news article, this program returns the list tags containing keywords of that article." + "\n"
                + "Article: japanese banking battle at an end japan s sumitomo mitsui financial has withdrawn its takeover offer for rival bank ufj holdings  enabling the latter to merge with mitsubishi tokyo.  sumitomo bosses told counterparts at ufj of its decision on friday  clearing the way for it to conclude a 3 trillion" + "\n"
                + "Tags: sumitomo mitsui financial, ufj holdings, mitsubishi tokyo, japanese banking" + "\n"
                + "--" + "\n"
                + "Article: france starts digital terrestrial france has become the last big european country to launch a digital terrestrial tv (dtt) service.  initially  more than a third of the population will be able to receive 14 free-to-air channels. despite the long wait for a french dtt roll-out" + "\n"
                + "Tags: france, digital terrestrial" + "\n"
                + "--" + "\n"
                + "Article: apple laptop is  greatest gadget  the apple powerbook 100 has been chosen as the greatest gadget of all time  by us magazine mobile pc.  the 1991 laptop was chosen because it was one of the first  lightweight  portable computers and helped define the layout of all future notebook pcs." + "\n"
                + "Tags: apple, apple powerbook 100, laptop" + "\n"
                + "--" + "\n"
                + "Article: " + article.text + "" + "\n"
                + "Tags:";
        String rawTags = llm.process(prompt);
        // clean result
        Set<String> tagSet = new HashSet<>();
        for(String tag: rawTags.split(",")) {
            tag = tag.trim(); // remove trailing spaces
            tag = String.join(" ", Arrays.asList(tag.split(" "))); // remove empty spaces
            if(tag.length() > 3) // remove short tags
                tagSet.add(tag); // remove duplicates
        }
        // update article with tags
        article.setTags(tagSet);
    }

    public static void main(String[] args) {
        // Setup configuration
        Map<String, String> configMap = new HashMap<String, String>(){{
            put("palm.apiKey", "${env:PALM_API_KEY}");
            put("es.url", "${env:ELASTIC_URL}");
            put("es.apiKey", "${env:ELASTIC_API_KEY}");
        }};
        Configuration config = new MapConfiguration(configMap);

        // Setup language model
        LanguageModel llm = LLM4J.getLanguageModel(config, new PaLMLanguageModel.Builder());

        // Setup vector db
        VectorDb vectorDb = new VectorDb(config.getString("es.url"), config.getString("es.apiKey"));
        vectorDb.setup("news", "news_index_mappings.json");

        // Step 1: Get a List of Articles
        NewsDataset dataset = new NewsDataset();
        dataset.load("bbc_news_test.csv");

        // Step 2: Embed Articles, Extract tags and store in vector DB
        for(Article article: dataset) {
            embed(llm, article);
            extractTags(llm, article);
            // TODO: Classify article to: Business, Politics, Tech, Entertainment, and Sport
            vectorDb.addDocument("news", article);
        }

        // Step 3: Create Article Selector
        Article article = dataset.sample();
        embed(llm, article);

        // Step 4: Find the Most Similar Articles
        vectorDb.search("news", article.getEmbeddings());

        // Clean up
        vectorDb.teardown("news");
        vectorDb.shutdown();
    }
}
