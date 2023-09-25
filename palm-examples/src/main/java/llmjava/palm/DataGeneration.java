package llmjava.palm;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.llm4j.api.LLM4J;
import org.llm4j.api.LanguageModel;
import org.llm4j.api.PromptTemplate;
import org.llm4j.palm.PaLMLanguageModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import llmjava.wikipedia.Wikipedia;
import llmjava.wikipedia.Document;

/*
 */
public class DataGeneration {

    public static LanguageModel initLLM() {
        // Setup configuration
        Map<String, String> configMap = new HashMap<String, String>(){{
            put("palm.apiKey", "${env:PALM_API_KEY}");
            put("palm.modelId", "models/text-bison-001");
            put("topK", "40");
            put("topP", "0.95");
            put("temperature", "0.7");
            put("maxNewTokens", "1024");
            put("maxOutputTokens", "1024");
            put("candidateCount", "1");
        }};
        Configuration config = new MapConfiguration(configMap);

        // Setup language model
        return LLM4J.getLanguageModel(config, new PaLMLanguageModel.Builder());
    }

    public static String generate(LanguageModel palm, WikipediaParser.Section section) {
        String jsoString = null;
        try {
            String text = section.title + ", " + section.text;
            // TODO check if size of the text is too long
            String prompt = new PromptTemplate()
                .withFile("dataset_questions.template")
                .withParam("delimiter", "####")
                .withParam("TEXT", text)
                .render();
            jsoString = palm.process(prompt);
        } catch (Exception e) {
            System.out.println("Failed to generate question");
            e.printStackTrace();
        }
        return jsoString;
    }

    public static void main(String[] args) throws Exception {
        LanguageModel palm = initLLM();

        Wikipedia wiki = new Wikipedia();
        List<Document> results = wiki.search("Ibn Battuta");
        String wikiText = results.get(0).getText();
        List<WikipediaParser.Section> sections = WikipediaParser.extractSections(wikiText);

        Random rnd = new Random(123);
        List<Question> questions = new ArrayList<>();
        Type objectType = new TypeToken<Question>(){}.getType();
        Type listType = new TypeToken<List<Question>>(){}.getType();
        Gson gson = new Gson();
        // seclect sections
        for (int i=0; i<5; i++) {
            System.out.println("================= Section =================");
            int idx = rnd.nextInt(sections.size());
            WikipediaParser.Section section = sections.get(idx);
            System.out.println(section.title + ", " + section.text);
            // generate questions
            for (int j=0; j<2; j++) {
                System.out.println("              --- Question ---");
                String jsonString = generate(palm, section);
                Question question = Question.parse(gson, objectType, listType, jsonString);
                if(question!=null) questions.add(question);
            }
        }
        System.out.println("================= CSV =================");
        System.out.println(Question.toCSV(questions));
    }
}
