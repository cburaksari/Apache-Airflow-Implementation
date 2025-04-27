Case Study

Bu proje, Java içinde Apache Spark kullanarak Minio üzerinden yüklenen person ve country excellerine göre analiz gerçekleştiren ve sonucunda filtreleme gerçekleştiren bir uygulamayı içerir.
Proje, kişisel verileri yaşlarına (30) ve kan gruplarına göre filtreler ve ülkelerle gruplar, ardından sonuçları minio depolama alanına kaydeder.

Kullanılan Gereksinimler

- Apache Spark

- Apache Airflow
- S3 Uyumlu Depolama Alanı - Minio
- Java JDK 17
- Maven

Kurulum

Java:
- Maven ile `mvn clean package` çalıştırılarak target dosyasında jar dosyasının yüklendiğine emin olunur.

Docker :
- `docker-compose up -d` ile docker containerları ayağa kaldırılır.
- Dockerfile içerisinde airflow için çalışması gereken birkaç command yer almaktadır. (java çalıştırılabilmesi için jdk kurulumu)

Minio:
- `minioadmin` username ve `minioadmin` şifresiyle 9001 portu üzerinden giriş yapılarak casestudy bucket'ı oluşturulur. İçine person_data.csv ve country_data.csv excel dosyaları manuel yüklenir.

Airflow:
- Klasördeki dags dosyasının içindeki spark_job.py' i 10 dakika aralıklarla çalıştırması beklenmektedir.
- 8080 portu üzerinden airflow arayüzüne ulaşılabilir.
- default kullanıcı ile bağlanılamıyor ise;
  - `docker ps` ile containerlar listelenir. Airflow webserver containerin id si alınır.
  - `docker exec -it airflow-webserver bash` ile container console'a girilir.
  - `airflow users create \
    --username admin \
    --firstname Admin \
    --lastname User \
    --role Admin \
    --email admin@example.com \
    --password admin` ile admin kullanıcısı oluşturularak airflow arayüzüne giriş yapılır.


Tanımlı Değişkenler

- SPARK_MASTER: Spark kümesinin master URL'si.
- MINIO_URL: MinIO veya S3 uyumlu depolama alanının URL'si.
- MINIO_ACCESS_KEY ve MINIO_SECRET_KEY: S3 uyumlu depolama alanına erişim sağlamak için gereken kimlik bilgileri.
- PATH_STYLE_ACCESS: S3 uyumlu depolama alanı için yol stili erişimi (genellikle "true").
- MAX_ATTEMPTS: Bağlantı denemeleri için maksimum sayı.
- ESTABLISH_TIMEOUT: Bağlantı kurma süresi (milisaniye cinsinden).
- TIMEOUT: Genel zaman aşımı süresi (milisaniye cinsinden).

Spark Yapılandırması

createSparkSession metodu, Spark oturumu başlatırken aşağıdaki yapılandırma ayarlarını kullanır:

- spark.master: Spark master URL'si.
- spark.hadoop.fs.s3a.endpoint: S3 uyumlu depolama alanının uç noktası.
- spark.hadoop.fs.s3a.access.key ve spark.hadoop.fs.s3a.secret.key: Erişim anahtarları ve gizli anahtar.
- spark.hadoop.fs.s3a.path.style.access: Yol stili erişimi.
- spark.hadoop.fs.s3a.connection.ssl.enabled: SSL bağlantısı.
- spark.hadoop.fs.s3a.attempts.maximum: Maksimum bağlantı deneme sayısı.
- spark.hadoop.fs.s3a.connection.establish.timeout: Bağlantı kurma zaman aşımı.
- spark.hadoop.fs.s3a.connection.timeout: Genel zaman aşımı.

Çalışma Şekli
- Spark ile yeni bir spark session oluşturulur.
- minio içinde casestudy bucket içerisine yüklenen person_data ve country_data excelleri spark ile okunur. (readFromBucket)
- İlgili person exceline yaş bilgisi kolon olarak doğum tarihi üzerinden hesaplama yapılarak eklenir.
- Kişilerden yaşı 30 dan büyük ve kan grubu "A+", "A-", "AB+", "AB-" olanlar filtrelenir.
- join groupby ve agg metotları kullanılarak country ve person arasında gruplama yapılır.
- sonuçlar spark üzerinden minio ya kaydedilir.
