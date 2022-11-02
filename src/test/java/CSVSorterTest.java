import CSVSorter.CSVSorter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CSVSorterTest {
    private final String outputFilename = "src/main/resources/res.csv";

    @Test
    void testSmallFileSort() throws IOException {
        String addressesFile = "src/main/resources/addresses.csv";
        String number = "number";
        String name = "name";

        Comparator<CSVRecord> comparator = (o1, o2) -> {
            if (o1.get(number).compareTo(o2.get(number)) == 0) {
                return o1.get(name).compareTo(o2.get(name));
            }
            return o1.get(number).compareTo(o2.get(number));
        };
        CSVSorter csvSorter = new CSVSorter(comparator, 2, 2);

        csvSorter.sort(addressesFile, outputFilename);
        List<CSVRecord> csvRecords = CSVSorter.filetoList(outputFilename);
        Assertions.assertEquals(csvRecords.size(), 6);

        List<CSVRecord> sortedRecs = csvRecords.stream().sorted(comparator).collect(Collectors.toList());
        Assertions.assertEquals(csvRecords, sortedRecs);
    }

    @Test
    void testBiggerFileSort() throws IOException {
        String addressesFile = "src/main/resources/hw_25000.csv";
        String height = "Height(Inches)";

        Comparator<CSVRecord> comparator = (o1, o2) -> {
            Double d1 = Double.parseDouble(o1.get(height));
            Double d2 = Double.parseDouble(o2.get(height));
            return d1.compareTo(d2);
        };
        CSVSorter csvSorter = new CSVSorter(comparator, 10, 456);

        csvSorter.sort(addressesFile, outputFilename);
        List<CSVRecord> csvRecords = CSVSorter.filetoList(outputFilename);
        Assertions.assertEquals(25_000, csvRecords.size());

        List<CSVRecord> sortedRecs = csvRecords.stream().sorted(comparator).collect(Collectors.toList());
        Assertions.assertEquals(csvRecords, sortedRecs);

        csvSorter.setMaxFilesPerMerge(20);
        csvSorter.setMaxRecordsPerFile(1000);
        csvSorter.sort(addressesFile, outputFilename);
        csvRecords = CSVSorter.filetoList(outputFilename);
        Assertions.assertEquals(25_000, csvRecords.size());
        sortedRecs = csvRecords.stream().sorted(comparator).collect(Collectors.toList());
        Assertions.assertEquals(csvRecords, sortedRecs);
    }

    @Test
    void testEmptyFileSort() throws IOException {
        String addressesFile = "src/main/resources/empty.csv";
        String number = "number";
        String name = "name";

        Comparator<CSVRecord> comparator = (o1, o2) -> {
            if (o1.get(number).compareTo(o2.get(number)) == 0) {
                return o1.get(name).compareTo(o2.get(name));
            }
            return o1.get(number).compareTo(o2.get(number));
        };
        CSVSorter csvSorter = new CSVSorter(comparator);

        csvSorter.sort(addressesFile, outputFilename);
        List<CSVRecord> csvRecords = CSVSorter.filetoList(outputFilename);
        Assertions.assertEquals(csvRecords.size(), 0);
    }

    @Test
    void testOneRecFileSort() throws IOException {
        String addressesFile = "src/main/resources/one_rec.csv";
        String number = "number";
        String name = "name";

        Comparator<CSVRecord> comparator = (o1, o2) -> {
            if (o1.get(number).compareTo(o2.get(number)) == 0) {
                return o1.get(name).compareTo(o2.get(name));
            }
            return o1.get(number).compareTo(o2.get(number));
        };
        CSVSorter csvSorter = new CSVSorter(comparator);

        csvSorter.sort(addressesFile, outputFilename);
        List<CSVRecord> csvRecords = CSVSorter.filetoList(outputFilename);
        Assertions.assertEquals(csvRecords.size(), 1);
        Assertions.assertEquals(csvRecords.get(0).get(number), "35");
    }
}
