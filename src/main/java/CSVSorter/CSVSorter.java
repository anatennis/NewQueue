package CSVSorter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class CSVSorter {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
    private final static int DEFAULT_MAX_RECORDS_PER_FILE = 80_000;
    private final static int DEFAULT_MAX_FILES_PER_MERGE = 50;
    private final int maxFilesPerMerge;
    private final int maxRecordsPerFile;
    private File output;
    private Comparator<CSVRecord> comparator;

    public CSVSorter(Comparator<CSVRecord> comparator) {
        this(comparator, DEFAULT_MAX_FILES_PER_MERGE, DEFAULT_MAX_RECORDS_PER_FILE);
    }

    public CSVSorter(Comparator<CSVRecord> comparator, int maxFilesPerMerge, int maxRecordsPerFile) {
        this.comparator = comparator;
        this.maxFilesPerMerge = maxFilesPerMerge;
        this.maxRecordsPerFile = maxRecordsPerFile;
    }

    public void setComparator(Comparator<CSVRecord> comparator) {
        this.comparator = comparator;
    }



    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void sort(String fileInputName, String fileOutputName) throws IOException {
        Objects.requireNonNull(fileInputName);
        Objects.requireNonNull(fileOutputName);
        output = new File(fileOutputName);

        List<File> fileList = new ArrayList<>();
        List<CSVRecord> recordsList = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new BufferedReader(new InputStreamReader(new FileInputStream(fileInputName))), CSV_FORMAT)) {
            CSVRecordBufferReader bufferReader = new CSVRecordBufferReader(parser);
            CSVRecord csvRecord;
            while ((csvRecord = bufferReader.get()) != null) {
                recordsList.add(csvRecord);
                if (recordsList.size() == maxRecordsPerFile) {
                    fileList.add(sortAndWriteToFile(recordsList));
                    recordsList = new ArrayList<>();
                }
            }
        }

        if (!recordsList.isEmpty()) {
            if (fileList.isEmpty()) {
                sortAndWriteToFile(recordsList, output);
            } else {
                fileList.add(sortAndWriteToFile(recordsList));
            }
        } else if (fileList.isEmpty()) {
            output.delete();
            output.createNewFile();
        }
        mergeAllFilesInOutputFile(fileList);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void mergeAllFilesInOutputFile(List<File> files) throws IOException {
        List<CSVRecordBufferReader> bufferReaders = new ArrayList<>();
        int index = files.size();
        CSVParser parser = null;
        try {
            for (int i = 0; i < files.size(); i++) {
                parser = new CSVParser(new BufferedReader(new InputStreamReader(new FileInputStream(files.get(i)))), CSV_FORMAT);
                bufferReaders.add(new CSVRecordBufferReader(parser));
                if (bufferReaders.size() == maxFilesPerMerge || i + 1 == index) {
                    index = bufferReaders.size() == maxFilesPerMerge ? files.size() : index + maxFilesPerMerge;
                    files.add(mergeFilesPool(bufferReaders, i == files.size() - 1));
                    bufferReaders = new ArrayList<>();
                }
            }
        } finally {
            if (parser != null) parser.close();
        }


        for (int i = 0; i < files.size() - 1; i++) {
            files.get(i).delete();
        }
    }

    private File mergeFilesPool(List<CSVRecordBufferReader> recordReaders, boolean isLastMerge) throws IOException {
        PriorityQueue<CSVRecordBufferReader> queue = new PriorityQueue<>(
                (o1, o2) -> comparator.compare(o1.peek(), o2.peek())
        );
        for (CSVRecordBufferReader reader : recordReaders) {
            if (reader.peek() != null) {
                queue.add(reader);
            }
        }
        File outputFile = isLastMerge ? output : File.createTempFile("csvsort", "");
        boolean printHeaders = true;
        try (BufferedWriter bufferedWriter =
                     new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
             CSVPrinter printer = new CSVPrinter(bufferedWriter, CSV_FORMAT)) {
            while (queue.size() > 0) {
                CSVRecordBufferReader recordReader = queue.poll();
                writeRecordToFile(recordReader.get(), printer, printHeaders);
                printHeaders = false;
                if (recordReader.peek() == null) {
                    recordReader.close();
                } else {
                    queue.add(recordReader); // add it back
                }
            }
        } finally {
            for (CSVRecordBufferReader reader : queue) {
                reader.close();
            }
        }

        return outputFile;
    }

    private File sortAndWriteToFile(List<CSVRecord> list, File file) throws IOException {
        if (list.size() > 5000) {
            list = list.parallelStream().sorted(comparator).collect(Collectors.toList());
        } else {
            list.sort(comparator);
        }

        writeToFile(list, file);
        return file;
    }

    private File sortAndWriteToFile(List<CSVRecord> list) throws IOException {
        return sortAndWriteToFile(list, File.createTempFile("csvsort", ""));
    }

    private void writeToFile(List<CSVRecord> csvRecords, File file) throws IOException {
        try (CSVPrinter printer = CSVFormat.DEFAULT.print(file, StandardCharsets.UTF_8)) {
            boolean printHeaders = true;
            for (CSVRecord record : csvRecords) {
                writeRecordToFile(record, printer, printHeaders);
                printHeaders = false;
            }
        }
    }

    private void writeRecordToFile(CSVRecord record, CSVPrinter printer, boolean printHeaders) throws IOException {
        List<String> headers = record.getParser().getHeaderNames();
        if (printHeaders && !headers.isEmpty()) {
            printer.printRecord(headers);
        }
        printer.printRecord(record);
    }

    private static class CSVRecordBufferReader {
        private final CSVParser parser;
        private CSVRecord curRecord;
        private final Iterator<CSVRecord> iterator;

        CSVRecordBufferReader(CSVParser parser) {
            iterator = parser.iterator();
            curRecord = iterator.hasNext() ? iterator.next() : null;
            this.parser = parser;
        }

        CSVRecord peek() {
            return curRecord;
        }

        CSVRecord get() {
            CSVRecord answer = curRecord;
            curRecord = iterator.hasNext() ? iterator.next() : null;
            return answer;
        }

        void close() throws IOException {
            parser.close();
        }
    }
}
