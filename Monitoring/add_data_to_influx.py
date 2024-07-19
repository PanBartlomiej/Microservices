import csv
from influxdb_client import InfluxDBClient, Point, WriteOptions

token = "HKdAw-OMsZ_i26dTudKAXXM2ELWq0Jr64ocNIAOZQY_MM6_6w7kn1V-5K5J-xsqwfc3N-2C_Wwz9tEJDGzUPyQ=="
org = "myorg"
bucket = "mybucket"

client = InfluxDBClient(url="http://localhost:8086", token=token)

def write_csv_to_influx(file_path, measurement, tag_columns):
    with open(file_path) as csvfile:
        reader = csv.DictReader(csvfile)
        write_api = client.write_api(write_options=WriteOptions(batch_size=500, flush_interval=10_000))

        for row in reader:
            point = Point(measurement)
            point.time(row["timestamp"])

            for tag in tag_columns:
                point.tag(tag, row[tag])

            for field, value in row.items():
                if field != "timestamp" and field not in tag_columns:
                    try:
                        point.field(field, float(value))
                    except ValueError:
                        point.field(field, value)

            write_api.write(bucket=bucket, org=org, record=point)
        write_api.__del__()

# Przykłady wywołania funkcji z odpowiednimi tagami
write_csv_to_influx("bandwidth_usage.csv", "bandwidth_usage", ["device"])
write_csv_to_influx("packet_count.csv", "packet_count", ["device"])
write_csv_to_influx("latency.csv", "latency", ["device"])
write_csv_to_influx("device_temperature.csv", "device_temperature", ["device"])
write_csv_to_influx("cpu_usage.csv", "cpu_usage", ["device"])
