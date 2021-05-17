package com.example.iotgreenhouse;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class CustomValueFormatter extends ValueFormatter {

    private final DecimalFormat mFormat = new DecimalFormat("###,##0.0");

    @Override
    public String getPointLabel(Entry entry) {
        return mFormat.format(entry.getY());
    }

}
