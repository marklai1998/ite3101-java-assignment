package hk.edu.vtc.it3101.mcmarking;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This is a helper class that help you to read excel and file related process.<br>
 * YOU DON'T NEED TO UNDERSTAND THIS, AND ALSO DON'T MODIFY IT!<br>
 */
public class ExcelHelper {
    /**
     * Read the first sheet of excel specified at excelFilePathName,<br>
     * and return 2D string array that represent the first sheet.<br>
     * <br>
     *
     * @param excelFilePathName Excel File path name.
     * @return 2D string array that represent the first sheet.
     */
    public static String[][] getExcelTo2DStringArray(String excelFilePathName) {
        XSSFRow row;
        XSSFCell cell;
        String[][] value = null;

        try {
            FileInputStream inputStream = new FileInputStream(excelFilePathName);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            //only handle the first sheet!
            int cn = 0;

            // get 0th sheet data
            XSSFSheet sheet = workbook.getSheetAt(cn);

            // get number of rows from sheet
            int rows = 75;

            // get number of cell from row
            int cells = 26;

            value = new String[rows][cells];

            for (int r = 0; r < rows; r++) {
                row = sheet.getRow(r); // bring row
                if (row != null) {
                    for (int c = 0; c < cells; c++) {
                        cell = row.getCell(c);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case XSSFCell.CELL_TYPE_FORMULA:
                                    value[r][c] = cell.getCellFormula();
                                    break;
                                case XSSFCell.CELL_TYPE_NUMERIC:
                                    value[r][c] = "" + cell.getNumericCellValue();
                                    break;
                                case XSSFCell.CELL_TYPE_STRING:
                                    value[r][c] = "" + cell.getStringCellValue();
                                    break;
                                case XSSFCell.CELL_TYPE_BLANK:
                                    value[r][c] = "";
                                    break;
                                case XSSFCell.CELL_TYPE_ERROR:
                                    value[r][c] = "" + cell.getErrorCellValue();
                                    break;
                                default:
                            }
                        }
                    } // for(c)
                }
            } // for(r)
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Input the fold path and return the string array, which contains all files,<br>
     * except for the "Answer.xlsx".<br>
     *
     * @param folderPath Folder that contains all excel files.
     * @return string array of excel files.
     */
    public static String[] getStudentAnswerPaths(String folderPath) {
        String[] excelList = new String[0];
        try {
            excelList = Files.list(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .filter(f -> !f.getFileName().endsWith("Answer.xlsx"))
                    .filter(f -> f.toAbsolutePath().toString().toLowerCase().endsWith(".xlsx"))
                    .map(a -> a.toAbsolutePath().toString())
                    .toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return excelList;
    }


}
