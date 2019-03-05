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
        String data = Utils.readFileAsString("data/2016_Presidential_Results.csv");
       // System.out.println(data);


        ArrayList<ElectionResult> electionResults =Utils.parse2016PresidentialResults(data);
        System.out.println(electionResults.get(0));

    }

}
