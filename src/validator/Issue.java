package validator;

/** A single data-quality problem found in the input file. */
public record Issue(int row, String candidateId, String rule, String message) {
}
