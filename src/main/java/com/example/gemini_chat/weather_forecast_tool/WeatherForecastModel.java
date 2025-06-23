package com.example.gemini_chat.weather_forecast_tool;

public record WeatherForecastModel(
        String location,
        String forecast,
        int temperature) {}
