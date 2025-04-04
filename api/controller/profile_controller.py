import os
from sqlalchemy import create_engine
from werkzeug.security import safe_join
from post_api import app
from model.profile_model import ProfileModel
from flask import request, send_file, jsonify

# Kết nối database
server = r'.\SQLEXPRESS'
database = 'BookMoth'
driver = 'ODBC Driver 17 for SQL Server'
conn_str = f'mssql+pyodbc://@{server}/{database}?trusted_connection=yes&driver={driver}'
engine = create_engine(conn_str)

# Khởi tạo model với engine
profile_model = ProfileModel(engine)

@app.route('/profile/<int:id>', methods=['GET'])
def getProfileId(id):
    return profile_model.getProfileID(id)

@app.route('/avatar_profile/<int:id>', methods=['GET'])
def get_avatar(id):
    """ API lấy ảnh avatar của người dùng theo ID """
    avatar_path = profile_model.getAvatarPath(id)

    if avatar_path:  # Đường dẫn đầy đủ
        return send_file(f'uploads/{avatar_path}')  # Gửi file về client
    else:
        return jsonify({"message": "❌ Không tìm thấy ảnh đại diện!"}), 404

@app.route('/profile_avata/<int:id>', methods=['GET'])
def get_profile_avatar(id):
    try:
        avatar_url = profile_model.getUrlAvatar(id)
        if avatar_url:
            return jsonify({"data": avatar_url})  # Đảm bảo trả về JSON hợp lệ
        else:
            return jsonify({"error": "Không tìm thấy ảnh đại diện!"}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/pinbooks', methods = ['GET'])
def get_pinbooks():
    """API lấy danh sách các sách để gắn vào bài đăng"""
    return profile_model.getBooks()

@app.route('/books/<int:id>', methods=['GET'])
def get_book_by_id(id):
    return profile_model.getBookId(id)

@app.route('/covers/<path:filename>', methods=['GET'])
def get_cover(filename):
    """Lấy ảnh sách từ thư mục"""
    folder_path = "covers"
    file_path = os.path.join(folder_path, filename)
    if os.path.isfile(file_path):
        return send_file(file_path)
    else:
        return jsonify({"message": "❌ Không tìm thấy ảnh!"}), 404

@app.route('/idfollowers/<int:id>', methods=['GET'])
def get_followers(id):
    return profile_model.getFollowedOrPurchasedProfiles(id)
