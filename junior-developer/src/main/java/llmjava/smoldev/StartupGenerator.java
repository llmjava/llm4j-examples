package llmjava.smoldev;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.llm4j.api.LLM4J;
import org.llm4j.api.LanguageModel;
import org.llm4j.palm.PaLMLanguageModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Ported from https://docs.cohere.com/page/startup-idea-generator
 *
 * Example generated result
 *   Industry: food
 *   Startup Idea: A subscription service that sends you fresh ingredients for a homemade meal every week
 *   Startup Name: Meal Maker
 *
 *   Industry: transportation
 *   Startup Idea: An app that lets users share their cars with people in their neighborhood while they're not using them
 *   Startup Name: Neighbourhood Cars
 */
public class StartupGenerator {

    private final LLMUtils utils;
    private Boolean debug = true;
    public StartupGenerator(LanguageModel llm) {
        this.utils = new LLMUtils(llm);
    }

    public String generateIdea(String industry) {
        String idea_prompt = new PromptTemplate()
                .withFile("startup_idea.template")
                .withParam("INDUSTRY", industry)
                .build();
        return utils.runStep("Idea", idea_prompt);
    }

    public String generateName(String idea) {
        String idea_prompt = new PromptTemplate()
                .withFile("startup_name.template")
                .withParam("IDEA", idea)
                .build();
        return utils.runStep("Name", idea_prompt);
    }

    public static void main(String[] args) {
        // Setup configuration
        Map<String, String> configMap = new HashMap<String, String>(){{
            put("palm.apiKey", "${env:PALM_API_KEY}");
            put("topK", "40");
            put("topP", "0.95");
            put("temperature", "0.75");
            put("maxNewTokens", "1024");
            put("maxOutputTokens", "1024");
            put("candidateCount", "1");
        }};
        Configuration config = new MapConfiguration(configMap);

        // Setup language model
        LanguageModel llm = LLM4J.getLanguageModel(config, new PaLMLanguageModel.Builder());

        StartupGenerator app = new StartupGenerator(llm);

        String idea1 = app.generateIdea("transportation");
        String name1 = app.generateName(idea1);
    }
}
