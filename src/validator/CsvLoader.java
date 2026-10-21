package validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads candidate rows from a CSV file with the columns:
 *  id,name,party,office,state,issue_position */
public final class CsvLoader {

    private static final int EXPECTED_COLUMNS = 6;

    private CsvLoader() { }

    /**
     * Loads candidates from the file. Rows with the wrong number of columns are not
     * loaded; a MALFORMED_ROW issue is added to {@code issues} for each one.
     */
    public static List<Candidate> load(Path path, List<Issue> issues) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<Candidate> candidates = new ArrayList<>();

        // Line 1 is the header, so data rows are numbered from 2 to match a spreadsheet.
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;

            int rowNumber = i + 1;
            List<String> fields = splitLine(line);
            if (fields.size() != EXPECTED_COLUMNS) {
                issues.add(new Issue(rowNumber, "?", "MALFORMED_ROW",
                        "Expected " + EXPECTED_COLUMNS + " columns but found " + fields.size()));
                continue;
            }
            candidates.add(new Candidate(rowNumber,
                    fields.get(0).trim(), fields.get(1).trim(), fields.get(2).trim(),
                    fields.get(3).trim(), fields.get(4).trim(), fields.get(5).trim()));
        }
        return candidates;
    }

    /** Splits one CSV line, keeping commas that appear inside double quotes. */
    static List<String> splitLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); // escaped quote
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
