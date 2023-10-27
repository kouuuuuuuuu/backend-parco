package com.project.Eparking.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConfig extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String formattedDate = sdf.format(date);
        generator.writeString(formattedDate);
    }
}
