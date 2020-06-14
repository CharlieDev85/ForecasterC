package org.charlie.forecaster.main;

import java.util.*;
/*
* {0=[Year, Quarter, sales],
* 1=[1.0, 1.0, 4.8],
* 2=[1.0, 2.0, 4.1],
* 3=[1.0, 3.0, 6.0],
* 4=[1.0, 4.0, 6.5],
* 5=[2.0, 1.0, 5.8],
* 6=[2.0, 2.0, 5.2],
* 7=[2.0, 3.0, 6.8],
* 8=[2.0, 4.0, 7.4],
* 9=[3.0, 1.0, 6.0],
* 10=[3.0, 2.0, 5.6],
* 11=[3.0, 3.0, 7.5],
* 12=[3.0, 4.0, 7.8],
* 13=[4.0, 1.0, 6.3],
* 14=[4.0, 2.0, 5.9],
* 15=[4.0, 3.0, 8.0],
* 16=[4.0, 4.0, 8.4],
* 17=[5.0, 1.0],
* 18=[5.0, 2.0],
* 19=[5.0, 3.0],
* 20=[5.0, 4.0]}
* */

public class ForecasterC {


    ArrayList<Integer> reducedKeys;
    ArrayList<Integer> historicalKeys;
    ArrayList<Integer> forecastKeys;
    Map<Integer, String> forecastPeriods;
    int periodsPerCycle;
    Map<String, Double> st;
    Map<String, Double> slr;
    ArrayList<SeasonIrregularity>stItByPeriod;
    ArrayList<Double> des;
    Map<Integer, List<String>> reducedData;
    ArrayList<Double> cma;
    Map<Integer, List<String>> newData;



    public Map<Integer, List<String>> getForecast(Map<Integer, List<String>> data) {
        //initial variables
        this.periodsPerCycle = getPeriodsPerCycle(data);
        this.historicalKeys = getHistoricalKeys(data);
        this.forecastKeys = getForecastKeys(data, historicalKeys);
        this.reducedKeys = getReducedKeys(data, periodsPerCycle);
        this.forecastPeriods = getForecastPeriods(forecastKeys,data);

        Map<String, String> forecast;
        Map<Integer, List<String>> newData;
        reducedData = getReducedData(data);
        cma = getCma(data);
        stItByPeriod = getStItByPeriod(reducedData, cma);
        st = getStComponent(stItByPeriod);
        des = getDes(st, data);
        slr = getSLR(des);

        Map<String, Double> finalNumbers = getFinalNumbers(slr);
        forecast = getFinalResult(finalNumbers);
        newData = getNewData(data, forecast);
        return newData;
    }

    private Map<Integer, List<String>> getNewData(Map<Integer, List<String>> data, Map<String, String> forecast) {
        Map<Integer, List<String>> newData = new HashMap<>();
        //headers
        newData.put(0, data.get(0));
        for(Integer historicalKey : historicalKeys){
            newData.put(historicalKey, data.get(historicalKey));
        }
        for(Integer forecastKey : forecastKeys){
            List<String> info = new ArrayList<>();
            for(String value : data.get(forecastKey)){
                info.add(value);

            }
            String newInfo = forecast.get(data.get(forecastKey).get(data.get(forecastKey).size()-1));
            info.add(newInfo);
            newData.put(forecastKey, info);
        }
        return newData;
    }

    private Map<Integer, String> getForecastPeriods(ArrayList<Integer> forecastKeys, Map<Integer, List<String>> data) {
        Map<Integer, String> periods = new HashMap<>();
        int i = 1;
        for(Integer key: forecastKeys){
            periods.put(i, data.get(key).get(1));
            i++;
        }
        return periods;
    }

    private ArrayList<Integer> getForecastKeys(Map<Integer, List<String>> data, ArrayList<Integer> historicalKeys) {
        ArrayList<Integer> forecastKeys = new ArrayList<>();
        int firstForecastKey = historicalKeys.size()+1;
        forecastKeys.add(firstForecastKey);
        for(int i = firstForecastKey+1; i < data.size(); i++){
            if(data.get(i).size() == data.get(i-1).size()){
                forecastKeys.add(new Integer(i));
            } else {break;}
        }
        return forecastKeys;
    }


    private ArrayList<Integer> getHistoricalKeys(Map<Integer, List<String>> data) {
        ArrayList<Integer> historicalKeys = new ArrayList<>();
        historicalKeys.add(1);
        for(int i = 2; i < data.size(); i++){
            if(data.get(i).size() == data.get(i-1).size()){
                historicalKeys.add(new Integer(i));
            } else {break;}
        }
        return historicalKeys;
    }

    private ArrayList<Integer> getReducedKeys(Map<Integer, List<String>> data, int periodsPerCycle) {
        ArrayList<Integer> reducedKeys =  new ArrayList<>();
        int firstKey = periodsPerCycle%2==0 ? (periodsPerCycle/2)+1 : (periodsPerCycle + 1)/2;
        reducedKeys.add(firstKey);
        for(int i = firstKey + 1; i <= historicalKeys.size()-firstKey + 1; i++){
                reducedKeys.add(i);
        }
        return reducedKeys;
    }

    public ArrayList<Double> getCma (Map<Integer, List<String>> data){
        ArrayList<Double> historicalData = getHistoricalData(data);
        ArrayList<Double> cma = getSimpleCMA(historicalData, periodsPerCycle);
        if(periodsPerCycle % 2 == 0){
            cma = getSimpleCMA(cma,2);
        }
        return cma;
    }


    private ArrayList<Double> getHistoricalData(Map<Integer, List<String>> data) {
        ArrayList<Double> historicalData = new ArrayList<>();
        for(Integer key: historicalKeys){
            historicalData.add(new Double(data.get(key).get(2)));
        }
        return historicalData;
    }

    private ArrayList<Double> getSimpleCMA(ArrayList<Double> historicalData, int periodsPerCycle) {
        ArrayList<Double> ma = new ArrayList<>();
        int limit = historicalData.size()-1;
        int maxIndex = periodsPerCycle-1;
        int counter = 0;
        while(maxIndex <= limit){
            ArrayList<Double> numbers = new ArrayList<>();
            for(int i = counter; i <= maxIndex ; i++){
                numbers.add(historicalData.get(i));
            }
            Double avg = getAvg(numbers);
            ma.add(avg);
            counter = counter + 1;
            maxIndex = maxIndex + 1;
        }
        return ma;
    }

    private int getPeriodsPerCycle(Map<Integer, List<String>> data) {
        int periodsPerCycle = 1;
        String cycle = data.get(1).get(0);
        for(int i=2; i<data.size(); i++ ){
            if(data.get(i).get(0).equals(cycle)){
                periodsPerCycle = periodsPerCycle +1;
            }else{
                break;
            }
        }
        return periodsPerCycle;
    }

    public ArrayList<SeasonIrregularity> getStItByPeriod(Map<Integer, List<String>> reducedData, ArrayList<Double> cma){
        ArrayList<SeasonIrregularity>  stItByPeriod = new ArrayList<>();
        if(reducedData.size() == cma.size()){
            int i = 0;
            for( Integer key : reducedKeys){
                    Double a = Double.valueOf(reducedData.get(key).get(2));
                    Double b = cma.get(i);
                    SeasonIrregularity stIt = new SeasonIrregularity(reducedData.get(key).get(1), a/b);
                    stItByPeriod.add(stIt);
                    i++;
            }

        } else {
            System.out.println("ERROR: getStItByPeriod-> sizes are not the same! XD");
            //Sizes for reduced data and cma should be the same
            //reduced data represents the data that will be operated to get the St,It components.
        }
        return stItByPeriod;
    }

    public Map<Integer, List<String>> getReducedData(Map<Integer, List<String>> data){
        Map<Integer, List<String>> reducedData = new HashMap<>();
        for(Integer key : reducedKeys){
            //System.out.println(data.get(key));
            reducedData.put(key, data.get(key));
        }
        return reducedData;
    }

    public ArrayList<Double>  getDes(Map<String, Double> st, Map<Integer, List<String>> data){
        ArrayList<Double> des = new ArrayList<>();
        for(Integer key : historicalKeys){
            Double historicalNum = Double.valueOf(data.get(key).get(2));
            Double stNum = st.get(data.get(key).get(1));
            Double desNum = Double.valueOf(historicalNum / stNum);
            des.add(desNum);
        }
        return des;
    }

    public Map<String, Double> getSLR(ArrayList<Double> des){
        Map<String, Double> slr = new HashMap<>();
        LinearRegression lr = new LinearRegression();
        lr.setX(historicalKeys);
        lr.setY(des);
        int i = 1;
        for(Integer x: forecastKeys){
            Double num = lr.predictForValue(x);
            slr.put(forecastPeriods.get(i), num);
            i++;
        }
        return slr;
    }

    public Map<String, Double> getStComponent(ArrayList<SeasonIrregularity> stItByMonth){
        Map<String, Double> st =  new HashMap<>();

        for(int i = 0; i<periodsPerCycle; i++){
            String period = stItByPeriod.get(i).getPeriod();
            ArrayList<Double> numbers = new ArrayList<>();

            for(SeasonIrregularity si : stItByMonth){
                if(si.getPeriod().equals(period)){
                    numbers.add(si.getValue());
                }
            }
            Double avg = getAvg(numbers);
            st.put(period, avg);
        }

        return st;
    }

    public Map<String, Double>getFinalNumbers(Map<String, Double> slr){
        Map<String, Double> finalNumbers = new HashMap<>();

        for(String key : st.keySet()){
             Double num = st.get(key) * slr.get(key);
             //finalNumbers.add(num);
            finalNumbers.put(key, num);
        }
        return finalNumbers;
    }

    public Map<String, String> getFinalResult(Map<String, Double> finalNumbers){
        Map<String, String> finalForecast = new HashMap<>();
        for(String key : finalNumbers.keySet()){
            finalForecast.put(key, finalNumbers.get(key).toString());
        }
        return finalForecast;
    }

    private Double getAvg(ArrayList<Double> numbers){
        double total=0;
        int len = numbers.size();
        for(int i=0; i<len; i++){
            total = total + numbers.get(i);
        }
        double avg = total / len;
        return avg;
    }
}
