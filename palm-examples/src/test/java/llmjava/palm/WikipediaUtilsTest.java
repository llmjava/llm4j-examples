package llmjava.palm;

import java.util.List;

public class WikipediaUtilsTest {
    

    public static void main(String[] args) {
        // Test the method with an example wikiText
        String wikiText = "= Header 1 =\n" +
        "\n" +
        "Some text.\n" +
        "\n" +
        "== Header 2 ==\n" +
        "\n" +
        "More text.\n" +
        "\n" +
        "=== Header 3 ===\n" +
        "\n" +
        "Even more text.\n" +
        "\n" +
        "=== Header 3 ===\n" +
        "\n" +
        "Even more more text.\n" +
        "\n" +
        "== Header 2 ==\n" +
        "\n" +
        "More more text.";

        List<WikipediaUtils.Section> sections = WikipediaUtils.extractSections(wikiText);
        System.out.println(sections);
    }
}
