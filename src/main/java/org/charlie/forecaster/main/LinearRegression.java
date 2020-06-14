package org.charlie.forecaster.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.util.Arrays.asList;

public class LinearRegression {

    /*
    private  List<Integer> x = asList(2, 3, 5, 7, 9, 11, 14); // Consecutive hours developer codes
    private  List<Double> y = asList(4d, 5d, 7d, 10d, 15d, 20d, 40d); // Number of bugs produced
     */
    private  List<Integer> x;
    private  List<Double> y;

    public List<Integer> getX() {
        return x;
    }

    public void setX(List<Integer> x) {
        this.x = x;
    }

    public List<Double> getY() {
        return y;
    }

    public void setY(List<Double> y) {
        this.y = y;
    }

    public  Double predictForValue(int predictForDependentVariable) {
        if (x.size() != y.size())
            throw new IllegalStateException("Must have equal X and Y data points");

        Integer numberOfDataValues = x.size();

        List<Double> xSquared = x
                .stream()
                .map(position -> Math.pow(position, 2))
                .collect(Collectors.toList());

        /*List<Integer> xMultipliedByY = IntStream.range(0, numberOfDataValues)
                .map(i -> x.get(i) * y.get(i))
                .boxed()
                .collect(Collectors.toList());*/
        //previous block to work with Double
        //Charlie:
        List<Double> xMultipliedByY = new ArrayList<>();
        for(int i = 0; i<x.size(); i++){
            Double num = x.get(i) * y.get(i);
            xMultipliedByY.add(num);
        }

        Integer xSummed = x
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        Double ySummed = y
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        Double sumOfXSquared = xSquared
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        Double sumOfXMultipliedByY = xMultipliedByY
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        Double slopeNominator = numberOfDataValues * sumOfXMultipliedByY - ySummed * xSummed;
        Double slopeDenominator = numberOfDataValues * sumOfXSquared - Math.pow(xSummed, 2);
        Double slope = slopeNominator / slopeDenominator;

        double interceptNominator = ySummed - slope * xSummed;
        double interceptDenominator = numberOfDataValues;
        Double intercept = interceptNominator / interceptDenominator;

        return (slope * predictForDependentVariable) + intercept;
    }
/*
    public static void main(String[] args) {
        System.out.println(new LinearRegression().predictForValue(13));
    }*/

}
