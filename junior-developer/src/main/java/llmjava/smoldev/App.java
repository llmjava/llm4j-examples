package llmjava.smoldev;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.text.StringSubstitutor;
import org.llm4j.api.LLM4J;
import org.llm4j.api.LanguageModel;
import org.llm4j.palm.PaLMLanguageModel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class App {

    static final String SYSTEM_PROMPT = "You are a top tier AI developer who is trying to write a program that will generate code for the user based on their intent.\n" +
            "Do not leave any todos, fully implement every feature requested.\n" +
            "\n" +
            "When writing code, add comments to explain what you intend to do and why it aligns with the program plan and specific instructions from the original prompt.";

    private final LanguageModel llm;
    public App(LanguageModel llm) {
        this.llm = llm;
    }

    public String generatePlan(String prompt) {
        String plan_prompt_template = readTemplate("plan_prompt.template");
        Map<String, Object> params = new HashMap<>();
        params.put("SYSTEM_PROMPT", SYSTEM_PROMPT);
        params.put("USER_PROMPT", prompt);
        String plan_prompt = StringSubstitutor.replace(plan_prompt_template, params, "${", "}");

        return llm.process(plan_prompt);
    }

    public String listFilePaths(String prompt, String plan) {
        String plan_prompt_template = readTemplate("file_paths.template");
        Map<String, Object> params = new HashMap<>();
        params.put("SYSTEM_PROMPT", SYSTEM_PROMPT);
        params.put("USER_PROMPT", prompt);
        params.put("PLAN", plan);
        String list_prompt = StringSubstitutor.replace(plan_prompt_template, params, "${", "}");

        return llm.process(list_prompt);
    }

    public String readTemplate(String fileName) {
        String template = "";
        try {
            ClassLoader classloader = getClass().getClassLoader();
            Path path = Paths.get(classloader.getResource(fileName).toURI());
            for(String line: Files.readAllLines(path)) {
                template += "\n" + line;
            }
            template = template.substring("\n".length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return template;
    }

    public static void main(String[] args) {
        // Setup configuration
        Map<String, String> configMap = new HashMap<String, String>(){{
            put("palm.apiKey", "${env:PALM_API_KEY}");
            put("topK", "3");
            put("topP", "0.4");
            put("temperature", "0.8");
            put("maxNewTokens", "1024");
            put("maxOutputTokens", "2048");
            put("candidateCount", "1");
        }};
        Configuration config = new MapConfiguration(configMap);

        // Setup language model
        LanguageModel llm = LLM4J.getLanguageModel(config, new PaLMLanguageModel.Builder());

        App app = new App(llm);
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
        System.out.println(plan);
        
        String paths = app.listFilePaths(USER_PROMPT, plan);
        System.out.println(paths);
    }
}
