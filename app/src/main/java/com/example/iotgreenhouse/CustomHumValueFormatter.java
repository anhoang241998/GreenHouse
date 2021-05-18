package com.example.iotgreenhouse;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.math.BigDecimal;
import java.util.Locale;

public class CustomHumValueFormatter extends ValueFormatter {

    @Override
    public String getPointLabel(Entry entry) {
        float value = entry.getY();
        float number  = BigDecimal.valueOf(value)
                .setScale(2, BigDecimal.ROUND_HALF_DOWN)
                .floatValue();
        return number + "%%";
    }

}
