/*
<key>
   rmy/HdJSzX1TP4KFv9F1ZQ==
</key>
*/
package hk.edu.vtc.it3101.mcmarking;

import com.sun.org.apache.xml.internal.utils.res.StringArrayWrapper;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Scanner;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.length;

/**
 * McMarker is a tools to mark multiple choice question, and you have to complete all the missing codes according to the java documents description.
 */
public class McMarker {
    public static void main(String[] args) {
        String folderPath;
        //Ask user to inout the folder Path
        //Example input E:\\Working\\MultipleChoicesTestMarking\\data\\Test1
        //Call readFolderPath
        folderPath = readFolderPath();
        //Read Answer Excel file.
        String answerExcelPathName = folderPath + java.io.File.separator + "Answer.xlsx";
        //answerExcel is 75 x 26 2D array
        String[][] answerExcel = ExcelHelper.getExcelTo2DStringArray(answerExcelPathName);
        print2DStringArray(answerExcel);
        //Create array that can save all answers.
        int[] answers = getAllAnswers(answerExcel);

        //Get the list if excel file from the folder.
        String[] excelList = ExcelHelper.getStudentAnswerPaths(folderPath);

        //Create String array to store student ID, and O is wrong!
        String[] studentIds = new String[40];
        //Create 2D int array to store student answer.
        int[][] studentAnswers = new int[40][];
        populateStudentAnswers(excelList, studentIds, studentAnswers);
        //Call printAnswerReport.
        printAnswerReport(studentIds, studentAnswers);

        //Call getTotalMarks, and save the result to totalMarks.
        int[] totalMarks = getTotalMarks(answers, studentAnswers);
        //Call printTotalMarkReport.
        printTotalMarkReport(studentIds, totalMarks);

        //Call getWrongAnswers, and save the result to studentWrongAnswer.
        char[][] studentWrongAnswer = getWrongAnswers(answers, studentAnswers);
        //Call printWrongAnswerReport.
        printWrongAnswerReport(studentIds, studentWrongAnswer);
    }

    /**
     * Prompt user to input folder that contains all excel files.<br>
     * 1. Create Scanner.<br>
     * 2. Display message "Please input assignment folder path:"<br>
     * 3. get user input folder path and return the input value<br>
     *
     * @return folder path that contains all mc excel files.
     */
    public static String readFolderPath() {
        System.out.println("Please input assignment folder path:");
        Scanner input = new Scanner(System.in);
        return input.next();
    }

    /**
     * Print row array and separate with tab.<br>
     * 1. Loop through row<br>
     * 2. Print each row value- if it is not null, and for null, print empty string "".<br>
     * 3. Print tab after each value.<br>
     *
     * @param row it is String array.
     */
    public static void printRow(String[] row) {
        for(String rowItem : row){
            if(rowItem == null) rowItem = "";
            System.out.print(rowItem + "\t");
        }
    }

    /**
     * Print 2D Array for display excel answer.<br>
     * 1. Loop through answerExcel.<br>
     * 2. Call printRow for each item.<br>
     * 3. Print new line.<br>
     *
     * @param answerExcel 2D String Array stores students answer.
     */
    public static void print2DStringArray(String[][] answerExcel) {
        for(String[] row : answerExcel){
            printRow(row);
            System.out.print("\n");
        }
    }

    /**
     * In addition to the total marks, teachers are interested to know how students are getting wrong!<br>
     * You have to use nested for loop to compare standard answer and student answer.<br>
     * Requirements:<br>
     * 1. Return a 2D char array that store the wrong answer.<br>
     * 2. if student answer is wrong, then store the wrong answer.<br>
     * 3. if standard answer is equal to -1 (the question is not used!), then store ' '.<br>
     * 4. if student answer is equal to -1 (student fills in something wrong i.e. filled 2 cells!), then store 'X'<br>
     * 5. You must use switch to handle the logic!<br>
     * <br>
     *
     * @param answers        int array that represent the standard answer.
     * @param studentAnswers 2D student answer array
     * @return 2D char array that show how students are getting wrong!
     */

    public static char[][] getWrongAnswers(int[] answers, int[][] studentAnswers) {
        char[][] studentWrongAnswer = new char[studentAnswers.length][answers.length];
        for(int studentCount = 0; studentCount < studentAnswers.length; studentCount++){
            if(studentAnswers[studentCount] == null) break;
            for(int ansCount = 0; ansCount < answers.length; ansCount++){
                if(studentAnswers[studentCount][ansCount] != answers[ansCount]){
                    switch (studentAnswers[studentCount][ansCount]){
                        case 0: studentWrongAnswer[studentCount][ansCount] = 'A';
                                break;
                        case 1: studentWrongAnswer[studentCount][ansCount] = 'B';
                               break;
                        case 2: studentWrongAnswer[studentCount][ansCount] = 'C';
                                break;
                        case 3: studentWrongAnswer[studentCount][ansCount] = 'D';
                                break;
                        case 4: studentWrongAnswer[studentCount][ansCount] = 'E';
                                break;
                        case -1: studentWrongAnswer[studentCount][ansCount] = 'X';
                                 break;
                    }
                }else{
                    studentWrongAnswer[studentCount][ansCount] = ' ';
                }
            }
        }
        return studentWrongAnswer;
    }

    /**
     * Get the total mark for each student.<br>
     * 1. Create array totalMarks.<br>
     * 2. Nested for loop to calculate total mark, and you have to take care of -1, and correct answer.<br>
     * 3. Return totalMarks<br>
     *
     * @param answers        correct answer array
     * @param studentAnswers 2D student answer array
     * @return total mark array
     */
    public static int[] getTotalMarks(int[] answers, int[][] studentAnswers) {
        int[] totalMarks = new int[studentAnswers.length];
        for(int studentCount = 0; studentCount < studentAnswers.length; studentCount++){
            if(studentAnswers[studentCount] == null) break;
            for(int ansCount = 0; ansCount < answers.length; ansCount++){
                if(studentAnswers[studentCount][ansCount] == answers[ansCount] && studentAnswers[studentCount][ansCount] != -1) {
                    totalMarks[studentCount]++;
                }
            }
        }
        return totalMarks;
    }

    /**
     * Print Total mark report.<br>
     * 1. First line is "ID\t\t\tMarks"<br>
     * 2. Loop through both studentIds and totalMarks. i.e. ID mark<br>
     *
     * @param studentIds String Array of student id.
     * @param totalMarks int Array of student total Mark.
     */
    public static void printTotalMarkReport(String[] studentIds, int[] totalMarks) {
        System.out.println("ID\t\t\tMarks");
        for (int studentCount = 0; studentCount < studentIds.length; studentCount++) {
            if(studentIds[studentCount] == null) break;
            System.out.println(studentIds[studentCount] + "\t" + totalMarks[studentCount]);
        }
    }

    /**
     * Print the first row in ID\t\t\tq1\tq2\t.....<br>
     * 1. Print "ID\t\t\t" (Don't use println).<br>
     * 2. Loop based on numberOfQuestion, and print "qx\t", where x is 1,2,..., numberOfQuestion<br>
     * 3. Print a new line.<br>
     *
     * @param numberOfQuestion Number of question
     */
    public static void printFirstRow(int numberOfQuestion) {
        System.out.print("ID\t\t\t");
        for(int questionCount = 1; questionCount <= numberOfQuestion; questionCount++) {
            System.out.print("q" + questionCount + "\t");
        }
        System.out.print("\n");
    }

    /**
     * Print the answer report<br>
     * 1. call printFirstRow.<br>
     * 2. Use nested for loop to print studentIds and studentAnswers. Each result separates with tab.<br>
     *
     * @param studentIds     String Array of student id
     * @param studentAnswers 2D int Array of student answer.
     */
    public static void printAnswerReport(String[] studentIds, int[][] studentAnswers) {
        printFirstRow(studentAnswers[0].length);
        for(int studentCount = 0; studentCount < studentIds.length; studentCount++){
            if(studentIds[studentCount] == null) break;
            System.out.print(studentIds[studentCount] + "\t");
            for(int answerCount = 0; answerCount < studentAnswers[studentCount].length; answerCount++){
                System.out.print(studentAnswers[studentCount][answerCount] + "\t");
            }
            System.out.print("\n");
        }
    }

    /**
     * Print the Wrong answer report.<br>
     * The code is the same as printAnswerReport, since your coding skill is not able to combine it into one at this moment!<br>
     *
     * @param studentIds     String Array of student id
     * @param studentAnswers 2D char Array of student answer.
     */
    public static void printWrongAnswerReport(String[] studentIds, char[][] studentAnswers) {
        printFirstRow(studentAnswers[0].length);
        for(int studentCount = 0; studentCount < studentIds.length; studentCount++){
            if(studentIds[studentCount] == null) break;
            System.out.print(studentIds[studentCount] + "\t");
            for(int answerCount = 0; answerCount< studentAnswers[studentCount].length; answerCount++){
                System.out.print(studentAnswers[studentCount][answerCount] + "\t");
            }
            System.out.print("\n");
        }
    }

    /**
     * Populate/Set studentIds and studentAnswers. Array valve can be updated in another method!<br>
     * 1. Loop through excelList<br>
     * 2. Call ExcelHelper.getExcelTo2DStringArray for each item in excelList, and store it in a 2D string array variable.<br>
     * 3. Call getStudentId, and save it to studentIds.<br>
     * 4. Call getAllAnswers, and save it to studentAnswers.<br>
     *
     * @param excelList      String array with all excel files, excepts for the Answer.xlsx
     * @param studentIds     student id string array reference, and it will be filled with student ID.
     * @param studentAnswers studentAnswers 2D int array, and it will be filled with student answer.
     */
    public static void populateStudentAnswers(String[] excelList, String[] studentIds, int[][] studentAnswers) {
        int count = 0;
        for(String excelFile : excelList){
            String excelAnswer[][] = ExcelHelper.getExcelTo2DStringArray(excelFile);
            studentIds[count] = getStudentId(excelAnswer);
            studentAnswers[count] = getAllAnswers(excelAnswer);
            count ++;
        }
    }

    /**
     * Input 2D Answer Excel and return student ID or "error", if any digit return -1. <br>
     *
     * @param answerExcel 2D String Array stores students answer.
     * @return Student ID in String.
     */
    public static String getStudentId(String[][] answerExcel){
        String studentId = "";
        for(int rows = 13; rows <= 29; rows = rows + 2){
            int digit = getDigit(answerExcel[rows]);
            studentId = studentId.concat(String.valueOf(digit));
        }
        if (studentId.equals("")) studentId = "error";
        return studentId;
    }

    /**
     * Input string array row, and startX, then get the filled digit.<br>
     * It is very similar to getAnswerForQuestion!<br>
     *
     * @param row String array represent the row of excel.
     * @return 0 - 9 (the filled digit) or -1 for missing or filled more than one cell.
     */
    public static int getDigit(String[] row) {
        int digit = 0;
        for(int cols = 3; cols <= 21; cols = cols+2){
            if(row[cols].length() > 0) break;
            digit++;
        }
        return digit;
    }


    /**
     * Read all answers from 2D string excel array.<br>
     * 1. Create int Array "answers" of size 40.<br>
     * 2. Use for loop to call getAnswerForQuestion for each question, and save it to "answers'<br>
     * 3. return answers<br>
     *
     * @param answerExcel 75 x 26 2D String array
     * @return integer arrays with all answers, and each value maps to A to E, and -1 for other case.
     */
    public static int[] getAllAnswers(String[][] answerExcel) {
        int[] answers = new int[40];
        for(int question = 0; question < answers.length; question++) {
            answers[question] = getAnswerForQuestion(question, answerExcel);
        }
        return answers;
    }

    /**
     * Read answer for a question from 2D string excel array.<br>
     * 1. create 2 int variables startX, and startY. Check the excel, and set startY to proper value. <br>
     * 2. Use if/else to set proper startX according to q, since there are 2 columns! <br>
     * 3. create char array "allBox" of size 5, and it will store all filled cells.<br>
     * 4. Loop through all answer related to question q. <br>
     * 5. Save the first character or ' ', if there is nothing in the cell! (Hints: check length!) <br>
     * 6. create 2 int variables filledCount and filledPosition. <br>
     * 7. Loop through "allBox" to count number of answer and save it to filledCount, and store last answer position to filledPosition. <br>
     * 8. if filledCount and filledPosition are both valid, then return filledPosition, else return -1 to represent error! <br>
     *
     * @param q           start from 0, and it means question 1 is 0.
     * @param answerExcel 75 x 26 2D String array represent sheet data.
     * @return 0 to 4, and it maps to A to E, and -1 for other case.
     */
    public static int getAnswerForQuestion(int q, String[][] answerExcel) {
        int startX, startY;
        char allBox[] = new char[5];
        int filledCount = 0;
        int filledPosition = -1;
        if(q < 20){
            startX = 3;
            startY = 35+2*q;
        }else{
            startX = 15;
            startY = 35+2*(q-20);
        }
        for(int count = 0; count < allBox.length; count++){
            if(answerExcel[startY][startX + 2 * count].length() > 0){
                allBox[count] = answerExcel[startY][startX + 2 * count].charAt(0);
            }else{
                allBox[count] = ' ';
            }
        }
        for(int ans = 0; ans < 5 ; ans++){
           if(allBox[ans] != ' '){
               filledCount++;
               filledPosition = ans;
           }
        }
        if(filledCount > 1) filledPosition = -1;
        return filledPosition;
    }
}