package com.example.gemini_chat.weather_forecast;

import jdk.jfr.Description;

public record WeatherForecast(
        @Description("minimum temperature")
        Integer minTemperature,
        @Description("maximum temperature")
        Integer maxTemperature,
        @Description("chances of rain")
        boolean rain
) { }
