package util;

import org.apache.spark.sql.*;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.udf;
import static util.Constants.*;

public class FilterUtil {

    private FilterUtil(){}

    private static final Logger logger = LoggerFactory.getLogger(FilterUtil.class);

    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    public static void saveResult(String bucketName, Dataset<Row> result) {
        result.write()
                .format("csv")
                .option("header", "true")
                .mode("overwrite")
                .save("s3a://" + bucketName + RESULT_FILE_PATH);
    }

    public static Dataset<Row> getPersonDataWithAge(Dataset<Row> persons) {
        return persons.withColumn(AGE, calculateAgeFromBirthday().apply(col(BIRTHDAY)));
    }

    public static Dataset<Row> groupPersonsWithCountries(Dataset<Row> countries, Dataset<Row> persons) {
        Dataset<Row> joinedData = persons.join(countries, persons.col(COUNTRY).equalTo(countries.col(COUNTRY)));
        RelationalGroupedDataset groupedData = joinedData
                .groupBy(countries.col(COUNTRY_NAME).alias(COUNTRY_NAME));

        return groupedData
                .agg(
                        functions.count("*").alias("count"),
                        functions.concat_ws(", ",
                                functions.collect_list(
                                        functions.concat_ws(" ", joinedData.col(FIRST_NAME), joinedData.col(LAST_NAME))
                                )
                        ).alias(NAMES));
    }

    public static Dataset<Row> filterPersonsOfAgeThirtyByTheirBloodType(Dataset<Row> persons) {
        return persons.filter(
                col(AGE).gt(30)
                        .and(col(BLOOD_TYPE).isin("A+", "A-", "AB+", "AB-"))
        );
    }

    public static Dataset<Row> readFromBucket(SparkSession spark, String bucketName, String fileName) {
        try {
            return spark.read()
                    .format("csv")
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .load("s3a://" + bucketName + fileName);
        } catch (Exception e) {
            logger.error("Failed to read file '{}' from bucket '{}'", fileName, bucketName, e);
            throw new RuntimeException("Error reading from bucket", e);
        }
    }

    public static UserDefinedFunction calculateAgeFromBirthday() {
        return udf((String birthday) -> {
            LocalDate birthDate = getParsedBirthDate(birthday);

            if (birthDate == null) {
                throw new IllegalArgumentException("Date format not recognized: " + birthday);
            }

            return Period.between(birthDate, LocalDate.now()).getYears();
        }, DataTypes.IntegerType);
    }

    private static LocalDate getParsedBirthDate(String birthday) {
        LocalDate birthDate = null;
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                birthDate = LocalDate.parse(birthday, formatter);
                break;
            } catch (DateTimeParseException e) {
                logger.error("Failed to parse birthDate from birthday : {}", e.getMessage());
            }
        }
        return birthDate;
    }
}
