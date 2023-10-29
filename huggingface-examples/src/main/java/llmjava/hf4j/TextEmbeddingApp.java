package llmjava.hf4j;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import llmjava.hf_text_embeddings.ApiClient;
import llmjava.hf_text_embeddings.ApiException;
import llmjava.hf_text_embeddings.ApiResponse;
import llmjava.hf_text_embeddings.client.TextEmbeddingsInferenceApi;
import llmjava.hf_text_embeddings.model.EmbedRequest;
import llmjava.hf_text_embeddings.model.Info;

/**
 * Examples for interacting with HuggingFace Text Embeddings API in java
 * See https://huggingface.co/inference-api
 */
public class TextEmbeddingApp {

    /**
     * Change this when running the inference endpoint, e.g. 127.0.0.1:8080
     */
    static String HF_API_URL = "https://api-inference.huggingface.co";

    public static TextEmbeddingsInferenceApi createApi(Boolean interceptResponse, String url) {
        System.out.println("API_URL: " + url);
        String HF_TOKEN = System.getenv("HF_API_KEY");
        
        ApiClient client = new ApiClient()
            .setRequestInterceptor(new Consumer<HttpRequest.Builder>() {
                @Override public void accept(HttpRequest.Builder builder) {
                    builder.header("Authorization", "Bearer " + HF_TOKEN);
                }
            });
        if(interceptResponse) {
            client.setResponseInterceptor(new Consumer<HttpResponse<InputStream>>() {
                @Override public void accept(HttpResponse<InputStream> response) {
                    try {
                        byte[] bytes = response.body().readAllBytes();
                        String content = new String(bytes, StandardCharsets.UTF_8);
                        System.out.println("Received response:\n" + content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        client.updateBaseUri(url);
        ObjectMapper mapper = client.getObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        
        return new TextEmbeddingsInferenceApi(client);
    }

    public static void submitEmbedRequest() {
        String modelId = "sentence-transformers/all-MiniLM-L6-v2";
        String API_URL = HF_API_URL + "/models/" + modelId ;
        TextEmbeddingsInferenceApi api = createApi(false, API_URL);

        // Compat Generation request
        EmbedRequest embedRequest = new EmbedRequest().addInputsItem("Hi");
        try {
        List<List<Float>> response = api.embed(embedRequest);
        System.out.println("Generated embeddings" + response);
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitEmbedRequestWithInfo() {
        String modelId = "sentence-transformers/all-MiniLM-L6-v2";
        String API_URL = HF_API_URL + "/models/" + modelId ;
        TextEmbeddingsInferenceApi api = createApi(false, API_URL);

        // Compat Generation request
        EmbedRequest embedRequest = new EmbedRequest().addInputsItem("Hi");
        try {
            ApiResponse<List<List<Float>>> response = api.embedWithHttpInfo(embedRequest);
        System.out.println("Generated embeddings" + response.getData());
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitModelInfo() {
        TextEmbeddingsInferenceApi api = createApi(false, HF_API_URL);
        // Model info
        try {
        Info modelInfoResponse = api.getModelInfo();
        System.out.println("Model info:\n" + modelInfoResponse);
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitApiHealth() {
        TextEmbeddingsInferenceApi api = createApi(false, HF_API_URL);
        // Health request
        try {
        api.health();

        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitMetrics() {
        TextEmbeddingsInferenceApi api = createApi(false, HF_API_URL);
        // Metrics request
        try {
        String metricsResponse = api.metrics();
        System.out.println("Metrics response:\n" + metricsResponse);
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * See https://huggingface.co/inference-api
     */
    public static void main(String[] args) {
        submitEmbedRequest();

        submitModelInfo();

        submitApiHealth();

        submitMetrics();
    }
    
}
