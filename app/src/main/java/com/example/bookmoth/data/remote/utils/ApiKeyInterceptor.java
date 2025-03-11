//package com.example.bookmoth.data.remote;
//
//import androidx.annotation.NonNull;
//
//import java.io.IOException;
//
//import okhttp3.Interceptor;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class ApiKeyInterceptor implements Interceptor {
//    @NonNull
//    @Override
//    public Response intercept(@NonNull Chain chain) throws IOException {
//        Request original = chain.request();
//
//        // Thêm API Key vào Header
//        Request request = original.newBuilder()
//                .header("Content-Type", "application/json")
//                .header("x-api-key", "minh")
//                .build();
//
//        return chain.proceed(request);
//    }
//}
