package validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Usage: java validator.Main <input.csv> [issues-output.csv] */
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java validator.Main <input.csv> [issues-output.csv]");
            System.exit(1);
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args.length > 1 ? args[1] : "out/issues.csv");

        if (!Files.exists(input)) {
            System.err.println("Input file not found: " + input);
            System.exit(1);
        }

        try {
            List<Issue> issues = new ArrayList<>();
            List<Candidate> candidates = CsvLoader.load(input, issues);
            int totalRows = candidates.size() + countMalformed(issues);

            DataValidator validator = new DataValidator(Rules.defaults());
            issues.addAll(validator.validate(candidates));

            Report.printSummary(totalRows, issues);
            Report.writeIssues(output, issues);
            System.out.println();
            System.out.println("Issue details written to " + output);
        } catch (IOException e) {
            System.err.println("Could not read or write a file: " + e.getMessage());
            System.exit(2);
        }
    }

    private static int countMalformed(List<Issue> issues) {
        int count = 0;
        for (Issue i : issues) {
            if (i.rule().equals("MALFORMED_ROW")) count++;
        }
        return count;
    }
}
