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
        DataManager d = Utils.parseFilesIntoDataManager(ElectionData, EducationData, EmploymentData);
        System.out.println(d.getStates().get(0).getCounties().get(0).getEduc2016().getOnlyHighSchool());
    }

}
