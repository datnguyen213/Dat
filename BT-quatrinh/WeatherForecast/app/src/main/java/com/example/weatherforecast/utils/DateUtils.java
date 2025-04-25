package com.example.weatherforecast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatToVietnameseDay(String dtTxt) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd/MM", new Locale("vi", "VN"));

        try {
            Date date = inputFormat.parse(dtTxt);
            String formatted = outputFormat.format(date);

            // Chuyển chữ cái đầu của "thứ" thành hoa (Thứ hai -> Thứ Hai)
            return capitalizeFirstLetter(formatted);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}