from datetime import datetime

from post_api import app
from model.user_model import user_model
from flask import request, send_file

obj = user_model()
@app.route('/user/signup')
def signup():
    return obj.user_login()
@app.route('/user/signin', methods=['POST'])
def signin():
    return obj.user_signin(request.form)
@app.route('/user/update', methods=['PUT'])
def update():
    return obj.user_update(request.form)
@app.route('/user/delete/<string:id>', methods=['DELETE'])
def delete(id):
    return obj.user_delete(id)

@app.route('/user/patch/<string:id>', methods=['PATCH'])
def tsach_patch_controler(id):
    return obj.tsach_patch_model(request.form,id)

@app.route('/user/getall/limit/<limit>/page/<page>', methods=['PATCH'])
def tsach_pagination_controler(limit, page):
    return obj.tsach_pagination_model(limit,page)

@app.route('/user/<string:uid>/upload/avatar', methods=['PUT'])
def upload_avatar(uid):
    file = request.files['avatar']
    uniqueFileName = str(datetime.now().timestamp()).replace(".", "")
    fileNameSplit = file.filename.split(".")
    extension = fileNameSplit[len(fileNameSplit) - 1]
    finalFilePath = f"uploads/{uniqueFileName}.{extension}"
    file.save(finalFilePath)
    return obj.upload_img_model(uid,finalFilePath)
@app.route('/user/<filename>')
def user_avatar(filename):
    return send_file(f'uploads/{filename}')