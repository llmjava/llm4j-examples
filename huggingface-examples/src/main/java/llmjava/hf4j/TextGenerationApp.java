package llmjava.hf4j;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import llmjava.hf_text_generation.ApiClient;
import llmjava.hf_text_generation.ApiException;
import llmjava.hf_text_generation.client.TextGenerationInferenceApi;
import llmjava.hf_text_generation.model.CompatGenerateRequest;
import llmjava.hf_text_generation.model.GenerateParameters;
import llmjava.hf_text_generation.model.GenerateRequest;
import llmjava.hf_text_generation.model.GenerateResponse;
import llmjava.hf_text_generation.model.Info;
import llmjava.hf_text_generation.model.StreamResponse;

/**
 * Examples for interacting with HuggingFace Text Generation API in java
 * See https://huggingface.co/inference-api
 */
public class TextGenerationApp {

    /**
     * Change this when running the inference endpoint elsewhere: 127.0.0.1:8080
     * See documentation https://huggingface.co/docs/text-generation-inference
     */
    static String HF_API_URL = "https://api-inference.huggingface.co";

    public static TextGenerationInferenceApi createApi(Boolean interceptResponse, String url) {
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
        
        return new TextGenerationInferenceApi(client);
    }

    public static void submitCompatGenerateRequest() {
        String modelId = "tiiuae/falcon-7b-instruct";
        String API_URL = HF_API_URL + "/models/" + modelId ;
        TextGenerationInferenceApi api = createApi(false, API_URL);

        GenerateParameters params = new GenerateParameters()
            .seed(123L)
            .temperature(0.7f)
            .topK(3)
            .maxNewTokens(100);

        // Compat Generation request
        CompatGenerateRequest compatRequest = new CompatGenerateRequest()
            .inputs("Hi")
            .parameters(params);
        try {
        GenerateResponse compatResponse = api.compatGenerate(compatRequest);
        System.out.println("Generated text" + compatResponse.getGeneratedText());
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitGenerateRequest() {
        TextGenerationInferenceApi api = createApi(false, HF_API_URL);

        GenerateParameters params = new GenerateParameters()
            .seed(123L)
            .temperature(0.7f)
            .topK(3)
            .maxNewTokens(100);

        // Generation request
        GenerateRequest request = new GenerateRequest()
            .inputs("Hi")
            .parameters(params);
        try {
        GenerateResponse response = api.generate(request);
        System.out.println("Generated text" + response.getGeneratedText());
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitStreamGenerateRequest() {
        TextGenerationInferenceApi api = createApi(false, HF_API_URL);

        GenerateParameters params = new GenerateParameters()
            .seed(123L)
            .temperature(0.7f)
            .topK(3)
            .maxNewTokens(100);

        // Generate stream
         GenerateRequest streamRequest = new GenerateRequest()
            .inputs("Hi")
            .parameters(params);
        
        try {
        StreamResponse streamResponse = api.generateStream(streamRequest);
        System.out.println("Generated text" + streamResponse.getGeneratedText());
        } catch(ApiException e) {
            e.printStackTrace();
        }

    }

    public static void submitModelInfo() {
        TextGenerationInferenceApi api = createApi(false, HF_API_URL);
        // Model info
        try {
        Info modelInfoResponse = api.getModelInfo();
        System.out.println("Model info:\n" + modelInfoResponse);
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitApiHealth() {
        TextGenerationInferenceApi api = createApi(false, HF_API_URL);
        // Health request
        try {
        api.health();

        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    public static void submitMetrics() {
        TextGenerationInferenceApi api = createApi(false, HF_API_URL);
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
        submitCompatGenerateRequest();

        submitGenerateRequest();

        submitStreamGenerateRequest();

        submitModelInfo();

        submitApiHealth();

        submitMetrics();
    }
    
}
