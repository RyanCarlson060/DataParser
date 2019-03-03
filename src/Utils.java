import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {

    public static String readFileAsString(String filepath) {
        StringBuilder output = new StringBuilder();

        try (Scanner scanner = new Scanner(new File(filepath))) {

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                output.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    public static ArrayList<ElectionResult> parse2016PresidentialResults(String filedata) {
        String data = normalizeLineBreaks(filedata);
        String[] lines = data.split("\n");

        ArrayList<ElectionResult> dataSet = new ArrayList<>();

        String[] lineData;
        for (int a = 1; a < lines.length; a++) {
            lineData = lines[a].split(",");
            if (lineData.length == 11) {
                dataSet.add(parseLinesWithoutExtraCommas(lineData));
            } else {
                dataSet.add(parseLinesWithExtraCommas(lineData));
            }

        }

        return dataSet;

    }

    private static ElectionResult parseLinesWithoutExtraCommas(String[] lineData) {
        double[] dataVals = new double[5];
        for (int i = 1; i <= 5; i++) {
            dataVals[i - 1] = Double.parseDouble(lineData[i].trim());
        }
        int diff = Integer.parseInt(lineData[6].trim());
        double diffPerPoint = Double.parseDouble(lineData[7].substring(0, lineData[7].length() - 1).trim());
        int combined_fips = Integer.parseInt(lineData[10].trim());

        ElectionResult result = new ElectionResult(dataVals[0], dataVals[1], dataVals[2], dataVals[3], dataVals[4], diff, diffPerPoint, lineData[8], lineData[9], combined_fips);
        return result;
    }

    private static ElectionResult parseLinesWithExtraCommas(String[] lineData) {
        int differenceInCommas = lineData.length - 11;
        double[] dataVals = new double[5];
        for (int i = 1; i <= 5; i++) {
            dataVals[i - 1] = Double.parseDouble(lineData[i].trim());
        }
        String diffs = lineData[6].trim();
        for (int i = 0; i < differenceInCommas; i++) {
            diffs = diffs + lineData[7 + i].trim();
        }
        int diff = Integer.parseInt(diffs.substring(1, diffs.length() - 1));
        double diffPerPoint = Double.parseDouble(lineData[7 + differenceInCommas].substring(0, lineData[7 + differenceInCommas].length() - 1).trim());
        int combined_fips = Integer.parseInt(lineData[10 + differenceInCommas].trim());

        ElectionResult result = new ElectionResult(dataVals[0], dataVals[1], dataVals[2], dataVals[3], dataVals[4], diff, diffPerPoint, lineData[8 + differenceInCommas], lineData[9 + differenceInCommas], combined_fips);
        return result;
    }

    private static String normalizeLineBreaks(String s) {
        s = s.replace('\u00A0', ' '); // remove non-breaking whitespace characters
        s = s.replace('\u2007', ' ');
        s = s.replace('\u202F', ' ');
        s = s.replace('\uFEFF', ' ');

        return s.replace("\r\n", "\n").replace('\r', '\n');
    }


}
