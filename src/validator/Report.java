package validator;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Builds a console summary and writes the issue list to a CSV file. */
public final class Report {

    private Report() { }

    public static void printSummary(int totalRows, List<Issue> issues) {
        Set<Integer> badRows = new HashSet<>();
        Map<String, Integer> byRule = new TreeMap<>();
        for (Issue issue : issues) {
            badRows.add(issue.row());
            byRule.merge(issue.rule(), 1, Integer::sum);
        }

        int cleanRows = totalRows - badRows.size();
        double cleanPercent = totalRows == 0 ? 0 : 100.0 * cleanRows / totalRows;

        System.out.println("=== Candidate Data Validation Report ===");
        System.out.println("Data rows read:   " + totalRows);
        System.out.println("Rows with issues: " + badRows.size());
        System.out.printf("Clean rows:       %d (%.1f%%)%n", cleanRows, cleanPercent);
        System.out.println();
        System.out.println("Issues by rule:");
        if (byRule.isEmpty()) {
            System.out.println("  none");
        }
        for (Map.Entry<String, Integer> e : byRule.entrySet()) {
            System.out.printf("  %-15s %d%n", e.getKey(), e.getValue());
        }
    }

    public static void writeIssues(Path out, List<Issue> issues) throws IOException {
        if (out.getParent() != null) {
            Files.createDirectories(out.getParent());
        }
        List<Issue> sorted = issues.stream()
                .sorted(Comparator.comparingInt(Issue::row).thenComparing(Issue::rule))
                .toList();

        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            w.println("row,candidate_id,rule,message");
            for (Issue i : sorted) {
                w.println(i.row() + "," + quote(i.candidateId()) + "," + i.rule()
                        + "," + quote(i.message()));
            }
        }
    }

    private static String quote(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
