package data.boss.casestudy;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static util.Constants.*;
import static util.FilterUtil.*;

@SpringBootApplication
public class CasestudyApplication {

    public static void main(String[] args) {
        SparkSession spark = createSparkSession();
        String bucketName = BUCKET_NAME;

        Dataset<Row> persons = readFromBucket(spark, bucketName, PERSON_FILE_PATH);
        Dataset<Row> personDataWithAge = getPersonDataWithAge(persons);
        Dataset<Row> countries = readFromBucket(spark, bucketName, COUNTRY_FILE_PATH);

        Dataset<Row> filteredPersons = filterPersonsOfAgeThirtyByTheirBloodType(personDataWithAge);
        Dataset<Row> result = groupPersonsWithCountries(countries, filteredPersons);

        saveResult(bucketName, result);

        spark.stop();
    }

    private static SparkSession createSparkSession() {
        return SparkSession.builder()
                .appName("CasestudyApplication")
                .config("spark.master", SPARK_MASTER)
                .config("spark.hadoop.fs.s3a.endpoint", MINIO_URL)
                .config("spark.hadoop.fs.s3a.access.key", MINIO_ACCESS_KEY)
                .config("spark.hadoop.fs.s3a.secret.key", MINIO_SECRET_KEY)
                .config("spark.hadoop.fs.s3a.path.style.access", PATH_STYLE_ACCESS)
                .config("spark.hadoop.fs.s3a.connection.ssl.enabled", PATH_STYLE_ACCESS)
                .config("spark.hadoop.fs.s3a.attempts.maximum", MAX_ATTEMPTS)
                .config("spark.hadoop.fs.s3a.connection.establish.timeout", ESTABLISH_TIMEOUT)
                .config("spark.hadoop.fs.s3a.connection.timeout", TIMEOUT)
                .getOrCreate();
    }
}
