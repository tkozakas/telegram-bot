package org.churk.telegrambot.utility;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.LoaderProperties;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.repository.FactRepository;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FactLoader {
    private static final String FILE_PATH = "src/main/resources/facts.csv";
    private final LoaderProperties loaderProperties;
    private final FactRepository factRepository;

    public FactLoader(LoaderProperties loaderProperties, FactRepository factRepository) {
        this.loaderProperties = loaderProperties;
        this.factRepository = factRepository;
    }

    public void loadFacts() {
        if (!loaderProperties.isLoadFacts()) {
            return;
        }
        Map<String, String> columnMapping = Map.of(
                "comment", "comment",
                "isHate", "isHate"
        );

        HeaderColumnNameTranslateMappingStrategy<Fact> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
        strategy.setType(Fact.class);
        strategy.setColumnMapping(columnMapping);
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(FILE_PATH)).withCSVParser(parser).build()) {
            CsvToBean<Fact> csvToBean = new CsvToBeanBuilder<Fact>(csvReader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Fact> factList = csvToBean.parse();
            factList.forEach(fact -> fact.setFactId(UUID.randomUUID()));
            factRepository.saveAll(factList);
        } catch (FileNotFoundException e) {
            log.error("File not found: " + FILE_PATH, e);
        } catch (IOException e) {
            log.error("IO Exception while reading the file: " + FILE_PATH, e);
        } catch (Exception e) {
            log.error("Error while processing CSV file: " + FILE_PATH, e);
        }
    }
}
