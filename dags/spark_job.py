from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2024, 1, 1),
    'retries': 1,
    'retry_delay': timedelta(minutes=1),
}

dag = DAG(
    'spark_job_scheduler',
    default_args=default_args,
    description='Run Spark job for demographic analysis',
    schedule_interval='*/10 * * * *',  # Every 10 minutes
    catchup=False,
)

spark_submit_cmd = """
java --add-exports java.base/sun.nio.ch=ALL-UNNAMED -jar /opt/airflow/target/casestudy-0.0.1-SNAPSHOT.jar
"""

run_spark_task = BashOperator(
    task_id='run_spark_task',
    bash_command=spark_submit_cmd,
    cwd='/opt/airflow/target',
    dag=dag,
)

run_spark_task
