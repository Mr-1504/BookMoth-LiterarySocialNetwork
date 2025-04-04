from StoreAPI.Model.Profiles_Model import ProfilesModel
from flask import Blueprint

obj = ProfilesModel()
profiles_blueprint = Blueprint('profiles_blueprint', __name__)

@profiles_blueprint.route('/profiles/<int:work_id>', methods=['GET'])
def get_profiles_by_work_id(work_id):
    return obj.get_profiles_by_work_id(work_id)