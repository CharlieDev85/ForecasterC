package org.charlie.forecaster.main;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    private Map<Integer, List<String>> data;
    private String fileLocation;

    public ExcelReader(String fileLocation) throws IOException {
        this.fileLocation = fileLocation;
        this.processFile();
    }

    public Map<Integer, List<String>> getData() {
        return data;
    }
    public void setData(Map<Integer, List<String>> data) {
        this.data = data;
    }

    private void processFile() throws IOException {
        FileInputStream file = new FileInputStream(new File(fileLocation));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());
            for (Cell cell : row) {
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        data.get(new Integer(i)).add(cell.getRichStringCellValue().getString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(cell.getDateCellValue() + "");
                        } else {
                            data.get(i).add(cell.getNumericCellValue() + "");
                        }
                        break;
                    case BOOLEAN:
                        data.get(i).add(cell.getBooleanCellValue() + "");
                        break;
                    case FORMULA:
                        data.get(i).add(cell.getCellFormula() + "");
                        break;
                    case BLANK:
                        data.get(i).add("");
                        break;
                    default: data.get(new Integer(i)).add(" ");
                }
            }
            i++;
        }
        data = Cleaner.clean(data);
        this.setData(data);
    }


}
