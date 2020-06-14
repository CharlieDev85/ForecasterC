package org.charlie.forecaster.main;

import java.util.*;

public class Cleaner {
    public static Map<Integer, List<String>> clean(Map<Integer, List<String>> data){

        Map<Integer, List<String>> cleaned = new HashMap<>();
        for(Integer t : data.keySet()){
            List<String> values = data.get(t);
            values.removeAll(Arrays.asList("", null));
            cleaned.put(t, values);
        }
        return cleaned;
    }
}
