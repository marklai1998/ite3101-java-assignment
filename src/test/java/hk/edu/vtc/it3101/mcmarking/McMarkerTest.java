package hk.edu.vtc.it3101.mcmarking;

import org.junit.*;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;


public class McMarkerTest {
    private String[][] answerExcel;
    private String answerPath;
    private String[] excelList;
    private Path testDataFolder;

    private String resourceTestSet = "test1/";
    private String[] answersFileName = {"1.xlsx", "2.xlsx", "3.xlsx", "4.xlsx"};

    private String print2DStringArrayTestData = "print2DStringArray.txt";
    private String[][] studentExcel;

    private String[] studentIds = new String[4];
    private int[][] studentAnswers = new int[4][];

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();
    @Rule
    public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

    @Before
    public void setUp() throws Exception {
        String fileName = "test1/Answer.xlsx";
        testDataFolder = Files.createTempDirectory("tempfiles");

        answerPath = ExportResource(fileName);
        System.out.println(answerPath);
        for (String s : answersFileName)
            ExportResource(resourceTestSet + s);

        ExportResource(resourceTestSet + print2DStringArrayTestData);

        answerExcel = ExcelHelper.getExcelTo2DStringArray(answerPath);
        excelList = ExcelHelper.getStudentAnswerPaths(testDataFolder.toString());

        studentExcel = ExcelHelper.getExcelTo2DStringArray(excelList[0]);
    }

    @After
    public void tearDown() throws Exception {
        for (File file : testDataFolder.toFile().listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    @Test
    public void readFolderPath() throws Exception {
        systemOutRule.clearLog();
        systemInMock.provideLines(testDataFolder.toString());
        String returnValue = McMarker.readFolderPath();
        String expect = "Please input assignment folder path:\r\n";  //Println appends \r\n
        String actual = systemOutRule.getLog();
        Assert.assertEquals(expect, actual);
        Assert.assertEquals(testDataFolder.toString(), returnValue);
    }

    @Test
    public void printRow() throws Exception {
        String[] row = {"", "", "", "", "", "", "", "A", "", "B", "", "C", "", "D", "", "E", "", ""};
        String expect = "\t\t\t\t\t\t\tA\t\tB\t\tC\t\tD\t\tE\t\t\t";
        systemOutRule.clearLog();
        McMarker.printRow(row);
        String actual = systemOutRule.getLog();
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void print2DStringArray() throws Exception {
        systemOutRule.clearLog();
        McMarker.print2DStringArray(answerExcel);
        String actual = systemOutRule.getLog();
        String expect = new String(Files.readAllBytes(Paths.get(testDataFolder.toString(), print2DStringArrayTestData)));
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void getWrongAnswers() throws Exception {
        McMarker.populateStudentAnswers(excelList, studentIds, studentAnswers);
        int[] answers = McMarker.getAllAnswers(answerExcel);
        char[][] studentWrongAnswer = McMarker.getWrongAnswers(answers, studentAnswers);
        String actual = Arrays.deepToString(studentWrongAnswer);
        String expect = "[[ ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ], " +
                "[ ,  , A,  , C,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ], " +
                "[B,  , A, C, B,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ], " +
                "[ , A, A, A, A,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ,  ]]";
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void getTotalMarks() throws Exception {
        McMarker.populateStudentAnswers(excelList, studentIds, studentAnswers);
        int[] answers = McMarker.getAllAnswers(answerExcel);
        int[] actual = McMarker.getTotalMarks(answers, studentAnswers);
        int[] expect = {5, 3, 1, 1};
        Assert.assertArrayEquals(expect, actual);
    }

    @Test
    public void printTotalMarkReport() throws Exception {
        McMarker.populateStudentAnswers(excelList, studentIds, studentAnswers);
        int[] answers = McMarker.getAllAnswers(answerExcel);
        int[] totalMarks = McMarker.getTotalMarks(answers, studentAnswers);
        systemOutRule.clearLog();
        McMarker.printTotalMarkReport(studentIds, totalMarks);
        String actual = systemOutRule.getLog();
        String expect = "ID\t\t\tMarks\r\n" +
                "063365663\t5\r\n" +
                "060326349\t3\r\n" +
                "063022886\t1\r\n" +
                "060363853\t1\r\n";
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void printFirstRow() throws Exception {
        McMarker.populateStudentAnswers(excelList, studentIds, studentAnswers);
        systemOutRule.clearLog();
        McMarker.printFirstRow(studentAnswers[0].length + 1);
        String actual = systemOutRule.getLog();
        String expect = "ID\t\t\tq1\tq2\tq3\tq4\tq5\tq6\tq7\tq8\tq9\tq10\tq11\tq12\tq13\tq14\tq15\tq16\tq17\tq18\tq19\tq20\tq21\tq22\tq23\tq24\tq25\tq26\tq27\tq28\tq29\tq30\tq31\tq32\tq33\tq34\tq35\tq36\tq37\tq38\tq39\tq40\tq41\t\r\n";
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void printAnswerReport() throws Exception {
        McMarker.populateStudentAnswers(excelList, studentIds, studentAnswers);
        systemOutRule.clearLog();
        McMarker.printAnswerReport(studentIds, studentAnswers);
        String actual = systemOutRule.getLog();
        String expect = "ID\t\t\tq1\tq2\tq3\tq4\tq5\tq6\tq7\tq8\tq9\tq10\tq11\tq12\tq13\tq14\tq15\tq16\tq17\tq18\tq19\tq20\tq21\tq22\tq23\tq24\tq25\tq26\tq27\tq28\tq29\tq30\tq31\tq32\tq33\tq34\tq35\tq36\tq37\tq38\tq39\tq40\tq41\t\r\n" +
                "063365663\t0\t1\t2\t3\t4\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t\r\n" +
                "060326349\t0\t1\t0\t3\t2\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t\r\n" +
                "063022886\t1\t1\t0\t2\t1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t\r\n" +
                "060363853\t0\t0\t0\t0\t0\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t\r\n";
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void printWrongAnswerReport() throws Exception {
        McMarker.populateStudentAnswers(excelList, studentIds, studentAnswers);
        int[] answers = McMarker.getAllAnswers(answerExcel);
        char[][] studentWrongAnswer = McMarker.getWrongAnswers(answers, studentAnswers);
        systemOutRule.clearLog();
        McMarker.printWrongAnswerReport(studentIds, studentWrongAnswer);
        String actual = systemOutRule.getLog();
        String expect = "ID\t\t\tq1\tq2\tq3\tq4\tq5\tq6\tq7\tq8\tq9\tq10\tq11\tq12\tq13\tq14\tq15\tq16\tq17\tq18\tq19\tq20\tq21\tq22\tq23\tq24\tq25\tq26\tq27\tq28\tq29\tq30\tq31\tq32\tq33\tq34\tq35\tq36\tq37\tq38\tq39\tq40\tq41\t\r\n" +
                "063365663\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t\r\n" +
                "060326349\t \t \tA\t \tC\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t\r\n" +
                "063022886\tB\t \tA\tC\tB\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t\r\n" +
                "060363853\t \tA\tA\tA\tA\t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t \t\r\n";
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void populateStudentAnswers() throws Exception {
        McMarker.populateStudentAnswers(excelList, studentIds, studentAnswers);
        System.out.println(Arrays.toString(studentIds));
        String[] expectStudentId = {"063365663", "060326349", "063022886", "060363853"};
        Assert.assertArrayEquals(expectStudentId, studentIds);
        String expectedStudentAnswers = "[[0, 1, 2, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1], [0, 1, 0, 3, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1], [1, 1, 0, 2, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1], [0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1]]";
        Assert.assertEquals(expectedStudentAnswers, Arrays.deepToString(studentAnswers));
    }

    @Test
    public void getStudentId() throws Exception {
        String actual = McMarker.getStudentId(studentExcel);
        String expect = "063365663";
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void getDigit() throws Exception {
        Assert.assertEquals(0, McMarker.getDigit(studentExcel[13]));
        Assert.assertEquals(6, McMarker.getDigit(studentExcel[15]));
        Assert.assertEquals(3, McMarker.getDigit(studentExcel[17]));
        Assert.assertEquals(3, McMarker.getDigit(studentExcel[19]));
    }

    @Test
    public void getAllAnswers() throws Exception {
        int[] actual = McMarker.getAllAnswers(answerExcel);
        String expect = "[0, 1, 2, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1]";
        Assert.assertEquals(expect, Arrays.toString(actual));
    }

    @Test
    public void getAnswerForQuestion() throws Exception {
        for (int i = 0; i < 5; i++) {
            int actual = McMarker.getAnswerForQuestion(i, answerExcel);
            int expect = i;
            Assert.assertEquals(expect, actual);
        }
        int actual = McMarker.getAnswerForQuestion(6, answerExcel);
        int expect = -1;
        Assert.assertEquals(expect, actual);
    }

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    public String ExportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;

        Path tempFile;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(resourceName).getFile());

            stream = new FileInputStream(file);
            ;//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            tempFile = Files.createFile(Paths.get(testDataFolder.toString(), file.getName()));
            resStreamOut = new FileOutputStream(tempFile.toString());
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return tempFile.toString();
    }

}