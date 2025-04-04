from sqlalchemy import create_engine, text, MetaData,Table, Column, String, DateTime, INTEGER
from flask import Flask, request, jsonify
from decimal import Decimal

class ChaptersModel:
    def __init__(self):
        try:
            server = 'WANGAMRT\SQLEXPRESS'
            database = 'BookMoth'
            driver = 'ODBC Driver 17 for SQL Server'
            conn_str = f'mssql+pyodbc://{server}/{database}?trusted_connection=yes&driver={driver}'
            self.engine = create_engine(conn_str, echo=False)
            self.engine.autocommit = True

            self.base_url = "http://127.0.0.1:8000"
            self.static_cover_url = "/covers/"

            self.metadata = MetaData()
            self.chapters = Table(
                "Chapters",
                self.metadata,
                Column("chapter_id", INTEGER, primary_key=True),
                Column("work_id", INTEGER),
                Column("title", String),
                Column("post_date", DateTime),
                Column("content_url", String),

            )

            print("Connected to database successfully!")
        except Exception as e:
            print(f'Failed to connect to database: {e}')

    def construct_coverphoto(self, coverphoto):
        """Tạo đường dẫn đầy đủ cho file ảnh của chapter"""
        if coverphoto:
            return f"{self.base_url}{self.static_cover_url}{coverphoto}"
        return None

    def get_chapters_by_work(self, work_id):
        """Lấy các chapter của sách theo work_id"""
        try:
            with self.engine.connect() as conn:
                query = text("SELECT * FROM Chapters WHERE work_id = :work_id")
                result = conn.execute(query, {"work_id": work_id}).mappings().all()
                if not result:
                    return jsonify({'ERROR  ': 'No chapters found for this work'}), 404
                chapters = [dict(row) for row in result]
                for chapter in chapters:
                    chapter['content_url'] = self.construct_coverphoto(chapter['content_url'])
                return jsonify(chapters)
        except Exception as e:
            return jsonify({'error': str(e)}), 500

