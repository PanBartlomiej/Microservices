import csv
from influxdb_client import InfluxDBClient, Point, WriteOptions

token = "GuCoKr9rdRP7Bak9l_qyalr86Sbv4YURX9v6VgrlSuF7C4eTjTQhGmz-KMj1IXnFHOUJQgBP6lKK4Y_KBJ4WTw=="
org = "myorg"
bucket = "data_test"

client = InfluxDBClient(url="http://localhost:8086", token=token)

def write_csv_to_influx(file_path, measurement):
    with open(file_path) as csvfile:
        reader = csv.DictReader(csvfile)
        write_api = client.write_api(write_options=WriteOptions(batch_size=500, flush_interval=10_000))

        for row in reader:
            point = Point(measurement)
            point.time(row["timestamp"])
            for field, value in row.items():
                if field != "timestamp":
                    print(field,"  ",value,   "  ",row["timestamp"])
                    try:
                        point.field(field, float(value))
                    except ValueError:
                        point.field(field, value)
            write_api.write(bucket=bucket, org=org, record=point)
        write_api.__del__()
write_csv_to_influx("bandwidth_usage.csv", "bandwidth_usage")
write_csv_to_influx("packet_count.csv", "packet_count")
write_csv_to_influx("latency.csv", "latency")
write_csv_to_influx("device_temperature.csv", "device_temperature")
write_csv_to_influx("cpu_usage.csv", "cpu_usage")