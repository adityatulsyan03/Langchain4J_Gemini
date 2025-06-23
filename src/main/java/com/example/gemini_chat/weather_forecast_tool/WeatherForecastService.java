package com.example.gemini_chat.weather_forecast_tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

public class WeatherForecastService {
    @Tool("Get the weather forecast for a location")
    public WeatherForecastModel getForecast(
            @P("Location to get the forecast for") String location) {
        if (location.equals("Paris")) {
            return new WeatherForecastModel("Paris", "sunny", 20);
        } else if (location.equals("London")) {
            return new WeatherForecastModel("London", "rainy", 15);
        } else if (location.equals("Tokyo")) {
            return new WeatherForecastModel("Tokyo", "warm", 32);
        } else {
            return new WeatherForecastModel("Unknown", "unknown", 0);
        }
    }
}