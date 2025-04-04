from flask import Blueprint
from StoreAPI.Model.Chapters_Model import ChaptersModel

obj = ChaptersModel()
chapters_blueprint = Blueprint('chapters_blueprint', __name__)

@chapters_blueprint.route('/chapters/<int:work_id>', methods=['GET'])
def get_chapters(work_id):
    return obj.get_chapters_by_work(work_id)

