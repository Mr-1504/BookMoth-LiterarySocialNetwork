import os
import glob
from .Works_Controller import works_blueprint
from .Profiles_Controller import profiles_blueprint
from .Chapters_Controller import chapters_blueprint
from .Categories_Controller import categories_blueprint

def register_blueprints(app):
    app.register_blueprint(works_blueprint)
    app.register_blueprint(chapters_blueprint)
    app.register_blueprint(profiles_blueprint)
    app.register_blueprint(categories_blueprint)

__all__ = [os.path.basename(f)[:-3] for f in glob.glob(os.path.join(os.path.dirname(__file__), "*.py"))]