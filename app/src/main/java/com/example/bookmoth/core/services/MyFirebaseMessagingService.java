package com.example.bookmoth.core.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.data.model.notification.FcmTokenRequest;
import com.example.bookmoth.data.remote.notification.NotificationApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Dịch vụ Firebase Cloud Messaging (FCM) để xử lý thông báo push.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Được gọi khi thiết bị nhận một token FCM mới.
     *
     * @param token Token FCM mới của thiết bị.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "New Token: " + token);
        sendTokenToServer(token, this);
    }


    /**
     * Lấy token FCM hiện tại và cập nhật nó lên server.
     *
     * @param context Context của ứng dụng.
     */
    public void updateTokenToServer(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Lấy token thất bại", task.getException());
                        return;
                    }
                    String token = task.getResult();

                    sendTokenToServer(token, context);
                });
    }


    /**
     * Gửi token FCM lên server cùng với ID của thiết bị.
     *
     * @param token   Token FCM cần gửi lên server.
     * @param context Context của ứng dụng.
     */
    private void sendTokenToServer(String token, Context context) {
        String deviceId = getDeviceId(context);
        NotificationApiService apiService = RetrofitClient
                .getAspServerRetrofit()
                .create(NotificationApiService.class);
        apiService.registerDevice(new FcmTokenRequest(token, deviceId))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("FCM", "Token sent to server!");
                        } else {
                            Log.e("FCM", "Failed to send token to server!");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("FCM", "Failed to send token to server!");
                    }
                });
    }


    /**
     * Xử lý thông báo khi nhận được từ Firebase.
     *
     * @param remoteMessage Thông báo nhận được từ Firebase.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            showNotification(title, message);
        } else if (remoteMessage.getData().size() > 0) {
            // Nếu là Data Message
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            showNotification(title, message);
        }
    }

    /**
     * Hiển thị thông báo lên thiết bị.
     *
     * @param title   Tiêu đề của thông báo.
     * @param message Nội dung của thông báo.
     */
    private void showNotification(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("FCM", "Chưa có quyền gửi thông báo!");
                return;
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FCM_CHANNEL")
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    /**
     * Lấy ID của thiết bị.
     *
     * @param context Context của ứng dụng.
     * @return ID của thiết bị.
     */
    private static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
