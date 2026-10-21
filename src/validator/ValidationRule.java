package validator;

import java.util.Optional;

/** A check that can be run on one candidate. Returns a message if the record fails. */
public interface ValidationRule {
    String name();

    Optional<String> check(Candidate candidate);
}
