package com.example.gemini_chat.trip_itineary;

import jdk.jfr.Description;

import java.util.List;

@Description("details of a trip itinerary")
public record TripItinerary(
        String country,
        Integer numberOfPersons,
        Month month,
        @Description("key highlights when visiting the city")
        List<CityHighlights> cityHighlights
) {
    enum Month {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY,
        AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    }

    record CityHighlights(
            String cityName,
            List<String> visitHighlights
    ) { }
}
