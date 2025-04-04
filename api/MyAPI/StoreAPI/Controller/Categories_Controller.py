from flask import Blueprint
from StoreAPI.Model.Categories_Model import CategoriesModel

obj = CategoriesModel()
categories_blueprint = Blueprint('categories_blueprint', __name__)

@categories_blueprint.route('/categories', methods=['GET'])
def get_categories():
    return obj.get_all_categories()

@categories_blueprint.route('/categories/<int:work_id>', methods=['GET'])
def get_categories_by_work_id(work_id):
    return obj.get_category_by_id(work_id)