import csv
from datetime import datetime, timedelta
import random

# Konfiguracja czasu początkowego
start_time = datetime(2024, 7, 4, 0, 0, 0)

# Lista nazw urządzeń
devices = ['router1', 'router2', 'Switch12']

# Funkcja do zapisu danych do pliku CSV
def save_to_csv(filename, header, data):
    with open(filename, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(header)
        writer.writerows(data)

# 1. Pasmo przepustowości (Bandwidth Usage)
bandwidth_usage_data = []
current_time = start_time
for _ in range(1000):
    for device in devices:
        bandwidth_usage_data.append([
            current_time.isoformat() + "Z",
            device,
            'eth0',
            round(random.uniform(18, 25), 1),
            round(random.uniform(5, 7), 1)
        ])
    current_time += timedelta(minutes=1)
save_to_csv('bandwidth_usage.csv', ['timestamp', 'device', 'interface', 'download_mbps', 'upload_mbps'], bandwidth_usage_data)

# 2. Liczba pakietów (Packet Count)
packet_count_data = []
current_time = start_time
for _ in range(1000):
    for device in devices:
        packet_count_data.append([
            current_time.isoformat() + "Z",
            device,
            'eth0',
            random.randint(1400, 1700),
            random.randint(1100, 1400)
        ])
    current_time += timedelta(minutes=1)
save_to_csv('packet_count.csv', ['timestamp', 'device', 'interface', 'received_packets', 'sent_packets'], packet_count_data)

# 3. Opóźnienia (Latency)
latency_data = []
current_time = start_time
for _ in range(1000):
    for device in devices:
        latency_data.append([
            current_time.isoformat() + "Z",
            device,
            round(random.uniform(14, 16), 1)
        ])
    current_time += timedelta(minutes=1)
save_to_csv('latency.csv', ['timestamp', 'device',  'latency_ms'], latency_data)

# 4. Temperatura urządzenia (Device Temperature)
device_temperature_data = []
current_time = start_time
for _ in range(1000):
    for device in devices:
        device_temperature_data.append([
            current_time.isoformat() + "Z",
            device,
            round(random.uniform(44, 46), 1)
        ])
    current_time += timedelta(minutes=1)
save_to_csv('device_temperature.csv', ['timestamp', 'device', 'temperature_celsius'], device_temperature_data)

# 5. Użycie procesora (CPU Usage)
cpu_usage_data = []
current_time = start_time
for _ in range(1000):
    for device in devices:
        cpu_usage_data.append([
            current_time.isoformat() + "Z",
            device,
            round(random.uniform(30, 35), 1)
        ])
    current_time += timedelta(minutes=1)
save_to_csv('cpu_usage.csv', ['timestamp', 'device', 'cpu_usage_percent'], cpu_usage_data)
