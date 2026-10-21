# Candidate Data Validator

A command-line Java tool that reads a CSV of political candidate records, checks it for data-quality problems, and writes a summary report plus a CSV of every issue it found.

I built it to practice object-oriented design and file handling in Java, inspired by the kind of routine accuracy and consistency checks I did as a data analyst intern. **The sample data is made up** (fictional names and positions), not real Vote Smart data.

## What it checks

| Rule | What it flags |
| --- | --- |
| `REQUIRED_FIELD` | Blank id, name, party, office, or state |
| `STATE_CODE` | State is not a valid two-letter US code (case-insensitive) |
| `OFFICE_TITLE` | Office is not one of the recognized titles |
| `DUPLICATE_ID` | An id that already appeared on an earlier row |
| `MALFORMED_ROW` | Row does not have exactly 6 columns |

## Run it

Requires JDK 17 or newer. No external libraries.

```bash
mkdir -p out/classes
javac -d out/classes src/validator/*.java test/SimpleTests.java
java -cp out/classes validator.Main data/sample_candidates.csv out/issues.csv
java -cp out/classes validator.SimpleTests
```

Example output on the included sample file (15 data rows):

```
Rows with issues: 6
Clean rows:       9 (60.0%)

Issues by rule:
  DUPLICATE_ID    1
  MALFORMED_ROW   1
  OFFICE_TITLE    1
  REQUIRED_FIELD  2
  STATE_CODE      1
```

## How it is organized

- `Candidate` holds one row of data.
- `ValidationRule` is an interface; each rule in `Rules` implements it, so a new check is one new class added to `Rules.defaults()`.
- `CsvLoader` parses the file, including quoted fields that contain commas, and reports malformed rows instead of crashing.
- `DataValidator` runs every rule on every record and tracks duplicate ids with a `HashMap`.
- `Report` prints the summary (sorted counts with a `TreeMap`) and writes the issue list sorted by row.
- `SimpleTests` has 8 plain-Java tests, no framework needed.

## Ideas to extend it

- Switch the tests to JUnit.
- Load the records into a SQL database through JDBC and run the same checks with queries.
- Add a rule for valid party names or a maximum field length.
- Add a `--format json` option for the report.
