package validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Runs every rule on every candidate and also flags duplicate IDs. */
public class DataValidator {

    private final List<ValidationRule> rules;

    public DataValidator(List<ValidationRule> rules) {
        this.rules = rules;
    }

    public List<Issue> validate(List<Candidate> candidates) {
        List<Issue> issues = new ArrayList<>();
        Map<String, Integer> firstRowById = new HashMap<>();

        for (Candidate c : candidates) {
            for (ValidationRule rule : rules) {
                Optional<String> problem = rule.check(c);
                problem.ifPresent(msg ->
                        issues.add(new Issue(c.getRow(), c.getId(), rule.name(), msg)));
            }

            if (c.getId().isEmpty()) {
                issues.add(new Issue(c.getRow(), "(blank)", "REQUIRED_FIELD", "Missing id"));
            } else if (firstRowById.containsKey(c.getId())) {
                issues.add(new Issue(c.getRow(), c.getId(), "DUPLICATE_ID",
                        "Same id as row " + firstRowById.get(c.getId())));
            } else {
                firstRowById.put(c.getId(), c.getRow());
            }
        }
        return issues;
    }
}
