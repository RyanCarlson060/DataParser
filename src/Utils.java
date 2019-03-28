import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {

    private static final boolean UNEMPLOYMENT = true;
    private static final boolean HS_GRAD_RATE = false;


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

    public static DataManager parseFilesForCSV(String electionFile, String educationFile, String employmentFile) {
        ArrayList<State> states;
        String data = normalizeLineBreaks(electionFile);
        String data2 = normalizeLineBreaks(educationFile);
        String data3 = normalizeLineBreaks(employmentFile);

        String[] electionLines = data.split("\n");
        String[] educationLines = data2.split("\n");
        String[] employmentLines = data3.split("\n");

        states = getStates(electionLines);
        addAllDataToAllCounties(states, electionLines, educationLines, employmentLines);
        return new DataManager(states);
    }

    private static void addAllDataToAllCounties(ArrayList<State> states, String[] electionLines, String[] educationLines, String[] employmentLines) {
        String[] electionLineData;
        for (int a = 1; a < electionLines.length; a++) {
            electionLineData = electionLines[a].split(",");
            for (int i = 0; i < states.size(); i++)
                if (getState(electionLineData).equals(states.get(i).getName()))
                    addDataToState(states.get(i), electionLineData, educationLines, employmentLines);
        }
    }

    private static void addDataToState(State state, String[] electionLineData, String[] educationLines, String[] employmentLines) {
        ArrayList<County> counties = state.getCounties();
        String countyName = getCounty(electionLineData);
        int fips = getFip(electionLineData);
        boolean countyExists = false;
        for (County c : counties)
            if (c.getName().equals(countyName) && c.getFips() == fips)
                countyExists = true;
        if (!countyExists)
            createNewCountyWithData(counties, electionLineData, educationLines, employmentLines, fips);
    }

    private static void createNewCountyWithData(ArrayList<County> counties, String[] electionLineData, String[] educationLines, String[] employmentLines, int fips) {
        double hsGradRate = getHSGradRate(educationLines, fips);
        double unemploymentRate = getUnemploymentRate(employmentLines, fips);
        counties.add(new County(getCounty(electionLineData), getFip(electionLineData), unemploymentRate, hsGradRate));
    }

    private static ArrayList<State> getStates(String[] lines) {
        ArrayList<State> states = new ArrayList<>();
        String[] lineData;
        for (int a = 1; a < lines.length; a++) {
            lineData = lines[a].split(",");
            String state = getState(lineData);
            boolean stateExists = false;
            for (State s : states)
                if (s.getName().equals(state))
                    stateExists = true;
            if (!stateExists) {
                State s = new State(state, new ArrayList<>());
                states.add(s);
            }
        }
        return states;
    }

    private static double getUnemploymentRate(String[] employmentLines, int fips) {
        return getRate(employmentLines, fips, UNEMPLOYMENT);
    }

    private static double getHSGradRate(String[] educationLines, int fips) {
        return getRate(educationLines, fips, HS_GRAD_RATE);
    }

    private static double getRate(int startingLine, int endingLine, String[] dataLines, int fips, boolean b) {
        String[] lineData;
        int countyLine = -1;
        for (int i = startingLine; i < endingLine; i++) {
            lineData = dataLines[i].split(", ");
            if (Integer.parseInt(lineData[0]) == fips) {
                countyLine = i;
            }
        }
        if (countyLine != -1) {
            String line = dataLines[countyLine];
            line = optimizeLine(line);
            String[] data = line.split(",");
            if (b)
                return (Double.parseDouble(data[45].trim()) + Double.parseDouble(data[41].trim()) + Double.parseDouble(data[37].trim()) + Double.parseDouble(data[33].trim()) + Double.parseDouble(data[29].trim())) / 5;
            else
                return 100 - Double.parseDouble(data[45].trim());

        }
        return -1;
    }

    private static double getRate(String[] dataLines, int fips, boolean b) {
        if (b)
            return getRate(9, 3204, dataLines, fips, true);
        else
            return getRate(6, 3209, dataLines, fips, false);
    }

    private static String optimizeLine(String line) {
        int index = 0;
        while (index < line.length() && index >= 0) {
            line = replaceCommas(line, index);
            index++;
        }
        line = line.replace("\"", "");
        line = line.trim();
        System.out.println(line);
        return line;
    }

    private static String replaceCommas(String s, int index) {
        int quoteLoc = s.indexOf("\"", index);
        if (!(s.substring(quoteLoc + 1, quoteLoc + 2).equals(",")) && quoteLoc != -1) {
            int nextQuoteLoc = s.indexOf("\"", quoteLoc + 1);
            if (nextQuoteLoc != -1) {
                String originalString = s.substring(quoteLoc + 1, nextQuoteLoc);
                String fixedString = new String(originalString);
                fixedString = fixedString.replace(",", "");
                s = s.replace(originalString, fixedString);
            }
        }
        return s;
    }

    private static String getCounty(String[] lineData) {
        int differenceInCommas = lineData.length - 11;
        return lineData[9 + differenceInCommas];
    }

    private static int getFip(String[] lineData) {
        int differenceInCommas = lineData.length - 11;
        int combined_fips = Integer.parseInt(lineData[10 + differenceInCommas].trim());
        return combined_fips;
    }

    public static ArrayList<City> getNonprofits() {
        String[] data1Lines = getLinesFromFileName("data/eo1.csv");
        String[] data2Lines = getLinesFromFileName("data/eo2.csv");
        String[] data3Lines = getLinesFromFileName("data/eo3.csv");
        ArrayList<City> cities = new ArrayList<>();
        addCities(cities, data1Lines);
        addCities(cities, data2Lines);
        addCities(cities, data3Lines);
        return cities;
    }

    private static City getCity(ArrayList<City> cities, String[] lineData) {
        for (City c : cities) {
            if (c.getName().equals(lineData[4]) && c.getState().equals(lineData[5])) {
                return c;
            }
        }
        return null;
    }

    private static boolean cityExists(ArrayList<City> cities, String[] lineData) {
        for (City c : cities) {
            if (c.getName().equals(lineData[4]) && c.getState().equals(lineData[5])) {
                return true;
            }
        }
        return false;
    }

    private static String getState(String[] lineData) {
        int differenceInCommas = lineData.length - 11;
        return lineData[8 + differenceInCommas];
    }

    private static String normalizeLineBreaks(String s) {
        s = s.replace('\u00A0', ' '); // remove non-breaking whitespace characters
        s = s.replace('\u2007', ' ');
        s = s.replace('\u202F', ' ');
        s = s.replace('\uFEFF', ' ');

        return s.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static String[] getLinesFromFileName(String fileName) {
        return normalizeLineBreaks(readFileAsString(fileName)).split("\n");
    }

    private static void addCities(ArrayList<City> cities, String[] dataLines) {
        String line;
        for (int i = 1; i < dataLines.length; i++) {
            System.out.println(i);
            line = dataLines[i];
            String[] lineData = line.split(",");

            if (cityExists(cities, lineData)) {
                City c = getCity(cities, lineData);
                c.incrementNonprofits();
            } else {
                cities.add(new City(lineData[4], lineData[5]));
            }
        }
    }


   /*
   All methods below this point aren't actually used in the final product.
   We didn't go through the process of cleaning them up.
    */


    private static ElectionResult parseElectionLines(String[] lineData) {
        int diff;
        int differenceInCommas = lineData.length - 11;
        double[] dataVals = new double[5];
        for (int i = 1; i <= 5; i++) {
            dataVals[i - 1] = Double.parseDouble(lineData[i].trim());
        }
        String diffs = lineData[6].trim();
        for (int i = 0; i < differenceInCommas; i++) {
            diffs = diffs + lineData[7 + i].trim();
        }
        if (differenceInCommas > 0) {
            diff = Integer.parseInt(diffs.substring(1, diffs.length() - 1));
        } else {
            diff = Integer.parseInt(diffs);
        }
        double diffPerPoint = Double.parseDouble(lineData[7 + differenceInCommas].substring(0, lineData[7 + differenceInCommas].length() - 1).trim());
        int combined_fips = Integer.parseInt(lineData[10 + differenceInCommas].trim());

        ElectionResult result = new ElectionResult(dataVals[0], dataVals[1], dataVals[2], dataVals[3], dataVals[4], diff, diffPerPoint, lineData[8 + differenceInCommas], lineData[9 + differenceInCommas], combined_fips);
        return result;
    }

    public static ArrayList<ElectionResult> parse2016PresidentialResults(String filedata) {
        String data = normalizeLineBreaks(filedata);
        String[] lines = data.split("\n");

        ArrayList<ElectionResult> dataSet = new ArrayList<>();

        String[] lineData;
        for (int a = 1; a < lines.length; a++) {
            lineData = lines[a].split(",");
            ElectionResult result = parseElectionLines(lineData);
            dataSet.add(result);

        }

        return dataSet;
    }

    private static Election2016 getElection(String[] electionLineData) {
        double demVotes = Double.parseDouble(electionLineData[0]);
        double gopVotes = Double.parseDouble(electionLineData[1]);
        double totalVotes = Double.parseDouble(electionLineData[2]);

        return new Election2016(demVotes, gopVotes, totalVotes);
    }

    private static Education2016 getEducation(String[] educationLines, int fips) {
        String[] lineData;

        int countyLine = -1;
        for (int i = 6; i < 3209; i++) {
            lineData = educationLines[i].split(",");
            if (Integer.parseInt(lineData[0]) == fips) {
                countyLine = i;
            }
        }
        if (countyLine != -1) {
            String line = educationLines[countyLine];
            line = optimizeLine(line);
            String[] data = line.split(",");

            return new Education2016(Double.parseDouble(data[42].trim()), Double.parseDouble(data[39].trim()), Double.parseDouble(data[40].trim()), Double.parseDouble(data[41].trim()));
        }
        return new Education2016(-1, -1, -1, -1);
    }

    private static Employment2016 getEmployment(String[] employmentLines, int fips) {
        String[] lineData;
        int countyLine = -1;
        for (int i = 9; i < 3204; i++) {
            lineData = employmentLines[i].split(",");
            if (Integer.parseInt(lineData[0]) == fips) {
                countyLine = i;
            }
        }
        if (countyLine != -1) {
            String line = employmentLines[countyLine];
            line = optimizeLine(line);
            String[] data = line.split(",");

            return new Employment2016(Integer.parseInt(data[42].trim()), Integer.parseInt(data[43].trim()), Integer.parseInt(data[44].trim()), Double.parseDouble(data[45].trim()));
        }
        return new Employment2016(-1, -1, -1, -1);
    }

    public static DataManager parseFilesIntoDataManager(String electionFile, String educationFile, String employmentFile) {

        ArrayList<State> states = new ArrayList<>();
        String data = normalizeLineBreaks(electionFile);
        String data2 = normalizeLineBreaks(educationFile);
        String data3 = normalizeLineBreaks(employmentFile);

        String[] electionLines = data.split("\n");
        String[] educationLines = data2.split("\n");
        String[] employmentLines = data3.split("\n");


        states = getStates(electionLines);
        String[] electionLineData;
        for (int a = 1; a < electionLines.length; a++) {
            electionLineData = electionLines[a].split(",");

            for (int i = 0; i < states.size(); i++) {
                if (getState(electionLineData).equals(states.get(i).getName())) {

                    ArrayList<County> counties = states.get(i).getCounties();
                    String countyName = getCounty(electionLineData);
                    int fips = getFip(electionLineData);
                    boolean countyExists = false;
                    for (County c : counties) {
                        if (c.getName().equals(countyName) && c.getFips() == fips) {
                            countyExists = true;
                        }
                    }
                    if (!countyExists) {
                        Education2016 education = getEducation(educationLines, fips);
                        Employment2016 employment = getEmployment(employmentLines, fips);
                        counties.add(new County(getCounty(electionLineData), getFip(electionLineData), getElection(electionLineData), education, employment));

                    }
                }

            }
        }
        return new DataManager(states);

    }
}

