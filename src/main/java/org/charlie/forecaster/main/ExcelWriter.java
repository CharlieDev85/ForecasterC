package org.charlie.forecaster.main;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelWriter {

    private Map<Integer, List<String>> data;

    public ExcelWriter (Map<Integer, List<String>> data){
        this.data = data;
    }

    public void write() throws IOException {

        Workbook workbook = new XSSFWorkbook();
        //CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Forecast");

        for(int i = 0; i<data.size(); i++){
            Row row = sheet.createRow(i);
            for(int j = 0; j<data.get(i).size(); j++){
                Cell cell = row.createCell(j);
                if(isDouble(data.get(i).get(j))){
                    cell.setCellValue(Double.valueOf(data.get(i).get(j)));
                }else{
                    cell.setCellValue(data.get(i).get(j));
                }

            }
        }
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        // Closing the workbook
        workbook.close();

    }

    private boolean isDouble(String str){
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
