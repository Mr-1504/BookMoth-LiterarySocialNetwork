from flask import Blueprint
from StoreAPI.Model.Works_Model import WorksModel

works_blueprint = Blueprint('works', __name__)
obj = WorksModel()

@works_blueprint.route('/works',methods=['GET'])
def get_works():
    return obj.get_all_works()


@works_blueprint.route('/works/<int:work_id>',methods=['GET'])
def get_work(work_id):
    return obj.get_work_by_id(work_id)

@works_blueprint.route('/new_releases',methods=['GET'])
def get_newReleases():
    return obj.get_newReleases()

@works_blueprint.route('/popular',methods=['GET'])
def get_popular():
    return obj.get_popular()

@works_blueprint.route('/works/tags/<string:tag>', methods=['GET'])
def get_work_by_tag(tag):
    return obj.get_work_by_tag(tag)

@works_blueprint.route('/search/<string:title>', methods=['GET'])
def get_work_by_title(title):
    return obj.get_work_by_title(title)