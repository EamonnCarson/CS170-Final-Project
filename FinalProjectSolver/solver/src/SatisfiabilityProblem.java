import com.sun.tools.javac.util.ArrayUtils;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.*;

/**
 * Created by eamonncarson on 11/10/17.
 */
public class SatisfiabilityProblem {

    class Relation {
        // a < b
        int a, b;
        boolean flipped = false;

        public Relation(int a, int b) {
            if (a > b) {
                int tmp = a;
                a = b;
                b = tmp;
                flipped = true;
            }
            this.a = a;
            this.b = b;
        }

        @Override
        public int hashCode() {
            return a * numObjects + b;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Relation) {
                Relation that = (Relation) obj;
                return (this.a == that.a) && (this.b == that.b);
            } else {
                return false;
            }
        }
    }

    class idComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            Relation r = new Relation(o1, o2);
            int rid = relationId.get(r);
            // note that rids are 1 indexed
            int relationHolds = solution[rid - 1];
            if (r.flipped) {
                relationHolds = -relationHolds;
            }
            return relationHolds;
        }
    }

    private int numObjects;
    private int numRelations;
    private Relation[] relations;
    private Map<Relation, Integer> relationId;
    private int[] solution;

    /**
     * Generate a SAT problem that corresponds the the
     * NotBetweenProblem given, and solve it.
     * @param nbp
     */
    public SatisfiabilityProblem(NotBetweenProblem nbp) throws ContradictionException, TimeoutException {
        generateRelations(nbp);
        ISolver solver = SolverFactory.newDefault();
        solver.newVar(numRelations);
        // Add nbp clauses
        for (NotBetweenProblem.NotBetweenClause nbpClause : nbp.clauses) {
            // get canonical forms of relations
            Relation relation1 = new Relation(nbpClause.boundary1Id, nbpClause.bannedId);
            Relation relation2 = new Relation(nbpClause.bannedId, nbpClause.boundary2Id);
            // look up their ids
            int relation1Id = relationId.get(relation1);
            int relation2Id = relationId.get(relation2);
            // see if they are the inverted versions
            relation1Id = relation1.flipped ? -relation1Id : relation1Id;
            relation2Id = relation2.flipped ? -relation2Id : relation2Id;
            // add to the SAT problem
            int[] literals = {relation1Id, relation2Id};
            solver.addExactly(new VecInt(literals), 1);
        }
        // Add relation consistency clauses
        for (int upper = 0; upper < numObjects; upper++) {
            for (int lower = 0; lower < upper; lower++) {
                for (int inter = lower + 1; inter < upper; inter++) {
                    Relation relation1 = new Relation(lower, inter);
                    Relation relation2 = new Relation(inter, upper);
                    Relation relation3 = new Relation(lower, upper);
                    int id1 = relationId.get(relation1);
                    int id2 = relationId.get(relation2);
                    int id3 = relationId.get(relation3);
                    int[] literals1 = {-id1, -id2, id3}; // id1 and id2 ==> id3
                    int[] literals2 = {id1, id2, -id3}; // -id1 and -id2 ==> -id3
                    solver.addClause(new VecInt(literals1));
                    solver.addClause(new VecInt(literals2));
                }
            }
        }
        // Solve
        IProblem problem = solver;
        solution = problem.findModel();
    }

    /**
     * Return a list of ids which fulfill the conditions of the nbp
     * @return
     */
    public List<Integer> getSolution() {
        // initialize array 0 ... numObjects
        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i < numObjects; i++) {
            order.add(i);
        }
        // sort it using the relation booleans we know
        order.sort(new idComparator());
        return order;
    }

    public List<Integer> getSolution2() {
        int[] numGreater = new int[numObjects];
        for (int id = 0; id < numObjects; id++) {
            for (int otherId = 0; otherId < numObjects; otherId++) {
                if (otherId != id) {
                    Relation r = new Relation(id, otherId);
                    int rid = relationId.get(r);
                    int truth = r.flipped ? -solution[rid - 1] : solution[rid - 1];
                    if (truth > 0) { //the relation is true, so id < otherId
                        numGreater[id]++;
                    }
                }
            }
        }
        ArrayList<Integer> numGreaterList = new ArrayList<>();
        for (int elem : numGreater) {
            numGreaterList.add(elem);
        }
        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i < numObjects; i++) {
            order.add(numGreaterList.indexOf(i));
        }
        return order;
    }

    /**
     * generate the set of relation boolean inputs for the SAT problem
     * @param nbp
     */
    private void generateRelations(NotBetweenProblem nbp) {
        numObjects = nbp.numWizards;
        numRelations = numObjects * (numObjects - 1) / 2;
        relations = new Relation[numRelations + 1];
        relationId = new HashMap<>(numRelations);
        int index = 1;
        for (int i = 0; i < numObjects; i++) {
            for (int j = 0; j < i; j++) {
                Relation relation = new Relation(j, i);
                relations[index] = relation;
                relationId.put(relation, index);
                index++;
            }
        }
    }
}
