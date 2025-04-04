from post_api import app
import controller.profile_controller
import controller.model_blood

if __name__ == '__main__':
    app.run(host="192.168.218.34", port=5000, debug=True)