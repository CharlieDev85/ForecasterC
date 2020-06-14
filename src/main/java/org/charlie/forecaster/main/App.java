package org.charlie.forecaster.main;

import com.sun.media.sound.InvalidFormatException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String args[]) throws IOException, InvalidFormatException {
        String fileLocation = "./Book1.xlsx";
        ExcelReader handler = new ExcelReader(fileLocation);
        Map<Integer, List<String>> data = handler.getData();
        System.out.println(data);
        /*
        ArrayList<String> shouldBeResult = new ArrayList<>();
        shouldBeResult.add("7.085626244259736");
        shouldBeResult.add("6.491047938335678");
        shouldBeResult.add("8.632257740571097");
        shouldBeResult.add("9.194899457624615");
        */
        ForecasterC forecasterC = new ForecasterC();
        Map<Integer, List<String>> forecast = forecasterC.getForecast(data);
        /*
        if(forecast.equals(shouldBeResult)){
            System.out.println("yeah!");
        } else{
            System.out.println("not yet :(");
        }
        */
        //System.out.println(forecast);
        ExcelWriter writer = new ExcelWriter(forecast);
        writer.write();
    }
}
