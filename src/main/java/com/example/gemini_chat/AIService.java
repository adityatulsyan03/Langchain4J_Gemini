package com.example.gemini_chat;

import com.example.gemini_chat.trip_itineary.TripItinerary;
import com.example.gemini_chat.weather_forecast.WeatherForecast;
import com.example.gemini_chat.weather_forecast.WeatherForecastAssistant;
import com.example.gemini_chat.weather_forecast_tool.WeatherForecastModel;
import com.example.gemini_chat.weather_forecast_tool.WeatherForecastService;
import com.example.gemini_chat.weather_forecast_tool.WeatherForecastToolAssistant;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.model.chat.request.json.JsonIntegerSchema.JSON_INTEGER_SCHEMA;
import static dev.langchain4j.model.chat.request.json.JsonStringSchema.JSON_STRING_SCHEMA;
import static dev.langchain4j.service.output.JsonSchemas.jsonSchemaFrom;

@Service
public class AIService {
    
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.model}")
    private String geminiApiModel;

    @Value("${gemini.api.embedding.model}")
    private String geminiApiEmbeddingModel;

    private final WebClient webClient;

    public AIService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    //With WebFlux Using Gemini URL
    public String getAnswer(String question) {

        Map<String, Object> requestBody = Map.of("contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", question),})});

        String answer = webClient.post().uri(geminiApiUrl + geminiApiKey).header("Content-Type", "application/json").bodyValue(requestBody).retrieve().bodyToMono(String.class).block();

        return answer;
    }

    //Most Simple
    public String singleText(){
        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .build();

        String response = gemini.generate("Hii");

        System.out.println("Gemini> " + response);
        return response;
    }

    //Using conversation
    public String fullConversation(){
        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .build();

        ChatResponse chatResponse = gemini.chat(ChatRequest.builder()
                .messages(
                        SystemMessage.from("SDE"),
                        UserMessage.from("Hi"),
                        AiMessage.from("Hello"),
                        UserMessage.from("How many R's are there in the word 'strawberry'?")
                )
                .build());

        String response = chatResponse.aiMessage().text();

        System.out.println("Gemini> " + response);
        return "Gemini> " + response;
    }

    //Get Output in a particular format
    public String outputFormat(){
        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .responseMimeType("application/json")
                .build();

        ChatResponse chatResponse = gemini.chat(ChatRequest.builder()
                .messages(
                        UserMessage.from("Hii")
                )
                .build());

        String response = chatResponse.aiMessage().text();

        System.out.println("Gemini> " + response);
        return response;
    }

    //Custom Json
    public String outputFormatWithSchema() {
        JsonSchema schema = JsonSchema.builder()
                .rootElement(JsonObjectSchema.builder()
                        .properties(
                                Map.of(
                                        "title", JSON_STRING_SCHEMA,
                                    "ingredients", JsonArraySchema.builder()
                                            .items(JSON_STRING_SCHEMA)
                                            .build(),
                                    "preparationTime", JSON_INTEGER_SCHEMA,
                                    "steps", JsonArraySchema.builder()
                                            .items(JSON_STRING_SCHEMA)
                                            .build()
                                )
                        )
                        .required(List.of("title", "ingredients", "preparationTime", "steps"))
                        .build())
                .build();

        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .responseMimeType("application/json")
                .responseSchema(schema)
                .build();

        ChatResponse chatResponse = gemini.chat(ChatRequest.builder()
                .messages(
                        UserMessage.from("Give a recipe based on cheese.")
                )
                .build());

        String response = chatResponse.aiMessage().text();

        System.out.println("Gemini> " + response);
        return response;
    }

    //Custom Json using a record type1
    public String outputFormatWithSchemaFromRecord() {
        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .temperature(2.0)
                .responseSchema(jsonSchemaFrom(TripItinerary.class).get())
                .build();

        Response<AiMessage> tripResponse = gemini.generate(
                SystemMessage.from("You are an expert trip planner"),
                UserMessage.from("""
                        Suggest an itinerary for Japan.
                        Cities visited: Tokyo, Kyoto, Osaka.
                        Trip for a family of 4 persons.
                        Provide key highlights for each city visited.
                        """)
        );

        String response = tripResponse.content().text();

        System.out.println("Gemini> " + response);
        return response;
    }

    //Custom Json using record type2
    public WeatherForecast extractRequiredOutput(){

        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .build();

        WeatherForecastAssistant forecastAssistant = AiServices.builder(WeatherForecastAssistant.class)
                .chatLanguageModel(gemini)
                .build();

        WeatherForecast forecast = forecastAssistant.extract("""
            Morning: The day dawns bright and clear in Osaka, with crisp autumn air and sunny skies. Expect temperatures to hover around 18°C (64°F) as you head out for your morning stroll through Namba.
            Afternoon: The sun continues to shine as the city buzzes with activity. Temperatures climb to a comfortable 22°C (72°F).
            Enjoy a leisurely lunch at one of Osaka's many outdoor cafes, or take a boat ride on the Okawa River to soak in the beautiful scenery.
            Evening: As the day fades, expect clear skies and a slight chill in the air. Temperatures drop to 15°C (59°F). A cozy dinner at a traditional Izakaya will be the perfect way to end your day in Osaka.
            Overall: A beautiful autumn day in Osaka awaits, perfect for exploring the city's vibrant streets, enjoying the local cuisine, and soaking in the sights.
            Don't forget: Pack a light jacket for the evening and wear comfortable shoes for all the walking you'll be doing.
            """);

        System.out.println("Gemini> " + forecast);

        return forecast;
    }

    public String solveCode(){
        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .allowCodeExecution(true)
                .includeCodeExecutionOutput(true)
                .build();

        Response<AiMessage> mathQuizz = gemini.generate(
                SystemMessage.from("""
                    You are an expert mathematician.
                    When asked a math problem or logic problem,
                    you can solve it by creating a Python program,
                    and execute it also.
                    """),
                UserMessage.from("""
                    Implement the Fibonacci and Ackermann functions.
                    What is the result of `fibonacci(22)` - ackermann(3, 4)?
                    Also give me its final result after running the code.
                    """)
        );

        String response = mathQuizz.content().text();

        System.out.println("Gemini> " + response);
        return response;

    }

    //Get Output from a service
    public WeatherForecastModel getOutputUsingTool(){

        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .temperature(0.0)
                .logRequestsAndResponses(true)
                .build();

        WeatherForecastService weatherForecastService = new WeatherForecastService();

        WeatherForecastToolAssistant assistant = AiServices.builder(WeatherForecastToolAssistant.class)
                .chatLanguageModel(gemini)
                .tools(weatherForecastService)
                .build();

        WeatherForecastModel response = assistant.chat("What is the weather forecast for Tokyo?");

        System.out.println("Gemini> " + response);
        return response;
    }

    public String workWithFiles(String type,String url,String prompt){

        Base64.Encoder b64encoder = Base64.getEncoder();

        String base64Encoded = b64encoder.encodeToString(readBytes(url));

        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .build();

        Response<AiMessage> response = switch (type.toLowerCase()) {
            case "text" -> gemini.generate(
                        UserMessage.from(
                                TextFileContent.from(base64Encoded,"text/x-markdown"),
                                TextContent.from(prompt)
                        )
                );
            case "image" -> gemini.generate(
                    UserMessage.from(
                            ImageContent.from(base64Encoded, "image/png"),
                            TextContent.from(prompt)
                    )
            );
            default -> throw new IllegalStateException("Unexpected value: " + type.toLowerCase());
        };

        System.out.println("Gemini> " + response);
        return response.content().text();
    }


    private byte[] readBytes(String pathOrUrl) {
        try {
            if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
                try (InputStream in = new URL(pathOrUrl).openStream()) {
                    return in.readAllBytes();
                }
            } else {
                try (InputStream in = new FileInputStream(pathOrUrl)) {
                    return in.readAllBytes();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read bytes from: " + pathOrUrl, e);
        }
    }

    public String getOutputByDocumentUploads(String type, MultipartFile file,String text, String prompt) throws IOException {
        byte[] fileBytes = file.getBytes();
        String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);

        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiApiKey)
                .modelName(geminiApiModel)
                .logRequestsAndResponses(true)
                .build();

        Response<AiMessage> response = switch (type.toLowerCase()) {
            case "text" -> {
                String inputText = "This is the text " + text + prompt;
                yield gemini.generate(
                    UserMessage.from(
                            inputText
                    )
                );
            }
            case "image" -> gemini.generate(
                    UserMessage.from(
                            ImageContent.from(base64Encoded, "image/png"),
                            TextContent.from(prompt)
                    )
            );
            case "pdf" -> gemini.generate(
                    UserMessage.from(
                            PdfFileContent.from(base64Encoded, "application/pdf"),
                            TextContent.from(prompt)
                    )
            );
            default -> throw new IllegalStateException("Unexpected value: " + type.toLowerCase());
        };

        return response.content().text();
    }
}