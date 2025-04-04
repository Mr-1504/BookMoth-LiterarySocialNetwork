"""
    LibAPI: API điều phối hoạt động của Thư Viện và Nội Dung Văn Học
    Yêu cầu: python 3.11, thư viện Flask, pyodbc, gunicorn, MarkItDown(microsoft)
        pip install Flask
        pip install pyodbc
        pip install pillow
        pip install markitdown[docx]

    Khởi chạy:

    **[Docker CLI]**: gunicorn -w <số workers / 4> -b localhost:<port / 1445> libapi:LibAPI

    **[Windows VPS]**: waitress libapi:LibAPI

    **[localdev]**: just use Flask basic
"""
import datetime
import hashlib
from io import BytesIO
from enum import Enum
from flask import Flask, jsonify, request, json, send_file
import pyodbc
from PIL import Image
import os
import random
import string
import subprocess
# import waitress

import jwt

secret_key = "1k3akdh27dh4idj1gd4f82324eergfwe"

decoded = jwt.decode(
    token,
    key=secte,
    algorithms=["HS256"],
    audience="expected-audience"
)

class SQLCons:
    IP = ".\\SQLEXPRESS"
    DB_NAME = "BookMoth"
    UID = "sa"
    PWD = "MyStr0ngP@ssword"


class WorksConst:
    _TABLE_NAME = "Works"
    _id = "work_id"
    pid = "profile_id"
    title = "title"
    pdate = "post_date"
    desc = "description"
    price = "price"
    view = "view_count"
    img = "cover_url"


class ChapsConst:
    _TABLE_NAME = "Chapters"
    _id = "chapter_id"
    wid = "work_id"
    title = "title"
    pdate = "post_date"
    body = "content_url"


class CatesConst:
    _TABLE_NAME = "Categories"
    _id = "category_id"
    name = "tag"


class TagConst:
    _TABLE_NAME = "Worktags"
    wid = "work_id"
    tid = "category_id"


class OwnsConst:
    _TABLE_NAME = "OwnershipRecord"
    wid = "work_id"
    aid = "account_id"
    exp = "expiry_date"


class AccsConst:
    _TABLE_NAME = "Accounts"
    aid = "account_id"
    email = "email"
    pwd = "password"
    salt = "salt"
    type = "account_type"


class ProfsConst:
    _TABLE_NAME = "Profiles"
    pid = "profile_id"
    aid = "account_id"
    fname = "first_name"
    lname = "last_name"
    name = "username"
    avt = "avatar"
    cover = "coverphoto"
    type = "identifier"


class TokensConst:
    _TABLE_NAME = "RefreshTokens"
    tkid = "token_id"
    aid = "account_id"
    token = "token"
    exp = "expiry_date"
    created = "created_at"
    revoked = "revoked_at"


class JoinConst:
    author = "author_name"
    follows = "follow_count"
    lastupdate = "last_update"


class ResponseDict(Enum):
    EXPIRED_SESSION = {"error": "Session expired. Re-login and try again"}
    EXPIRED_LICENSE = {"error": "License expired. Re-purchase or contact the author / Bookmoth CS if you think this is a mistake"}
    AUTH_WRONGFORMAT = {"error": "Authorization key missing or invalid"}
    NULL_TOKEN = {"error": "Can't identify token in database"}
    NULL_WORK = {"error": "Can't find work in database"}
    NULL_RESULT = {"error": "Can't find this info inside database"}
    NO_PERMISSION = {"error": "Permission denied"}
    PROFILE_NOPERMISSION = {"error": "Account doesn't have administrator permission to this profile"}
    API_PERMISSION = {"error": "Out of API's range of permission. Look for other available API"}
    PARAMETER_REQUIRED = {"error": "Missing / Wrong formating of parameter(s). Please check again and retry"}
    REQUEST_REQUIRED = {"error": "This action mismatch with the provided data structure. Check your request again"}
    FILE_REQUIRED = {"error": "Required file not found. Please check again"}
    FILE_INVALIDNAME = {"error": "File name is ambiguous"}
    FILE_NOTSUPPORTED = {"error": "Not supported this file type"}
    FILE_EXISTCORRUPTED = {"error": "An error make the current file corrupted. Please upload a replacement file"}
    FILE_NULL = {"error": "Can't find file in database"}
    FILE_NOCHANGE = {"warning": "No change from previous fetch"}
    FILE_UNEXPECTED = {"error": "Unexpected error while handling file"}
    SQL_INTERNAL_ERROR = {"error": "Internal error of database. Please contact Bookmoth CS"}
    SUCCESS = {"success": "Success"}

TEMP_DIR = "temp"
CVR_DIR = "covers"
MKD_DIR = "contents"
api_rangeofpermission = (WorksConst, ChapsConst, CatesConst, TagConst, OwnsConst)

# Flask instance
flask = Flask(__name__)

# Enable connection pooling (one time I get Connection is busy with results for another command. I scared of it now)
pyodbc.pooling = True

# pyODBC connect instance
connection_gate = pyodbc.connect(
    'DRIVER={ODBC Driver 17 for SQL Server};'
    f'Server={SQLCons.IP};'
    f'Database={SQLCons.DB_NAME};'
    f'UID={SQLCons.UID};'
    f'PWD={SQLCons.PWD}')


def sql():
    """Lấy cursor từ pyODBC để tương tác với SQL Server"""
    return connection_gate.cursor()


def reqres(res):
    """
        Thay jsonify trả kết quả cho client trong một số trường hợp cần thiết (vd: Cursor)
    """
    if isinstance(res, pyodbc.Cursor):
        lt = list()
        cols = [k[0] for k in res.description]
        r = res.fetchone()
        while r:
            lt.append(dict(zip(cols, r)))
            r = res.fetchone()
        res.close()
        return jsonify(lt)
    if isinstance(res, pyodbc.Row):
        return jsonify(dict(zip([k[0] for k in res.cursor_description], res)))
    if isinstance(res, ResponseDict):
        return jsonify(res.value)
    return jsonify(res)


def now_time():
    now = datetime.datetime.now(datetime.UTC)
    return now.replace(microsecond=0)


# My poor brain can't think of a clever way to do this, so...
def extract_token(bearer, tokentype):
    if not bearer:
        return None
    token = bearer.split(" ")[1]
    try:
        if tokentype == 1:
            return token
        if tokentype == 0:
            decoded_token = jwt.decode(token, secret_key, algorithms=["HS256"], audience="com.example.bookmoth")
            account_id = decoded_token.get("accountId")
            return account_id
    except Exception:
        return 0

class Validate:
    """
    Lớp xác thực theo yêu cầu (all static)
    """

    @staticmethod
    def user(bearer):
        """
        Xác thực người dùng đang đăng nhập (Họ là ai?)
        :param bearer: request.headers.get("Authorization")
        :return: ID người dùng / 0 và báo cáo trạng thái
        """
        token = extract_token(bearer, 0)
        if not token:
            return 0, ResponseDict.AUTH_WRONGFORMAT
        with sql().execute(f"select * from {AccsConst._TABLE_NAME} where {AccsConst.aid} = ?", token) as cr:
            ft = cr.fetchone()
            cr.fetchall()
        if ft is None:
            return 0, ResponseDict.NULL_TOKEN
        aid = ft.__getattribute__(TokensConst.aid)
        if ft.__getattribute__(TokensConst.exp).replace(tzinfo=datetime.timezone.utc) < now_time():
            return 0, ResponseDict.EXPIRED_SESSION
        return aid, ResponseDict.SUCCESS

    @staticmethod
    def profile(aid, pid_token):
        """
        Xác thực profile có thuộc quyền quản lý của tài khoản hay không
        :param aid: Account ID
        :param pid: Profile ID
        :return: Message and HTTP code
        """
        pid = extract_token(pid_token, 1)
        with sql().execute(f"select {ProfsConst.pid} from {ProfsConst._TABLE_NAME} where {ProfsConst.aid} = ? and {ProfsConst.pid} = ?",
                           (aid, pid)) as cr:
            ft = cr.fetchone()
            cr.fetchall()
        if ft is None:
            return 0, ResponseDict.PROFILE_NOPERMISSION
        return pid, ResponseDict.SUCCESS

    @staticmethod
    def author(bearer, work_id):
        """
        Xác thực quyền hạn tác giả
        :param bearer: request.headers.get("Authorization")
        :param work_id: Mã định danh tác phẩm
        :return: Boolean và báo cáo trạng thái
        """
        aid, response = Validate.user(bearer)
        if response != ResponseDict.SUCCESS:
            return False, response
        cr = sql().execute(f"select {WorksConst.pid} from {WorksConst._TABLE_NAME} where {WorksConst._id} = ?", work_id)
        ft = cr.fetchone()
        cr.close()
        if ft is None:
            return False, ResponseDict.NULL_WORK
        pid = ft.__getattribute__(WorksConst.pid)
        cr = sql().execute(f"select {ProfsConst.aid} from {ProfsConst._TABLE_NAME} where {ProfsConst.pid} = ?", pid)
        ft = cr.fetchone()
        cr.close()
        if ft is None:
            return False, ResponseDict.NO_PERMISSION
        return True, ResponseDict.SUCCESS

    @staticmethod
    def reader(bearer, content_url):
        """
        Xác thực quyền hạn độc giả
        :param bearer: request.headers.get("Authorization")
        :param content_url: Tên file markdown từ thông tin Chapter
        :return: work_id/0 và báo cáo trạng thái
        """
        aid, response = Validate.user(bearer)
        if response != ResponseDict.SUCCESS:
            return 0, response
        cr = sql().execute(f"with ofWork as (select {ChapsConst.wid} from {ChapsConst._TABLE_NAME} where {ChapsConst.body} = ?) "
                           f"select * from {OwnsConst._TABLE_NAME} join ofWork on {OwnsConst._TABLE_NAME}.{OwnsConst.wid} = ofWork.{ChapsConst.wid} where {OwnsConst.aid} = ?",
                           (content_url, aid))
        ft = cr.fetchone()
        cr.close()
        if ft is None:
            return fetch_attribute(ChapsConst, ChapsConst.body, content_url, ChapsConst.wid), ResponseDict.NO_PERMISSION
        if not ft.__getattribute__(OwnsConst.exp) or ft.__getattribute__(OwnsConst.exp).replace(tzinfo=datetime.timezone.utc) < now_time():
            return ft.__getattribute__(OwnsConst.wid), ResponseDict.EXPIRED_LICENSE
        return ft.__getattribute__(OwnsConst.wid), ResponseDict.SUCCESS


def convert_to_markdown(file):
    """
    Chuyển đổi định dạng .docx và .txt sang .md (Markdown)

    Định dạng này dễ xử lý khi load lên TextView so... :)
    :param file: File đọc từ FileStorage request.files.get()
    :return: String MD hoặc None nếu không hỗ trợ
    """
    try:
        if file.filename.endswith('.docx'):
            tempname = ''.join(random.choices(string.ascii_letters, k=10))
            while os.path.exists(os.path.join(TEMP_DIR, tempname + ".docx")):
                tempname = ''.join(random.choices(string.ascii_letters, k=10))
            docx_path = os.path.join(TEMP_DIR, tempname + ".docx")
            with open(docx_path, 'wb') as docxfile:
                docxfile.write(file.read())
            docx2md_convert = ['markitdown', docx_path, '-o', os.path.join(TEMP_DIR, tempname + ".md")]
            subprocess.run(docx2md_convert, check=True)
            md_file_path = os.path.join(TEMP_DIR, tempname + ".md")
            with open(md_file_path, 'r', encoding='utf-8') as f:
                md_file = f.read()
            os.remove(docx_path)
            os.remove(md_file_path)
            return md_file
        elif file.filename.endswith('.txt') or file.filename.endswith('.md'):
            with open(file, 'r', encoding='utf-8') as f:
                return f.read()
    except Exception as e:
        print(e)
        return None


def convert_to_jpeg(file):
    """
    Chuyển đổi ảnh tải lên thành JPEG với width=512, ratio giữ nguyên
    :param file: FileStorage
    :return: BytesIO
    """
    WIDTH_RESIZE = 512
    img_io = BytesIO()
    try:
        with Image.open(file.stream) as img:
            img = img.convert("RGB")
            img = img.resize((WIDTH_RESIZE, int(img.height / img.width * WIDTH_RESIZE)), Image.Resampling.LANCZOS)
            img.save(img_io, "JPEG")
            img_io.seek(0)
        return img_io
    except Exception:
        return None


def make_chapter_filename(work_id):
    """
    Tạo tên cho file content upload cho chapter
    :param work_id: Work_ID
    :return: String name
    """
    count = sql().execute(f"select ISNULL(IDENT_CURRENT('{ChapsConst._TABLE_NAME}'), IDENT_SEED('{ChapsConst._TABLE_NAME}')) + IDENT_INCR('{ChapsConst._TABLE_NAME}') as nextID").fetchone()[0] + 1
    return f"{count}.md"


def add_file(codename, file, work_id=0, file_name=None):
    """
    Lưu file được tải lên cùng Work/Chapter

    Trong phạm vi chức năng, chỉ xử lý đăng cover và content, chỉ đáp ứng 1 file/lần

    2 mode: có work_id nếu thêm lần đầu, biết trước cần làm gì thì có file_name
    :param codename: Mã tài nguyên (hiện tại có content và cover)
    :param file: request.files.get()
    :param work_id: ID Work
    :param file_name: Khi có thể khẳng định tên không bị trùng (as seen in update_file)
    :return: Tên file/None và Thông báo
    """
    if codename == 'content':    # Handle chapter's content file
        if file.filename == '':
            return None, ResponseDict.FILE_INVALIDNAME
        markdown = convert_to_markdown(file)
        if not markdown:
            return None, ResponseDict.FILE_NOTSUPPORTED
        if not file_name:
            file_name = make_chapter_filename(work_id)
        with open(os.path.join(MKD_DIR, file_name), 'w', encoding="utf-8") as mdfile:
            mdfile.write(markdown)
        return file_name, ResponseDict.SUCCESS
    if codename == 'cover':      # Handle work's cover
        if file.filename == '':
            return None, ResponseDict.FILE_INVALIDNAME
        jpeg = convert_to_jpeg(file)
        if not jpeg:
            return None, ResponseDict.FILE_NOTSUPPORTED
        if not file_name:
            file_name = f"{work_id}.jpeg"
        with open(os.path.join(CVR_DIR, file_name), "wb") as jpfile:
            jpfile.write(jpeg.getvalue())
        return file_name, ResponseDict.SUCCESS


def update_file(name, newfile):
    """
    Cập nhật (chính xác hơn là thay thế) file có tên name bằng newfile
    :param name: Tên file bị thay thế
    :param newfile: File thay thế
    :return: Thông báo
    """
    USED_DIR = None
    CODENAME = None
    if name.endswith(".jpeg"):
        USED_DIR = CVR_DIR
        CODENAME = 'cover'
    if name.endswith(".md"):
        USED_DIR = MKD_DIR
        CODENAME = 'content'
    try:
        os.rename(os.path.join(USED_DIR, name), os.path.join(USED_DIR, f"old_{name}"))
    except Exception as err:
        print(f"update file error: {err}")
    temp, message = add_file(CODENAME, newfile, file_name=name)
    if message != ResponseDict.SUCCESS:
        try:
            os.rename(os.path.join(USED_DIR, f"old_{name}"), os.path.join(USED_DIR, name))
        except Exception as err:
            print(f"update file error: {err}")
        return message
    rem_file(f"old_{name}")
    return message


def rem_file(filename):
    """
    Xoá file khỏi hệ thống (má ơi nó là heaven so với add file ;))

    :param filename: Tên file
    """
    if filename.endswith(".md") and os.path.exists(os.path.join(MKD_DIR, filename)):
        os.remove(os.path.join(MKD_DIR, filename))
    if filename.endswith(".jpeg") and os.path.exists(os.path.join(MKD_DIR, filename)):
        os.remove(os.path.join(CVR_DIR, filename))


def updatepack_maker(table, args):
    """
    Tạo where clause bằng thông tin bảng và các biến từ request, hỗ trợ cho pyODBC execute()

    :param table: Static class của bảng cần query
    :param args: Giá trị từ request.args.to_dict() hoặc dict() thủ công chứa đúng giá trị key:value theo bảng
    :return: String where clause và Tuple giá trị
    """
    args = {k: v for k, v in args.items() if k in vars(table).values()}
    clause = ", ".join([k + " = ?" for k in args.keys()])
    values = tuple([v for v in args.values()])
    return clause, values


def where_maker(table, args):
    """
    Tạo where clause bằng thông tin bảng và các biến từ request, hỗ trợ cho pyODBC execute()

    :param table: Static class của bảng cần query
    :param args: Giá trị từ request.args.to_dict() hoặc dict() thủ công chứa đúng giá trị key:value theo bảng
    :return: String where clause và Tuple giá trị
    """
    args = {k: v for k, v in args.items() if k in vars(table).values()}
    clause = " and ".join([k + " = ?" for k in args.keys()])
    values = tuple([v for v in args.values()])
    if len(clause):
        clause = f"where {clause}"
    return clause, values


def valuespack_maker(table, data):
    """
    Tạo String và Tuple hỗ trợ lệnh SQL Insert/Update
    :param table: Class bảng
    :param data: Dictionary các giá trị
    :return: String (tên các trường), String (mấy cái ?) và Tuple
    """
    params = [v for k, v in vars(table).items() if not k.startswith("_")]
    s = "(" + ", ".join(params) + ")"
    p = "(" + ", ".join(["?"] * len(params)) + ")"
    values = [data.get(k, None) for k in params]
    t = tuple(values)
    return s, p, t


def add_row(table, data):
    """
    Thêm bản ghi vào bảng một cách tự động
    :param table: Class bảng đã define bên trên
    :param data: Dictionary chứa đầy đủ thuộc tính, thiếu cái nào thì cái đấy mặc định là None (null)
    :return: Xác nhận hành động + Thông báo
    """
    if table not in api_rangeofpermission:
        return 0, ResponseDict.API_PERMISSION
    params, placeholder, values = valuespack_maker(table, data)
    changes = 0
    try:
        changes = sql().execute(f"insert into {table._TABLE_NAME} {params} values {placeholder}", values).rowcount
    except pyodbc.Error as err:
        if isinstance(err, pyodbc.IntegrityError):
            message = ResponseDict.PARAMETER_REQUIRED
        elif isinstance(err, pyodbc.InternalError):
            message = ResponseDict.SQL_INTERNAL_ERROR
        else:
            message = {"add error": f"{err}"}
        sql().rollback()
        return changes, message
    sql().commit()
    return changes, ResponseDict.SUCCESS


def update_row(table, related_id, data):
    if table not in api_rangeofpermission:
        return 0, ResponseDict.API_PERMISSION
    params, values = updatepack_maker(table, data)
    changes = 0
    try:
        changes = sql().execute(f"update {table._TABLE_NAME} set {params} where {table._id} = ?", values + (related_id,)).rowcount
    except pyodbc.Error as err:
        if isinstance(err, pyodbc.IntegrityError):
            message = ResponseDict.PARAMETER_REQUIRED
        elif isinstance(err, pyodbc.InternalError):
            message = ResponseDict.SQL_INTERNAL_ERROR
        else:
            message = {"update error": f"{err}"}
        sql().rollback()
        return changes, message
    sql().commit()
    return changes, ResponseDict.SUCCESS


def rem_row(table, related_id, commit_right_after=True):
    if table not in api_rangeofpermission:
        return 0, ResponseDict.API_PERMISSION
    changes = 0
    try:
        if table is WorksConst:
            cr = sql().execute(f"select {ChapsConst._id} from {ChapsConst._TABLE_NAME} where {ChapsConst.wid} = ?", related_id)
            ft = cr.fetchone()
            while ft:
                rem_row(ChapsConst, ft[0], False)
                ft = cr.fetchone()
            cr.close()
            sql().execute(f"delete from {OwnsConst._TABLE_NAME} where {OwnsConst.wid} = ?", related_id)
            sql().execute(f"delete from {TagConst._TABLE_NAME} where {TagConst.wid} = ?", related_id)
            changes = sql().execute(f"delete from {WorksConst._TABLE_NAME} where {WorksConst._id} = ?", related_id).rowcount
        elif table is ChapsConst:
            cr = sql().execute(f"select {ChapsConst.body} from {ChapsConst._TABLE_NAME} where {ChapsConst._id} = ?", related_id)
            ft = cr.fetchone()
            cr.close()
            if ft:
                rem_file(ft[0])
            changes = sql().execute(f"delete from {ChapsConst._TABLE_NAME} where {ChapsConst._id} = ?", related_id)
        elif table is TagConst:
            changes = sql().execute(f"delete from {TagConst._TABLE_NAME} where {TagConst.wid} = ? and {TagConst.tid} = ?", (related_id.get(TagConst.wid), related_id.get(TagConst.tid))).rowcount
    except pyodbc.Error as err:
        sql().rollback()
        return changes, {"rem error": f"{err}"}
    if commit_right_after:
        sql().commit()
    return changes, ResponseDict.SUCCESS


def find_work_id(pid, pdate):
    """
    Tìm Work bằng tên và ngày đăng (vì post_work cần)
    :param pid: Profile tác giả
    :param pdate: Datetime ngày đăng
    :return: work_id
    """
    ft = sql().execute(f"select {WorksConst._id} from {WorksConst._TABLE_NAME} where {WorksConst.pdate} = ? and {WorksConst.pid} = ?", (pdate, pid)).fetchone()
    if ft:
        return ft[0]
    else:
        return None


def fetch_attribute(table, with_attr_name, with_attr_val, get_attr_name):
    """
    Lấy giá trị của attribue nào đó trong trường
    :param table: Class Bảng
    :param with_attr_name: Tên thuộc tính neo vào để tìm
    :param with_attr_val: Giá trị của thuộc tính neo (tìm bản ghi có giá trị này)
    :param get_attr_name: Thuộc tính cần lấy giá trị
    :return: Giá trị hoặc None
    """
    ft = sql().execute(f"select {get_attr_name} from {table._TABLE_NAME} where {with_attr_name} = ?", with_attr_val).fetchone()
    if ft:
        return ft[0]
    else:
        return None


def increase_view_count(wid):
    """
    Tăng số đếm viewcount của tác phẩm (so good that user can spam viewcount like crazy)
    :param wid: Work ID
    :return:
    """
    try:
        changes = sql().execute(f"update {WorksConst._TABLE_NAME} set {WorksConst.view} = {WorksConst.view} + 1 where {WorksConst._id} = ?", wid).rowcount
    except pyodbc.Error as err:
        if isinstance(err, pyodbc.IntegrityError):
            message = ResponseDict.PARAMETER_REQUIRED
        elif isinstance(err, pyodbc.InternalError):
            message = ResponseDict.SQL_INTERNAL_ERROR
        else:
            message = {"update error": f"{err}"}
        sql().rollback()
        return 0, message
    sql().commit()
    return changes, ResponseDict.SUCCESS


@flask.get("/libapi/work/<int:work_id>/stats")
def get_work_statistic(work_id):
    """
    Lấy thông tin thống kê của tác phẩm (lượt xem, số người mua, lần cập nhật cuối, giá hiện tại)
    :param wid: Work ID
    :return:
    """
    bearer = request.headers.get("Authorization")
    validate, message = Validate.author(bearer, work_id)
    if not validate:
        return reqres(ResponseDict.NO_PERMISSION), 400
    with sql().execute(f"with follows as (select {OwnsConst.wid}, COUNT({OwnsConst.aid}) as followcount from {OwnsConst._TABLE_NAME} where {OwnsConst.wid} = ? group by {OwnsConst.wid}), "
                        f"lastupdate as (select top 1 {ChapsConst.wid}, {ChapsConst.pdate} as lastdate from {ChapsConst._TABLE_NAME} where {ChapsConst.wid} = ? order by lastdate desc) "
                        f"select {WorksConst._TABLE_NAME}.{WorksConst._id}, isnull({WorksConst.view},0) as {WorksConst.view}, isnull(followcount,0) as {JoinConst.follows}, isnull(lastdate,{WorksConst.pdate}) as {JoinConst.lastupdate}, {WorksConst.price} from {WorksConst._TABLE_NAME} "
                        f"left join follows on {WorksConst._TABLE_NAME}.{WorksConst._id} = follows.{OwnsConst.wid} left join lastupdate on {WorksConst._TABLE_NAME}.{WorksConst._id} = lastupdate.{ChapsConst.wid} "
                        f"where {WorksConst._TABLE_NAME}.{WorksConst._id} = ?",
                       (work_id, work_id, work_id)) as cr:
        ft = cr.fetchone()
        cr.fetchall()
    return reqres(ft), 200


@flask.route("/libapi/cdn/read/<string:content>", methods=['GET'])
def get_chapter_content(content):
    """
    Trả về .md trong kho. Có check quyền hạn trước khi trả kết quả
    :param content: Tên file
    :return:
    """
    bearer = request.headers.get("Authorization")
    wid, message = Validate.reader(bearer, content)
    if not message == ResponseDict.SUCCESS:
        validate, message = Validate.author(bearer, wid)
        if not validate:
            return reqres(ResponseDict.NO_PERMISSION), 400
    increase_view_count(wid)
    return send_file(os.path.join(MKD_DIR, content), as_attachment=False), 200


@flask.route("/libapi/cdn/cover/<string:cover>", methods=['GET'])
def get_work_cover(cover):
    """
    Trả về ảnh trong kho kèm etag để cache (Glide). Thông báo nếu ảnh không cần trả về
    :param cover: Tên file (cover_url)
    """
    path = os.path.join(CVR_DIR, cover)
    if not os.path.exists(path):
        return reqres(ResponseDict.FILE_NULL), 400
    res = send_file(path, as_attachment=False)
    return res, 200


@flask.route("/libapi/works", methods=['GET'])
def get_works():
    """
    Lấy JSON thông tin các tác phẩm (theo điều kiện nếu có)
    """
    params = request.args.to_dict()
    clause, values = where_maker(WorksConst, params)
    cr = sql().execute(f"with Author as ( select {ProfsConst.pid}, {ProfsConst.lname}+' '+{ProfsConst.fname} as {JoinConst.author} from {ProfsConst._TABLE_NAME} ) "
                       f"select * from {WorksConst._TABLE_NAME} "
                       f"join Author on Author.{ProfsConst.pid} = {WorksConst._TABLE_NAME}.{WorksConst.pid} "
                       f"{clause}",
                       values)
    return reqres(cr), 200


@flask.route("/libapi/work/<int:work_id>/chapters", methods=['GET'])
def get_chapters(work_id: int):
    """
    Lấy JSON thông tin các chương (theo điều kiện nếu có)
    """
    params = request.args.to_dict()
    if work_id > 0:  #Vì chapter đi cùng với work
        params[ChapsConst.wid] = work_id.__str__()
    clause, values = where_maker(ChapsConst, params)
    cr = sql().execute(f"select * from {ChapsConst._TABLE_NAME} "
                       f"{clause}",
                       values)
    return reqres(cr), 200


@flask.route("/libapi/work/<int:work_id>", methods=['GET'])
def get_work(work_id: int):
    """
    Lấy JSON thông tin tác phẩm theo ID
    """
    cr = sql().execute(f"with Author as ( select {ProfsConst.pid}, {ProfsConst.lname}+' '+{ProfsConst.fname} as {JoinConst.author} from {ProfsConst._TABLE_NAME} ) "
                        f"select * from {WorksConst._TABLE_NAME} "
                        f"join Author on Author.{ProfsConst.pid} = {WorksConst._TABLE_NAME}.{WorksConst.pid} "
                        f"where {WorksConst._id} = ?",
                         work_id)
    ft = cr.fetchone()
    cr.close()
    return reqres(ft), 200


@flask.route("/libapi/chapter/<int:chapter_id>", methods=['GET'])
def get_chapter(chapter_id: int):
    """
    Lấy JSON thông tin chương theo ID
    """
    cr = sql().execute(f"select * from {ChapsConst._TABLE_NAME} "
                       f"where {ChapsConst._id} = ?",
                       chapter_id)
    ft = cr.fetchone()
    cr.close()
    return reqres(ft), 200


@flask.route("/libapi/work/post", methods=['POST'])
def post_work():
    """
    Thêm tác phẩm mới vào DB qua POST

    Yêu cầu: Form 'json', File ảnh tuỳ chọn tải lên luôn hoặc để sau có thể tải lên thêm
    """
    aid, message = Validate.user(request.headers.get("Authorization"))
    if not aid:
        return reqres(message), 400
    data = request.form.get('json')
    if data:
        data = json.loads(data)
    else:
        return reqres(ResponseDict.REQUEST_REQUIRED), 400
    pid, message = Validate.profile(aid, request.headers.get("ProfileId"))
    if not pid:
        return reqres(message), 400
    data[WorksConst.pid] = pid
    data[WorksConst.pdate] = now_time()
    data[WorksConst.view] = 0
    changes, message = add_row(WorksConst, data)
    if message == ResponseDict.SUCCESS and 'cover' in request.files and request.files['cover'].filename != "":
        wid = find_work_id(data[WorksConst.pid], data[WorksConst.pdate])
        imgname, message = add_file('cover', request.files['cover'], wid)
        if imgname:
            changes, message = update_row(WorksConst, wid, {WorksConst.img: imgname})
    if message != ResponseDict.SUCCESS:
        return reqres(message), 400
    return reqres(message), 200

    # TODO: Xác thực profile đã đăng ký làm tác giả hay chưa


@flask.route("/libapi/work/<int:work_id>/chapter/post", methods=['POST'])
def post_chapter(work_id):
    """
    Thêm chương mới vào tác phẩm qua POST

    Yêu cầu: Form 'json' và File 'docx/txt/md'
    """
    validate, message = Validate.author(request.headers.get("Authorization"), work_id)
    if not validate:
        return reqres(message), 400
    data = request.form.get('json')
    if data:
        data = json.loads(data)
    else:
        return reqres(ResponseDict.REQUEST_REQUIRED), 400
    if 'content' not in request.files:
        return reqres(ResponseDict.FILE_REQUIRED), 400
    mdname, message = add_file('content', request.files['content'], work_id)
    if not mdname:
        return reqres(message), 400
    data[ChapsConst.wid] = work_id
    data[ChapsConst.pdate] = now_time()
    data[ChapsConst.body] = mdname
    changes, message = add_row(ChapsConst, data)
    if message != ResponseDict.SUCCESS:
        return reqres(message), 400
    return reqres(message), 200


@flask.route("/libapi/work/<int:work_id>/put", methods=['PUT'])
def put_work(work_id):
    """
    Cập nhật thông tin (và hình ảnh) của tác phẩm
    :param work_id:
    :return:
    """
    validate, message = Validate.author(request.headers.get("Authorization"), work_id)
    if not validate:
        return reqres(message), 400
    data = request.form.get('json')
    if data:
        data = json.loads(data)
    else:
        return reqres(ResponseDict.REQUEST_REQUIRED), 400
    data.pop(WorksConst._id, None)
    data.pop(WorksConst.pdate, None)
    data.pop(WorksConst.view, None)
    data.pop(WorksConst.img, None)
    cvname = fetch_attribute(WorksConst, WorksConst._id, work_id, WorksConst.img)
    if 'cover' in request.files:
        if not cvname:
            cvname, message = add_file("cover", request.files['cover'], work_id=work_id)
        else:
            message = update_file(cvname, request.files['cover'])
        if message != ResponseDict.SUCCESS:
            return reqres(message), 400
        data[WorksConst.img] = cvname
    changes, message = update_row(WorksConst, work_id, data)
    if message != ResponseDict.SUCCESS:
        return reqres(message), 400
    return reqres(message), 200


@flask.route("/libapi/chapter/<int:chapter_id>/put", methods=['PUT'])
def put_chapter(chapter_id):
    """
    Cập nhật thông tin (và nội dung) của chương truyện
    :param chapter_id:
    :return:
    """
    validate, message = Validate.author(request.headers.get("Authorization"), fetch_attribute(ChapsConst, ChapsConst._id, chapter_id, ChapsConst.wid))
    if not validate:
        return reqres(message), 400
    data = request.form.get('json')
    if data:
        data = json.loads(data)
    else:
        return reqres(ResponseDict.REQUEST_REQUIRED), 400
    data.pop(ChapsConst._id, None)
    data.pop(ChapsConst.pdate, None)
    data.pop(ChapsConst.wid, None)
    data.pop(ChapsConst.body, None)
    mdname = fetch_attribute(ChapsConst, ChapsConst._id, chapter_id, ChapsConst.body)
    if 'content' in request.files:
        message = update_file(mdname, request.files['content'])
        if message != ResponseDict.SUCCESS:
            return reqres(message), 400
        data[ChapsConst.pdate] = now_time()
    changes, message = update_row(ChapsConst, chapter_id, data)
    if message != ResponseDict.SUCCESS:
        return reqres(message), 400
    return reqres(message), 200

# TODO: Xem thử có tách phẩn PUT thông tin và PUT file ra riêng không


@flask.route("/libapi/work/<int:work_id>/delete", methods=['DELETE'])
def delete_work(work_id):
    """
    Xoá tác phẩm
    :param work_id:
    :return:
    """
    validate, message = Validate.author(request.headers.get("Authorization"), work_id)
    if not validate:
        return reqres(message), 400
    changes, message = rem_row(WorksConst, work_id)
    if message != ResponseDict.SUCCESS:
        return reqres(message), 400
    return reqres(message), 200


@flask.route("/libapi/chapter/<int:chapter_id>/delete", methods=['DELETE'])
def delete_chapter(chapter_id):
    """
    Xoá chương truyện
    :param chapter_id:
    :return:
    """
    validate, message = Validate.author(request.headers.get("Authorization"), fetch_attribute(ChapsConst, ChapsConst._id, chapter_id, ChapsConst.wid))
    if not validate:
        return reqres(message), 400
    changes, message = rem_row(ChapsConst, chapter_id)
    if message != ResponseDict.SUCCESS:
        return reqres(message), 400
    return reqres(message), 200


# TODO: Nên nghĩ đến giải pháp thông minh hơn để làm Tag
TAG_LIMIT = 5


def fetch_worktags(work_id):
    """
    Lấy một dict các active tag của tác phẩm
    :param work_id:
    :return:
    """
    cr = sql().execute( f"with activ as (select {TagConst.tid} as ct from {TagConst._TABLE_NAME} where {TagConst.wid} = ?) " +
                        f"select activ.ct, {CatesConst.name} from activ join {CatesConst._TABLE_NAME} on ct = {CatesConst._id}", work_id)
    ft = cr.fetchone()
    tags = dict()
    while ft:
        tags[ft[0]] = ft[1]
        ft = cr.fetchone()
    cr.close()
    return tags


def tag_work(work_id, tag_with):
    """
    Tác động SQL để thêm tag
    :param work_id:
    :param tag_with: List các tag
    :return:
    """
    active_tags = list(fetch_worktags(work_id).keys())
    count = TAG_LIMIT - len(active_tags)
    tagged = 0
    for v in tag_with:
        if not count:
            break
        if v in active_tags:
            continue
        changes, message = add_row(TagConst, {TagConst.wid: work_id, TagConst.tid: v})
        if message == ResponseDict.SUCCESS:
            tagged += 1
            count -= 1
    sql().commit()
    return tagged, count


def untag_work(work_id, untag_from):
    """
    Tác động SQL để xoá tag
    :param work_id:
    :param untag_from: List các tag
    :return:
    """
    active_tags = list(fetch_worktags(work_id).keys())
    count = 5 - len(active_tags)
    untagged = 0
    for v in untag_from:
        if count >= TAG_LIMIT:
            break
        if v not in active_tags:
            continue
        changes, message = rem_row(TagConst, {TagConst.wid: work_id, TagConst.tid: v})
        if message == ResponseDict.SUCCESS:
            untagged += 1
            count += 1
    sql().commit()
    return untagged, count


@flask.route("/libapi/work/<int:work_id>/tags", methods=['GET'])
def get_tags(work_id):
    """
    Lấy danh sách các nhãn được dán lên tác phẩm
    :param work_id:
    :return:
    """
    return reqres(fetch_worktags(work_id)), 200


@flask.route("/libapi/work/<int:work_id>/tag/post", methods=['POST'])
def post_tag(work_id):
    """
    Thêm tag vào tác phẩm
    :param work_id:
    :return:
    """
    validate, message = Validate.author(request.headers.get("Authorization"), work_id)
    if not validate:
        return reqres(message), 400
    data = request.form.get('tags')
    if data:
        data = json.loads(data)
    else:
        return reqres(ResponseDict.REQUEST_REQUIRED), 400
    changes, remains = tag_work(work_id, data)
    return reqres({"tagged": changes, "remains": remains}), 200


@flask.route("/libapi/work/<int:work_id>/tag/delete", methods=['DELETE'])
def delete_tag(work_id):
    """
    Thêm tag vào tác phẩm
    :param work_id:
    :return:
    """
    validate, message = Validate.author(request.headers.get("Authorization"), work_id)
    if not validate:
        return reqres(message), 400
    data = request.form.get('tags')
    if data:
        data = json.loads(data)
    else:
        return reqres(ResponseDict.REQUEST_REQUIRED), 400
    changes, remains = untag_work(work_id, data)
    return reqres({"untagged": changes, "remains": remains})


@flask.get("/libapi/owned")
def get_owned_works():
    """
    Lấy danh sách các sách đang sở hữu / còn hạn đọc
    """
    aid, message = Validate.user(request.headers.get("Authorization"))
    if not aid:
        return reqres(message), 400
    cr = sql().execute(f"with Own as ( select {OwnsConst.wid} from {OwnsConst._TABLE_NAME} where {OwnsConst.aid} = ? and ({OwnsConst.exp} >= ?) or {OwnsConst.exp} is null ) "
                       f", Author as ( select {ProfsConst.pid}, {ProfsConst.lname}+' '+{ProfsConst.fname} as {JoinConst.author} from {ProfsConst._TABLE_NAME} ) "
                       f"select * from {WorksConst._TABLE_NAME} "
                       f"join Own on Own.{OwnsConst.wid} = {WorksConst._TABLE_NAME}.{WorksConst._id} "
                       f"join Author on Author.{ProfsConst.pid} = {WorksConst._TABLE_NAME}.{WorksConst.pid}",
                       (aid, now_time()))
    return reqres(cr), 200


@flask.get("/libapi/created")
def get_created_works():
    """
    Lấy danh sách các sách đang sở hữu / còn hạn đọc
    """
    aid, message = Validate.user(request.headers.get("Authorization"))
    if not aid:
        return reqres(message), 400
    cr = sql().execute(f"with AllProfs as ("
                            f"select {ProfsConst.pid}, {ProfsConst.lname}+' '+{ProfsConst.fname} as {JoinConst.author} from {ProfsConst._TABLE_NAME} "
                            f"where {ProfsConst.aid} = ?) "
                       f"select * from {WorksConst._TABLE_NAME} "
                       f"join AllProfs on AllProfs.{ProfsConst.pid} = {WorksConst._TABLE_NAME}.{WorksConst.pid} ",
                       aid)
    return reqres(cr), 200


def libapi_firstrun():
    """
    Tạo các folder cần thiết lần đầu chạy API
    :return:
    """
    if not os.path.exists(CVR_DIR):
        os.makedirs(CVR_DIR)
    if not os.path.exists(MKD_DIR):
        os.makedirs(MKD_DIR)
    if not os.path.exists(TEMP_DIR):
        os.makedirs(TEMP_DIR)


# TODO: Bất kể cái nào dùng các hàm bên dưới đều phải được đổi thành hàm chuyên dụng về sau (Đây là vùng giữ chỗ)
@flask.get("/liblab/whoami")
def get_account_info():
    aid, msg = Validate.user(request.headers.get("Authorization"))
    if not aid:
        return reqres(msg), 400
    return reqres(aid), 200


'''Windows / localdev only. Comment out and use gunicorn if on Linux/Docker'''
if __name__ == "__main__":
    libapi_firstrun()
    # waitress.serve(libapi, host="localhost", port=1445) #waitress
    flask.run(host="192.168.218.34", port=1445)  #basic Flask
# , ssl_context=("cert.pem", "key.pem")

