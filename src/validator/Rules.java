package validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** The built-in validation rules. */
public final class Rules {

    private static final Set<String> STATE_CODES = Set.of(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "DC");

    private static final Set<String> OFFICES = Set.of(
            "Governor", "Mayor", "State Senator", "State Representative",
            "U.S. Senator", "U.S. Representative");

    private Rules() { }

    /** Name, party, office and state must not be blank. */
    public static class RequiredFields implements ValidationRule {
        @Override
        public String name() { return "REQUIRED_FIELD"; }

        @Override
        public Optional<String> check(Candidate c) {
            List<String> missing = new ArrayList<>();
            if (isBlank(c.getName())) missing.add("name");
            if (isBlank(c.getParty())) missing.add("party");
            if (isBlank(c.getOffice())) missing.add("office");
            if (isBlank(c.getState())) missing.add("state");
            if (missing.isEmpty()) return Optional.empty();
            return Optional.of("Missing " + String.join(", ", missing));
        }
    }

    /** State must be a valid two-letter US state code. */
    public static class StateCode implements ValidationRule {
        @Override
        public String name() { return "STATE_CODE"; }

        @Override
        public Optional<String> check(Candidate c) {
            if (isBlank(c.getState())) return Optional.empty(); // reported by RequiredFields
            if (STATE_CODES.contains(c.getState().trim().toUpperCase())) return Optional.empty();
            return Optional.of("Unknown state code '" + c.getState() + "'");
        }
    }

    /** Office must be one of the known office titles. */
    public static class OfficeTitle implements ValidationRule {
        @Override
        public String name() { return "OFFICE_TITLE"; }

        @Override
        public Optional<String> check(Candidate c) {
            if (isBlank(c.getOffice())) return Optional.empty(); // reported by RequiredFields
            if (OFFICES.contains(c.getOffice().trim())) return Optional.empty();
            return Optional.of("Unrecognized office '" + c.getOffice() + "'");
        }
    }

    public static List<ValidationRule> defaults() {
        return List.of(new RequiredFields(), new StateCode(), new OfficeTitle());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
