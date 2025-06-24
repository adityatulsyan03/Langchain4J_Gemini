package com.example.gemini_chat;

import com.example.gemini_chat.weather_forecast.WeatherForecast;
import com.example.gemini_chat.weather_forecast_tool.WeatherForecastModel;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/qna")
@AllArgsConstructor
public class AIController {

    private final AIService qnAService;

    @PostMapping()
    public ResponseEntity<String> askQuestion(
            @RequestBody Map<String,String> payload
    ){
        String question = payload.get("question");

        String answer = qnAService.getAnswer(question);

        return ResponseEntity.ok(answer);
    }

    @GetMapping("/1")
    public ResponseEntity<String> singleText(){
        return ResponseEntity.ok(qnAService.singleText());
    }

    @GetMapping("/2")
    public ResponseEntity<String> fullConversation(){
        return ResponseEntity.ok(qnAService.fullConversation());
    }

    @GetMapping("/3")
    public ResponseEntity<String> outputFormat(){
        return ResponseEntity.ok(qnAService.outputFormat());
    }

    @GetMapping("/4")
    public ResponseEntity<String> outputFormatWithSchema() {
        return ResponseEntity.ok(qnAService.outputFormatWithSchema());
    }

    @GetMapping("/5")
    public ResponseEntity<String> outputFormatWithSchemaFromRecord() {
        return ResponseEntity.ok(qnAService.outputFormatWithSchemaFromRecord());
    }

    @GetMapping("/6")
    public ResponseEntity<WeatherForecast> extractRequiredOutput() {
        return ResponseEntity.ok(qnAService.extractRequiredOutput());
    }

    @GetMapping("/7")
    public ResponseEntity<String> getCodeSolution(){
        return ResponseEntity.ok(qnAService.solveCode());
    }

    @GetMapping("/8")
    public ResponseEntity<WeatherForecastModel> getOutputUsingTool(){
        return ResponseEntity.ok(qnAService.getOutputUsingTool());
    }

    @PostMapping("/9")
    public ResponseEntity<String> workWithFiles(
            @RequestParam("type") String type,
            @RequestParam("url") String url,
            @RequestParam("prompt") String prompt
    ){
        return ResponseEntity.ok(qnAService.workWithFiles(type,url,prompt));
    }

    @PostMapping(value = "/10", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> getOutputByDocumentUploads(
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file,
            @RequestParam("text") String text,
            @RequestParam("prompt") String prompt
    ) throws IOException {
        return ResponseEntity.ok(qnAService.getOutputByDocumentUploads(type,file,text,prompt));
    }
}
