import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/**
 * Created by eamonncarson on 11/10/17.
 */
public class NotBetweenProblem {

    int numWizards;
    String[] names; // the set of wizard names by index
    Map<String, Integer> nameId; // a map from wizard names to wizard IDs
    List<NotBetweenClause> clauses; // a list of conditions the solution must abide by

    class NotBetweenClause {
        int boundary1Id, boundary2Id, bannedId;

        public NotBetweenClause(int boundary1Id, int boundary2Id, int bannedId) {
            this.boundary1Id = boundary1Id;
            this.boundary2Id = boundary2Id;
            this.bannedId = bannedId;
        }
    }

    /**
     * Create NotBetweenProblem instance from input file
     * input file specs are explained in final project spec.
     * @param filename
     */
    public NotBetweenProblem(String filename) {
        nameId = new HashMap<>();
        clauses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // get number of wizards
            String numWizardsRaw = br.readLine();
            numWizards = Integer.parseInt(numWizardsRaw.trim());

            // get wizard names (and build nameId and names)
            /*
            String namesRaw = br.readLine();
            names = namesRaw.split(" ");
            for (int i = 0; i < names.length; i++) {
                nameId.put(names[i], i);
            }
            */
            int nameIndex = 0;
            names = new String[numWizards];

            // get number of clauses
            String numClausesRaw = br.readLine();
            int numClauses = Integer.parseInt(numClausesRaw.trim());


            // build clauses
            String clauseRaw = null;
            while ((clauseRaw = br.readLine()) != null) {
                String[] clauseTerms = clauseRaw.split(" ");
                // add new names to the list
                for (String name : clauseTerms) {
                    if (!nameId.containsKey(name)) {
                        names[nameIndex] = name;
                        nameId.put(name, nameIndex);
                        nameIndex++;
                    }
                }
                // add the clause
                int boundary1Id = nameId.get(clauseTerms[0]);
                int boundary2Id = nameId.get(clauseTerms[1]);
                int bannedId    = nameId.get(clauseTerms[2]);
                clauses.add(new NotBetweenClause(boundary1Id, boundary2Id, bannedId));
            }
        } catch (IOException e) {
            System.out.println("Error reading file. Exiting.");
            exit(1);
        }
    }

    /**
     * Given a list of wizard IDs, return the corresponding list of names
     * @param ids
     * @return
     */
    public String[] convertToNames(List<Integer> ids) {
        String[] result = new String[ids.size()];
        int index = 0;
        for (int id : ids) {
            result[index] = names[id];
            index++;
        }
        return result;
    }
}
