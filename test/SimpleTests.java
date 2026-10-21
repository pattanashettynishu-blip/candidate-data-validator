package validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Small self-contained tests (no external libraries needed).
 * Run with: java -cp out validator.SimpleTests
 */
public class SimpleTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        splitKeepsQuotedCommas();
        splitHandlesEscapedQuotes();
        requiredFieldsFlagsBlankName();
        stateRuleRejectsUnknownCode();
        stateRuleAcceptsLowercase();
        officeRuleRejectsUnknownTitle();
        duplicateIdsAreFlagged();
        malformedRowsAreReported();

        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    private static Candidate candidate(int row, String id, String name, String party,
                                       String office, String state) {
        return new Candidate(row, id, name, party, office, state, "");
    }

    private static void splitKeepsQuotedCommas() {
        List<String> f = CsvLoader.splitLine("a,\"b,c\",d");
        check("split keeps quoted commas", f.size() == 3 && f.get(1).equals("b,c"));
    }

    private static void splitHandlesEscapedQuotes() {
        List<String> f = CsvLoader.splitLine("a,\"say \"\"hi\"\"\",c");
        check("split handles escaped quotes", f.size() == 3 && f.get(1).equals("say \"hi\""));
    }

    private static void requiredFieldsFlagsBlankName() {
        Candidate c = candidate(2, "C1", "", "Green", "Mayor", "MN");
        check("blank name flagged", new Rules.RequiredFields().check(c).isPresent());
    }

    private static void stateRuleRejectsUnknownCode() {
        Candidate c = candidate(2, "C1", "A", "Green", "Mayor", "ZZ");
        check("unknown state rejected", new Rules.StateCode().check(c).isPresent());
    }

    private static void stateRuleAcceptsLowercase() {
        Candidate c = candidate(2, "C1", "A", "Green", "Mayor", "mn");
        check("lowercase state accepted", new Rules.StateCode().check(c).isEmpty());
    }

    private static void officeRuleRejectsUnknownTitle() {
        Candidate c = candidate(2, "C1", "A", "Green", "Chief Executive", "MN");
        check("unknown office rejected", new Rules.OfficeTitle().check(c).isPresent());
    }

    private static void duplicateIdsAreFlagged() {
        List<Candidate> list = List.of(
                candidate(2, "C1", "A", "Green", "Mayor", "MN"),
                candidate(3, "C1", "B", "Green", "Mayor", "MN"));
        List<Issue> issues = new DataValidator(Rules.defaults()).validate(list);
        boolean found = issues.stream().anyMatch(i -> i.rule().equals("DUPLICATE_ID") && i.row() == 3);
        check("duplicate id flagged on second row", found);
    }

    private static void malformedRowsAreReported() throws Exception {
        Path temp = Files.createTempFile("candidates", ".csv");
        Files.writeString(temp, "id,name,party,office,state,issue_position\nC1,A,Green\n");
        List<Issue> issues = new ArrayList<>();
        List<Candidate> loaded = CsvLoader.load(temp, issues);
        Files.deleteIfExists(temp);
        check("malformed row reported and skipped",
                loaded.isEmpty() && issues.size() == 1 && issues.get(0).rule().equals("MALFORMED_ROW"));
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }
}
