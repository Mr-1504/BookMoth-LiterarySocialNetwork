from sqlalchemy import create_engine, text, MetaData,Table, Column, String, DateTime, INTEGER
from flask import Flask, request, jsonify
from decimal import Decimal

class ProfilesModel:
    def __init__(self):
        try:
            server = '.\\SQLEXPRESS'
            database = 'BookMoth'
            driver = 'ODBC Driver 17 for SQL Server'
            conn_str = f'mssql+pyodbc://{server}/{database}?trusted_connection=yes&driver={driver}'
            self.engine = create_engine(conn_str, echo=False)
            self.engine.autocommit = True

            self.base_url = "http://192.168.218.34:8000"
            self.static_cover_url = "/covers/"

            self.metadata = MetaData()
            self.Profiles = Table(
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

            print("Connected to database successfully!")
        except Exception as e:
            print(f'Failed to connect to database: {e}')

    def construct_coverphoto(self, coverphoto):
        """Tạo đường dẫn cho ảnh"""
        if coverphoto:
            return f"{self.base_url}{self.static_cover_url}{coverphoto}"
        return None

    def get_profiles_by_work_id(self, work_id):
        """Lấy thông tin tác giả qua id sách"""
        try:
            if not isinstance(work_id, int) or work_id <= 0:
                return jsonify({'error': 'Invalid work_id'}), 400
            with self.engine.connect() as conn:
                query = text("""
                SELECT * FROM Profiles JOIN Works ON Profiles.profile_id = Works.profile_id WHERE Works.work_id = :work_id""")

                result = conn.execute(query, {'work_id': work_id}).mappings().all()
                if not result:
                    return jsonify({'ERROR  ': 'Cant find works author'}), 404
                profile = dict(result[0])

                profile['avatar'] = self.construct_coverphoto(profile['avatar'])
                profile['coverphoto'] = self.construct_coverphoto(profile['coverphoto'])
                return jsonify(profile)
        except Exception as e:
            return jsonify({'error': str(e)}), 500
