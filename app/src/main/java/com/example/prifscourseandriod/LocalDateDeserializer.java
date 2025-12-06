package com.example.prifscourseandriod;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {

        if (json == null || json.isJsonNull()) {
            return null;
        }

        if (json.isJsonPrimitive()) {
            try {
                return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                throw new JsonParseException("Invalid LocalDate string: " + json.getAsString(), e);
            }
        }

        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();

            JsonElement yearEl = obj.get("year");
            JsonElement monthEl = obj.get("month");
            JsonElement dayEl = obj.get("day");

            if (yearEl == null || monthEl == null || dayEl == null
                    || yearEl.isJsonNull() || monthEl.isJsonNull() || dayEl.isJsonNull()) {
                return null;
            }

            try {
                int year = yearEl.getAsInt();
                int month = monthEl.getAsInt();
                int day = dayEl.getAsInt();
                return LocalDate.of(year, month, day);
            } catch (Exception e) {
                throw new JsonParseException("Invalid LocalDate object: " + obj, e);
            }
        }

        return null;
    }
}
