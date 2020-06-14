package org.charlie.forecaster.main;

import java.util.Map;

public class SeasonIrregularity {
    private String period;
    private Double value;


    public SeasonIrregularity(String period, Double value){
        this.period = period;
        this.value = value;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
