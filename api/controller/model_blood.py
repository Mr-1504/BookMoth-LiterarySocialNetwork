import cv2
import torch
import numpy as np
from tensorflow.keras.models import load_model
from ultralytics import YOLO
from flask import request, send_file, jsonify
import os
from werkzeug.utils import secure_filename
from post_api import app


yolo_model = YOLO("../model/my_trained_model.pt")
vgg_model = load_model("E:\Software\Programing_Projects\Github_Repo\BookMoth-LiterarySocialNetwork\\api\model\hyper_blood_3class.h5")


# Thư mục tạm để lưu file
UPLOAD_FOLDER = 'uploads'
OUTPUT_FOLDER = 'outputs'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(OUTPUT_FOLDER, exist_ok=True)

def detect_blood_yolo(img):
    img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    results = yolo_model.predict(img_rgb, conf=0.3, verbose=False)

    boxes = []
    labels = []
    for result in results:
        for box in result.boxes:
            x_min, y_min, x_max, y_max = map(int, box.xyxy[0])
            class_id = int(box.cls[0])
            label = yolo_model.names[class_id]
            boxes.append([x_min, y_min, x_max, y_max])
            labels.append(label)

    return boxes, labels

def draw_yolo_boxes(img, boxes, labels):
    img_with_boxes = img.copy()
    for box, label in zip(boxes, labels):
        x_min, y_min, x_max, y_max = box
        color = (0, 255, 0) if label in ['blood'] else (255, 0, 0)
        cv2.rectangle(img_with_boxes, (x_min, y_min), (x_max, y_max), color, 2)
        cv2.putText(img_with_boxes, label, (x_min, y_min - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, color, 2)
    return img_with_boxes

def classify_blood_vgg16(img, box):
    x_min, y_min, x_max, y_max = box
    roi = img[y_min:y_max, x_min:x_max]
    roi_resized = cv2.resize(roi, (224, 224), interpolation=cv2.INTER_AREA)
    roi_input = np.expand_dims(roi_resized / 255.0, axis=0)
    prediction = vgg_model.predict(roi_input, verbose=0)
    label = 'blood' if prediction[0][0] > 0.5 else 'noblood'
    return label, prediction[0][0]

def process_blood_region(img, box):
    x_min, y_min, x_max, y_max = box
    roi = img[y_min:y_max, x_min:x_max]
    gray_roi = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
    gray_roi = cv2.cvtColor(gray_roi, cv2.COLOR_GRAY2BGR)
    img[y_min:y_max, x_min:x_max] = gray_roi
    return img

def process_image(image_path, id, only_yolo=False):
    img = cv2.imread(image_path)
    if img is None:
        raise ValueError("Không thể đọc ảnh từ đường dẫn cung cấp.")

    # Bước 1: Phát hiện bằng YOLO
    boxes, yolo_labels = detect_blood_yolo(img)
    img_with_boxes = draw_yolo_boxes(img, boxes, yolo_labels)
    cv2.imwrite(os.path.join(OUTPUT_FOLDER, id+'.jpg'), img_with_boxes)

    # Kiểm tra checkBlood
    checkBlood = 0
    if any(label in ['blood', 'flood'] for label in yolo_labels):
        checkBlood = 1

    # Nếu chỉ chạy YOLO (cho endpoint /check-blood)
    if only_yolo:
        return None, checkBlood, boxes, yolo_labels  # Trả về None cho output_path

    # Bước 2: Xử lý VGG16 nếu có máu
    if checkBlood:
        has_high_confidence_blood = False
        # Kiểm tra xem có vùng nào VGG confidence > 0.8 không
        for box, yolo_label in zip(boxes, yolo_labels):
            if yolo_label in ['blood', 'flood']:
                vgg_label, vgg_confidence = classify_blood_vgg16(img, box)
                if vgg_label == 'blood' and vgg_confidence > 0.9:
                    has_high_confidence_blood = True
                    break

        # Nếu có confidence > 0.8, đổi tất cả các box thành blood và xử lý
        if has_high_confidence_blood:
            # Cập nhật tất cả labels thành 'blood'
            yolo_labels = ['blood'] * len(boxes)
            # Xử lý tất cả các vùng
            for box in boxes:

                img = process_blood_region(img, box)
        else:
            # Nếu không có confidence > 0.8, xử lý bình thường
            for box, yolo_label in zip(boxes, yolo_labels):
                if yolo_label in ['blood', 'flood']:
                    vgg_label, vgg_confidence = classify_blood_vgg16(img, box)
                    if vgg_label == 'blood' and vgg_confidence > 0.7:
                        img = process_blood_region(img, box)


    output_path = os.path.join(OUTPUT_FOLDER, id + '.jpg')
    cv2.imwrite(output_path, img)
    return output_path, checkBlood, boxes, yolo_labels

# Endpoint để kiểm tra YOLO và trả checkBlood
@app.route('/check-blood/<int:id>', methods=['POST'])
def check_blood_api(id):
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'Không có file ảnh trong request'}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'Không có file được chọn'}), 400

        filename = secure_filename(file.filename)
        input_path = os.path.join(UPLOAD_FOLDER, filename)
        file.save(input_path)

        # Chỉ chạy YOLO
        _, checkBlood, _, _ = process_image(input_path, str(id), only_yolo=True)

        # Trả về JSON với checkBlood
        return jsonify({
            'checkBlood': checkBlood,
            'message': 'Có vùng máu, đang xử lý...' if checkBlood else 'Không phát hiện máu.'
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500

    finally:
        if os.path.exists(input_path):
            os.remove(input_path)

# Endpoint để xử lý toàn bộ (YOLO + VGG16)
@app.route('/process-image/<int:id>', methods=['POST'])
def process_image_api(id):
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'Không có file ảnh trong request'}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'Không có file được chọn'}), 400

        filename = secure_filename(file.filename)
        input_path = os.path.join(UPLOAD_FOLDER, filename)
        file.save(input_path)

        # Chạy toàn bộ quá trình
        output_path, _, _, _ = process_image(input_path, str(id), only_yolo=False)

        return send_file(output_path, mimetype='image/jpeg', as_attachment=True, download_name=f"{id}.jpg")

    except Exception as e:
        return jsonify({'error': str(e)}), 500

    finally:
        if os.path.exists(input_path):
            os.remove(input_path)

