package validator;

/** One candidate record read from a CSV row. */
public class Candidate {
    private final int row;
    private final String id;
    private final String name;
    private final String party;
    private final String office;
    private final String state;
    private final String issuePosition;

    public Candidate(int row, String id, String name, String party,
                     String office, String state, String issuePosition) {
        this.row = row;
        this.id = id;
        this.name = name;
        this.party = party;
        this.office = office;
        this.state = state;
        this.issuePosition = issuePosition;
    }

    public int getRow() { return row; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getParty() { return party; }
    public String getOffice() { return office; }
    public String getState() { return state; }
    public String getIssuePosition() { return issuePosition; }

    @Override
    public String toString() {
        return "Candidate{row=" + row + ", id=" + id + ", name=" + name + "}";
    }
}
