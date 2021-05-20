package com.example.iotgreenhouse;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

public class CustomTDSValueFormatter extends ValueFormatter {

    @Override
    public String getPointLabel(Entry entry) {
        return String.format(Locale.getDefault(), "%.02f TDS", entry.getY());
    }

}
