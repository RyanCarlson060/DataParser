import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/***
 *
 * Main class for data parsers
 * @author: Ryan Carlson
 *
 */

public class Main {
    public static void main(String[] args) {
        // Test of utils
        String ElectionData = Utils.readFileAsString("data/2016_Presidential_Results.csv");
        String EducationData = Utils.readFileAsString("data/Education.csv");
        String EmploymentData = Utils.readFileAsString("data/Unemployment.csv");
        // System.out.println(ElectionData);
        //ArrayList<ElectionResult> electionResults = Utils.parse2016PresidentialResults(ElectionData);
        //System.out.println(electionResults.get(0));
        DataManager d = Utils.parseFilesForCSV(ElectionData, EducationData, EmploymentData);
        System.out.println(d.getStates().get(0).getCounties().get(0).getUnemploymentRate());

        ArrayList<City> cities = Utils.getNonprofits();

        saveDataToFile("csv1", d);
        saveDataToFile("Nonprofits", cities, d.getStates());

    }

    private static void saveDataToFile(String file, DataManager d) {
        String data = "";
        ArrayList<State> states = d.getStates();
        for (State s : states) {
            ArrayList<County> counties = s.getCounties();
            for (County c : counties) {
                if (c.getUnemploymentRate() != -1) {
                    data = data + s.getName() + ", " + c.getName() + ", " + c.getUnemploymentRate() + ", " + c.getHsGradRate() + "\n";
                }
            }
        }
        writeDataToFile(file, data);
    }

    private static void saveDataToFile(String file, ArrayList<City> cities, ArrayList<State> states) {
        String data = "";
        boolean stateExists = false;
        for (City c : cities) {
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i).getName().equals(c.getState())) {
                    stateExists = true;
                }

            }
            if (stateExists) {
                data = data + c.getState() + ", " + c.getName() + ", " + c.getNumNonprofits() + "\n";
            }
        }
        writeDataToFile(file, data);
    }

    private static void writeDataToFile(String file, String data) {
        File outfile = new File(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outfile))) {
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
