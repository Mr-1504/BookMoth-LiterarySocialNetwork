from sqlalchemy import create_engine, text, MetaData, Table, Column, INTEGER,String
from flask import Flask, request, jsonify
from sqlalchemy.engine import row


class CategoriesModel:
    def __init__(self):
        try:
            server = 'WANGAMRT\SQLEXPRESS'
            database = 'BookMoth'
            driver = 'ODBC Driver 17 for SQL Server'
            conn_str = f'mssql+pyodbc://{server}/{database}?trusted_connection=yes&driver={driver}'
            self.engine = create_engine(conn_str, echo=False)
            self.engine.autocommit = True

            self.metadata = MetaData()
            self.categories = Table(
                "Categories",
                self.metadata,
                Column("category_id", INTEGER, primary_key=True),
                Column("tag", String)
            )

            print("Connected to database successfully!")
        except Exception as e:
            print(f'Failed to connect to database: {e}')

    def get_all_categories(self):
        """Lấy tất cả các thể loại"""
        try:
            with self.engine.connect() as conn:
                result = conn.execute(text("SELECT * FROM Categories")).mappings().all()
                categories = [dict(row) for row in result]
                return jsonify(categories)
        except Exception as e:
            return jsonify({'ERROR  ': str(e)}), 500

    def get_category_by_id(self, work_id):
        """Lấy thể loại của sách"""
        try:
            with self.engine.connect() as conn:
                query = text("""
                SELECT * FROM Categories
                JOIN Worktags ON Categories.category_id = Worktags.category_id
                JOIN Works ON Works.work_id = Worktags.work_id
                WHERE Works.work_id = :work_id 
                """)
                result = conn.execute(query, {'work_id': work_id}).mappings().all()
                if not result:
                    return jsonify({'ERROR  ': 'No categories found for this work'}), 404
                categories = [dict(row) for row in result]
                return jsonify(categories)
        except Exception as e:
            return jsonify({'ERROR  ': str(e)}), 500


