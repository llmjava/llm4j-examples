package llmjava.smoldev;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.llm4j.api.LLM4J;
import org.llm4j.api.LanguageModel;
import org.llm4j.palm.PaLMLanguageModel;
import java.util.HashMap;
import java.util.Map;

public class ApplicationGenerator {

    static final String SYSTEM_PROMPT = "You are a top tier AI developer who is trying to write a program that will generate code for the user based on their intent.\n" +
            "Do not leave any todos, fully implement every feature requested.\n" +
            "\n" +
            "When writing code, add comments to explain what you intend to do and why it aligns with the program plan and specific instructions from the original prompt.";

    private final LLMUtils utils;
    private Boolean debug = true;
    public ApplicationGenerator(LanguageModel llm) {
        this.utils = new LLMUtils(llm);
    }

    public String generatePlan(String prompt) {
        String plan_prompt = new PromptTemplate()
                .withFile("plan_prompt.template")
                .withParam("SYSTEM_PROMPT", SYSTEM_PROMPT)
                .withParam("USER_PROMPT", prompt)
                .build();
        return utils.runStep("Plan", plan_prompt);
    }

    public String listFilePaths(String prompt, String plan) {
        String list_prompt = new PromptTemplate()
                .withFile("file_paths.template")
                .withParam("SYSTEM_PROMPT", SYSTEM_PROMPT)
                .withParam("USER_PROMPT", prompt)
                .withParam("PLAN", plan)
                .build();
        return utils.runStep("List Files", list_prompt);
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

        ApplicationGenerator app = new ApplicationGenerator(llm);
        String USER_PROMPT = "a simple JavaScript/HTML/CSS/Canvas app that is a one player game of PONG. \n" +
                "  The left paddle is controlled by the player, following where the mouse goes.\n" +
                "  The right paddle is controlled by a simple AI algorithm, which slowly moves the paddle toward the ball at every frame, with some probability of error.\n" +
                "  Make the canvas a 400 x 400 black square and center it in the app.\n" +
                "  Make the paddles 100px long, yellow and the ball small and red.\n" +
                "  Make sure to render the paddles and name them so they can controlled in javascript.\n" +
                "  Implement the collision detection and scoring as well.\n" +
                "  Every time the ball bounces off a paddle, the ball should move faster.\n" +
                "  It is meant to run in Chrome browser, so don't use anything that is not supported by Chrome, and don't use the import and export keywords.";

        String plan = app.generatePlan(USER_PROMPT);

        String paths = app.listFilePaths(USER_PROMPT, plan);
    }
}
