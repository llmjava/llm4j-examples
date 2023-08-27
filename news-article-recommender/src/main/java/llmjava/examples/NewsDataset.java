package llmjava.examples;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class NewsDataset implements Iterable<Article> {
    static Random RND = new Random();
    List<Article> articles = new ArrayList<>();


    /**
     * Load the dataset
     */
    public void load(String fileName) {
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withDelimiter(',')
                    .withQuote('"')
                    .withIgnoreEmptyLines();

            ClassLoader classloader = getClass().getClassLoader();
            Path path = Paths.get(classloader.getResource(fileName).toURI());
            CSVParser csvParser = CSVParser.parse(path, StandardCharsets.UTF_8, csvFormat);

            for(CSVRecord csvRecord : csvParser) {
                String title = csvRecord.get("title");
                String news = csvRecord.get("news");
                Article article = new Article(title, news, Collections.emptyList());
                articles.add(article);
            }

            csvParser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Article sample() {
        int i = RND.nextInt(articles.size());
        return get(i);
    }

    public Article get(int i) {
        return articles.get(i);
    }

    @NotNull
    @Override
    public Iterator<Article> iterator() {
        return articles.iterator();
    }
}
