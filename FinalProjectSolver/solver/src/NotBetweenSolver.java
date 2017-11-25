import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by eamonncarson on 11/10/17.
 */
public class NotBetweenSolver {

    public static void main(String[] argv) {
        if (argv.length < 1) {
            System.out.println("Error: 1 command line argument for the filename of Wizards file required.");
            System.exit(1);
        }
        String inputFilename = "input/" + argv[0] + ".in";
        NotBetweenProblem nbp = new NotBetweenProblem(inputFilename);
        SatisfiabilityProblem sat = null;
        try {
            sat = new SatisfiabilityProblem(nbp);
        } catch (TimeoutException e) {
            System.out.println("Error: SAT algorithm has timed out.");
            System.exit(1);
        } catch (ContradictionException e) {
            System.out.println("Error: I screwed up.");
            System.exit(1);
        }

        String[] wizardOrder = nbp.convertToNames(sat.getSolution());
        System.out.println("Found valid order:");
        String output = "";

        for (String wizard : wizardOrder) {
            output += wizard + " ";
        }

        System.out.println(output);

        String outputFileName = "output/" + argv[0] + ".out";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName))) {
            bw.write(output);
        } catch (IOException e) {
            System.out.println("SORRY: error writing file.");
        }
    }

}
