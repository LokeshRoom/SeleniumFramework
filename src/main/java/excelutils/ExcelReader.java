package excelutils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ExcelReader {

    public static HashMap readExcelDataInMap(File file, String sheetName, int uniqueKeyColumnNumber) {
        String uniqueKey;
        HashMap entireTestData = new HashMap<>();
        HashMap eachRowTestData;
        String fileextension = file.getName().substring(file.getName().lastIndexOf("."));
        List headers = new ArrayList<>();
        Sheet excelSheet = null;


        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (fileextension.equalsIgnoreCase(".xls")) {
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                excelSheet = workbook.getSheet(sheetName);

            } else if (fileextension.equalsIgnoreCase(".xlsx")) {
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                excelSheet = workbook.getSheet(sheetName);
            }


            int rowCount = excelSheet.getLastRowNum();
            Row headerRow = excelSheet.getRow(0);

            for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                headers.add(j, readExcelCellData(excelSheet, 0, j));
            }
            for (int i = 1; i <= rowCount; i++) {
                eachRowTestData = new HashMap<>();
                Row row = excelSheet.getRow(i);
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    eachRowTestData.put(headers.get(j), readExcelCellData(excelSheet, i, j));
                }
                HashMap tempMap = eachRowTestData;
                uniqueKey = readExcelCellData(excelSheet, i, uniqueKeyColumnNumber);
                entireTestData.put(uniqueKey, tempMap);
            }

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException" + e);
            return null;
        } catch (IOException e) {
            System.out.println("IOException is" + e);
            return null;
        } catch (Exception e) {
            System.out.println("Exception.." + e);
            return null;
        }
        return entireTestData;
    }

    public static String readExcelCellData(Sheet excelWSheet, int rowNum, int colNum) {
        String strCellValue = "";
        try {
            DataFormatter formatter = new DataFormatter();
            Cell cell = excelWSheet.getRow(rowNum).getCell(colNum);
            strCellValue = formatter.formatCellValue(cell);
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
        return strCellValue;
    }
}