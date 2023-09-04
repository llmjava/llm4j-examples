package llmjava.smoldev;

import org.llm4j.api.LanguageModel;

public class LLMUtils {

    private final LanguageModel llm;
    private Boolean debug = true;
    public LLMUtils(LanguageModel llm) {
        this.llm = llm;
    }

    public String runStep(String name, String prompt) {
        if(debug) {
            System.out.println("------------------------------- "+name+"  Prompt -------------------------------");
            System.out.println(prompt);
        }
        String response = llm.process(prompt);
        if(debug) {
            System.out.println("------------------------------- "+name+" Response ------------------------------");
            System.out.println(response);
            System.out.println("---------------------------------------------------------------------------");
        }
        return response;
    }
}
