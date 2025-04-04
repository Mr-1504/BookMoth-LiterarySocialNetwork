from flask import Flask
from StoreAPI.Controller import register_blueprints

app = Flask(__name__, static_folder="covers")

register_blueprints(app)
@app.route('/')
def hello_world():
    return 'Hello World!'


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0',port=8000)
