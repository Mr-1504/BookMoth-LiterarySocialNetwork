from post_api import app
@app.route('/product')
def product():
    return "product"