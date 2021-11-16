package excelutils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Creator: Lokesh.kk
 * Created on: 2/10/2020
 **/
public class ExcelReader {

    public static Sheet getExcelSheet(String excelPath, String excelSheet) throws IOException {
        File file = new File("C:\\Users\\lokesh.kk\\Desktop\\Flows\\Model9.xlsx");
        FileInputStream fileInputStream = new FileInputStream(new File(excelPath));
        String fileextension = file.getName().substring(file.getName().indexOf("."));
        Sheet sheet=null;
        if (fileextension.equalsIgnoreCase(".xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            sheet = workbook.getSheet(excelSheet);

        } else if (fileextension.equalsIgnoreCase(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheet(excelSheet);
        }
        return sheet;
    }

    public static void main(String[] args) throws Exception {
        String path = "C:\\Users\\lokesh.kk\\Desktop\\Flows\\Model9.xlsx";
        File file = new File("C:\\Users\\lokesh.kk\\Desktop\\Flows\\Model9.xlsx");
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        String fileextension = file.getName().substring(file.getName().indexOf("."));
        Sheet sheet=null;
        Iterator<Row> rows=null;
        if (fileextension.equalsIgnoreCase(".xls")) {
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            sheet = workbook.getSheet("A1,B1 Simulator");
            rows = sheet.iterator();
        } else if (fileextension.equalsIgnoreCase(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheet("A1,B1 Simulator");
            rows = sheet.iterator();
        }
        CellAddress headerAddress=null;
        boolean flag=false;
        while (rows.hasNext()){
            Row row=rows.next();
            Iterator<Cell> cellIterator=row.cellIterator();
            for (Cell cell:row) {
                CellType cellType =cell.getCellType();
                //System.out.println(cell.toString());
                if (cell.toString().contains("Average Amount")){
                    headerAddress=cell.getAddress();
                    flag=true;
                    break;
                }

            }
        }
        if (flag) {
            Row testrow = sheet.getRow(headerAddress.getRow() + 1);
            testrow.getCell(headerAddress.getColumn()).setCellValue(234233);
            Row offer = sheet.getRow(headerAddress.getRow() + 5);
            System.out.println(headerAddress.getColumn());
            System.out.println(offer.getCell(headerAddress.getColumn() + 6).toString());
        }
    }

    public static HashMap readExcelDataInMap(File file, String sheetName, int uniqueKeyColumnNumber) {
        String uniqueKey;
        HashMap entireTestData = new HashMap<>();
        HashMap eachRowTestData;
        String fileextension = file.getName().substring(file.getName().indexOf("."));
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