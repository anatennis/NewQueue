package CSVSorter;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.Comparator;

/*
* Let us have a huge file (the file might be really huge, not get into the heap entirely).
* The file contains some records in CSV format. We need to be able to sort the file.
* It must be possible to provide a comparison strategy using different fields for comparison.
* */
public class Main {

    public static void main(String[] args) throws IOException {
        String inputFilename25 = "src/main/resources/ford_escort.csv";
        String comparatorField25 = "Price";
        String inputFilename25000 = "src/main/resources/hw_25000.csv";
        String comparatorField25000 = "Weight(Pounds)";
//        String inputFilename1000000 = "src/main/resources/1000000_SR.csv";
//        String inputFilename5000000 = "src/main/resources/5000000_SR.csv";
        String outputFilename = "src/main/resources/res.csv";

        Comparator<CSVRecord> comparator = Comparator.comparing(x -> x.get(comparatorField25000));
        CSVSorter csvSorter = new CSVSorter(comparator);

        long time  = System.currentTimeMillis();
        csvSorter.sort(inputFilename25000, outputFilename);
        System.out.println(System.currentTimeMillis() - time);

        time  = System.currentTimeMillis();csvSorter.setComparator(Comparator.comparing(x -> x.get(comparatorField25)));
        csvSorter.sort(inputFilename25, outputFilename);
        System.out.println(System.currentTimeMillis() - time);
    }

}
