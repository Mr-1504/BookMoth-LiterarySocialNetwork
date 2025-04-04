import math

from sqlalchemy import create_engine, text, MetaData, Table, update, Integer, Column, String, INTEGER, DateTime, DECIMAL
from flask import jsonify, request
from decimal import Decimal
from unidecode import unidecode

from unicodedata import category


class WorksModel:
    def __init__(self):
        try:
            server = '.\\SQLEXPRESS'
            database = 'BookMoth'
            driver = 'ODBC Driver 17 for SQL Server'
            conn_str = f'mssql+pyodbc://@{server}/{database}?trusted_connection=yes&driver={driver}'
            self.engine = create_engine(conn_str, echo=False)
            self.engine.autocommit = True

            self.base_url = "http://192.168.218.34:8000"
            self.static_cover_url = "/covers/"


            #Load bảng từ database
            self.metadata = MetaData()
            self.works = Table(
                "Works",
                self.metadata,
                Column("work_id", Integer, primary_key=True),
                Column("profile_id", Integer),
                Column("title",String),
                Column("post_date", DateTime),
                Column("author", String),
                Column("price", DECIMAL),
                Column("view_count",INTEGER),
                Column("description", String),
                Column("cover_url", String)
            )
            self.worktags = Table(
                "Worktags",
                self.metadata,
                Column("work_id", INTEGER),
                Column("category_id", INTEGER),
            )
            self.categories = Table(
                "Categories",
                self.metadata,
                Column("category_id", INTEGER, primary_key=True),
                Column("tag", String)
            )
            self.chapters = Table(
                "Chapters",
                self.metadata,
                Column("chapter_id", INTEGER, primary_key=True),
                Column("work_id", INTEGER),
                Column("title", String),
                Column("post_date", DateTime),
                Column("content_url", String)
            )
            self.profiles = Table(
                "Profiles",
                self.metadata,
                Column("profile_id", INTEGER, primary_key=True),
                Column("account_id", INTEGER),
                Column("first_name", String),
                Column("last_name", String),
                Column("username", String),
                Column("avatar", String),
                Column("coverphoto",String),
                Column("identifier",INTEGER),
                Column("gender",INTEGER)
            )

            print('Connected to database successfully!')
        except Exception as e:
            print(f'Connection failed: {e}')

    def construct_image(self,cover_filename):
        """Tạo đường dẫn đầy đủ cho file ảnh"""
        if cover_filename:
            return f"{self.base_url}{self.static_cover_url}{cover_filename}"

    def get_all_works(self):
        """Lấy tất cả các sách"""
        try:
            with self.engine.connect() as conn:
                result = conn.execute(text("SELECT * FROM Works"))
                works = [{key: float(value) if isinstance(value, Decimal) else value for key, value in row.items()}
                    for row in result]

                for work in works:
                    work['cover_url'] = self.construct_image(work['cover_url'])
                    work_id = work['work_id']
                    categories_query = text("""
                        SELECT c.tag FROM Categories c JOIN WorkTags w ON w.category_id = c.category_id WHERE w.work_id = :work_id
                    """)
                    categories = conn.execute(categories_query, {'work_id': work_id}).fetchall()
                    work['categories'] = [row.tag for row in categories]

                    chapters_query = conn.execute(text("SELECT * FROM Chapters WHERE work_id = :work_id"), {'work_id': work_id})
                    chapters = chapters_query.mappings().fetchall()
                    work['chapters'] = [{key: float(value) if isinstance(value, Decimal) else value for key, value in chapter.items()}
                        for chapter in chapters]

                return jsonify(works)
        except Exception as e:
            return jsonify({'error': str(e)})

    def get_work_by_id(self,work_id):
        try:
            with self.engine.connect() as conn:
                result = conn.execute(text("SELECT * FROM Works WHERE work_id = :work_id"), {'work_id': work_id}).fetchone()
                if not result:
                    return jsonify({'error': 'Work not found'}) , 404
                work = dict(result._mapping)
                work = {key: float(value) if isinstance(value, Decimal) else value for key, value in work.items()}
                work['cover_url'] = self.construct_image(work['cover_url'])

                categories_query = text("""
                    SELECT c.tag FROM Categories c JOIN Worktags w ON w.category_id = c.category_id WHERE w.work_id = :work_id
                """)
                categories = conn.execute(categories_query, {'work_id': work_id}).fetchall()
                work['categories'] = [row.tag for row in categories]

                chapters_query = conn.execute(text("SELECT * FROM Chapters WHERE work_id = :work_id"),
                                                  {'work_id': work_id})
                chapters = chapters_query.mappings().fetchall()
                work['chapters'] = [{key: float(value) if isinstance(value, Decimal) else value for key, value in chapter.items()}
                for chapter in chapters]

                return jsonify(work)
        except Exception as e:
            return jsonify({'error': str(e)}), 500

    def get_work_by_title(self,title):
        """Tìm sách theo tên"""
        try:
            with self.engine.connect() as conn:
                result = conn.execute(text("SELECT * FROM Works WHERE title COLLATE Vietnamese_CI_AI LIKE :title"), {'title': f"%{title}%"}).fetchall()
                if not result:
                    return jsonify({'error': 'Work not found'}) , 404
                works = []
                for row in result:
                    work = dict(row._mapping)
                    work = {key: float(value) if isinstance(value, Decimal) else value for key, value in work.items()}
                    work['cover_url'] = self.construct_image(work['cover_url'])

                    categories_query = text("""
                                        SELECT c.tag FROM Categories c JOIN Worktags wt ON wt.category_id = c.category_id 
                                        JOIN Works w ON w.work_id = wt.work_id
                                        WHERE w.title COLLATE Vietnamese_CI_AI LIKE :title      
                                    """)
                    categories = conn.execute(categories_query, {'title': f"%{title}%"}).fetchall()
                    work['categories'] = [row.tag for row in categories]

                    chapters_query = conn.execute(
                        text(
                            "SELECT * FROM Chapters c JOIN Works w ON c.work_id = w.work_id WHERE w.title COLLATE Vietnamese_CI_AI LIKE :title"),
                        {'title': f"%{title}%"})
                    chapters = chapters_query.mappings().fetchall()

                    work['chapters'] = [
                        {key: float(value) if isinstance(value, Decimal) else value for key, value in chapter.items()}
                        for chapter in chapters
                    ]
                    works.append(work)
                return jsonify(works)
        except Exception as e:
            return jsonify({'error': str(e)}), 500
    def get_work_by_tag(self,tag):
        """Tìm sách theo thể loại"""
        try:
            with self.engine.connect() as conn:
                page = int(request.args.get('page', 1))
                per_page = int(request.args.get('per_page', 15))
                offset = (page - 1) * per_page
                query = text("""
                    SELECT * FROM Works w JOIN Worktags wt on w.work_id = wt.work_id
                                          JOIN Categories c on c.category_id = wt.category_id
                                          WHERE c.tag = :tag
                                          ORDER BY w.work_id
                                          OFFSET :offset ROWS FETCH NEXT :per_page ROWS ONLY
                """)

                result = conn.execute(query, {'tag': tag, 'per_page': per_page, 'offset':offset}).mappings().all()
                if not result:
                    return jsonify({'error': 'Work not found'}) , 404
                works = [
                    {key: float(value) if isinstance(value, Decimal) else value for key, value in dict(row).items()}
                    for row in result
                ]

                for work in works:
                    work['cover_url'] = self.construct_image(work['cover_url'])
                    work_id = work['work_id']

                    categories_query = text("""
                        SELECT c.tag FROM Categories c
                        JOIN Worktags w ON w.category_id = c.category_id
                        WHERE w.work_id = :work_id
                    """)
                    categories = conn.execute(categories_query, {'work_id': work_id}).fetchall()
                    work['categories'] = [row.tag for row in categories]

                    chapters_query = conn.execute(text("SELECT * FROM Chapters WHERE work_id = :work_id"),
                                                  {'work_id': work_id})
                    chapters = chapters_query.mappings().fetchall()
                    work['chapters'] = [
                        {key: float(value) if isinstance(value, Decimal) else value for key, value in chapter.items()}
                        for chapter in chapters
                    ]
                    count_query = text("""
                                    SELECT COUNT(*) FROM Works w
                                    JOIN Worktags wt ON w.work_id = wt.work_id
                                    JOIN Categories c ON c.category_id = wt.category_id
                                    WHERE c.tag = :tag
                                """)
                    total_items = conn.execute(count_query, {'tag': tag}).scalar()

                return jsonify({
                    'works': works,
                    'total_items': total_items,
                    'page': page,
                    'per_page': per_page,
                    'total_pages': (total_items + per_page - 1) // per_page,
                })
        except Exception as e:
            return jsonify({'error': str(e)}), 500

    def get_newReleases(self):
        """Lấy danh sách truyện mới phát hành"""
        try:
            with self.engine.connect() as conn:
                query = text("""
                    SELECT TOP 10 * FROM Works
                    ORDER BY post_date DESC
                """)
                # result = conn.execute(query).fetchall()
                # works = [dict(zip(result.keys(), self.convert_decimal(result))) for row in result]
                result = conn.execute(query).mappings().all()  # Lấy dữ liệu dưới dạng dict

                works = [
                    {key: float(value) if isinstance(value, Decimal) else value for key, value in row.items()}
                    for row in result
                ]

                for work in works:
                        work['cover_url'] = self.construct_image(work['cover_url'])
                return jsonify(works)
        except Exception as e:
            return jsonify({'error': str(e)}), 500

    def get_popular(self):
        try:
            with self.engine.connect() as conn:
                query = text("""
                    SELECT TOP 10  * FROM Works
                    ORDER BY view_count DESC
                """)

                # result = conn.execute(query).fetchall()
                # works = [dict(zip(result.keys(), self.convert_decimal(result))) for row in result]
                result = conn.execute(query).mappings().all()

                works = [
                    {key: float(value) if isinstance(value, Decimal) else value for key, value in row.items()}
                    for row in result
                ]

                for work in works:
                    work['cover_url'] = self.construct_image(work['cover_url'])
                return jsonify(works)
        except Exception as e:
            return jsonify({'error': str(e)}), 500




