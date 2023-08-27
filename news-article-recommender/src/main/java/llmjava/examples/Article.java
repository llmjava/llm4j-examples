package llmjava.examples;

import java.util.List;
import java.util.Set;
import java.util.UUID;

class Article implements Identifiable {
    String id;
    String title;
    String text;

    Set<String> tags;
    private List<Float> embeddings;

    public Article() {}
    public Article(String title, String text, List<Float> embeddings) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.text = text;
        this.embeddings = embeddings;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setEmbeddings(List<Float> embeddings) {
        this.embeddings = embeddings;
    }

    public List<Float> getEmbeddings() {
        return embeddings;
    }

    @Override
    public String toString() {
        return "Article[id:" + id + ", title:" + title + ", tags: " + tags + "]";
    }
}
