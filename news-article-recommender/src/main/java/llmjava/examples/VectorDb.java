package llmjava.examples;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldAndFormat;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Refer to for instructions on how to setup Elasticsearch Java client https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/getting-started-java.html
 */
public class VectorDb {
    private final ElasticsearchClient esClient;

    VectorDb(String serverUrl, String apiKey) {
        this.esClient = createESClient(serverUrl, apiKey);
    }


    public ElasticsearchClient createESClient(String serverUrl, String apiKey) {
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }

    public void setup(String indexName, String mappingsFile) {
        System.out.println("Creating index: " + indexName);
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(mappingsFile);
            CreateIndexRequest request = new CreateIndexRequest.Builder()
                    .index(indexName)
                    .withJson(is)
                    .build();
            esClient.indices().create(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDocument(String indexName, Identifiable document) {
        try {
            IndexResponse response = esClient.index(i -> i
                    .index(indexName)
                    .id(document.getId())
                    .document(document)
            );
            if(response.result().jsonValue().equalsIgnoreCase("created")) {
                System.out.println("Successfully added document: " + document);
            } else {
                System.out.println("Failed to add document: " + document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Article> search(String indexName, List<Float> embeddings) {
        List<Article> result = new ArrayList<>();
        try {
            SearchRequest request = new SearchRequest.Builder()
                    .index(indexName)
                    .knn(builder -> builder
                            .k(3)
                            .numCandidates(10)
                            .field("embeddings")
                            .queryVector(embeddings)
                    )
                    .fields(new FieldAndFormat.Builder().field("title").build())
                    .build();
            SearchResponse<Article> response = esClient.search(request, Article.class);
            if(response.shards().failed().intValue() > 0) {
                System.out.println("Search failed");
            }
            for(Hit<Article> hit: response.hits().hits()) {
                Article article = hit.source();
                Double score = hit.score();
                System.out.println("Score: " + score + ", Article: " + article);
                result.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void teardown(String indexName) {
        System.out.println("Deleting index: " + indexName);
        try {
            DeleteIndexRequest request = new DeleteIndexRequest.Builder()
                    .index(indexName)
                    .build();
            esClient.indices().delete(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        esClient.shutdown();
    }

    /**
     * Create an API client on kibana at http://localhost:5601/app/management/security/api_keys/
     */
    public static void main(String[] args) {
        // URL and API key
        String serverUrl = "http://localhost:9200";
        String apiKey = "";

        VectorDb client = new VectorDb(serverUrl, apiKey);
        client.setup("news", "news_index_mappings.json");

        Article product = new Article("bk-1", "City bike", null);
        client.addDocument("news", product);

        client.teardown("news");
        client.shutdown();
    }
}
