package util;

public class Constants {
    private Constants(){}
    /**
     * MINIO CONFIGURATIONS
     */
    public static final String BUCKET_NAME = "casestudy";
    public static final String PERSON_FILE_PATH = "/person_data.csv";
    public static final String COUNTRY_FILE_PATH = "/country_data.csv";
    public static final String RESULT_FILE_PATH = "/result_data";

    /**
     * SPARK SESSION CONFIGURATIONS
     */
    public static final String SPARK_MASTER = "local";
    public static final String MINIO_URL = "http://minio:9000";
    public static final String MINIO_ACCESS_KEY = "minioadmin";
    public static final String MINIO_SECRET_KEY = "minioadmin";
    public static final String PATH_STYLE_ACCESS = "true";
    public static final String MAX_ATTEMPTS = "1";
    public static final String ESTABLISH_TIMEOUT = "5000";
    public static final String TIMEOUT = "10000";
    /**
     * EXCEL INFO
     */
    public static final String AGE = "age";
    public static final String BIRTHDAY = "birthday";
    public static final String COUNTRY = "country";
    public static final String COUNTRY_NAME = "country_name";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String NAMES = "names";
    public static final String BLOOD_TYPE = "blood_type";
}
