package org.churk.telegrampibot.utility;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CSVLoader {
    public List<List<String>> readFromCSV(String fileName) {
        List<List<String>> records = new ArrayList<>();
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(parser).build()) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            log.error("File not found", e);
        } catch (IOException e) {
            log.error("IO exception", e);
        } catch (CsvValidationException e) {
            log.error("CSV validation exception", e);
        }
        return records;
    }
}
