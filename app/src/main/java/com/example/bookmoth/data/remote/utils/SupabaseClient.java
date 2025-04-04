//package com.example.bookmoth.data.remote.utils;
//
//import io.github.jan.supabase.SupabaseClient;
//import io.github.jan.supabase.SupabaseClientBuilder;
//import io.github.jan.supabase.postgrest.Postgrest;
//import io.github.jan.supabase.realtime.Realtime;
//import io.ktor.client.engine.okhttp.OkHttp;
//
//public class SupabaseClientConfig {
//    private static final String SUPABASE_URL = "https://your-project-id.supabase.co";
//    private static final String SUPABASE_KEY = "your-anon-key";
//
//    private static SupabaseClient client;
//
//    public static SupabaseClient getClient() {
//        if (client == null) {
//            client = new SupabaseClientBuilder()
//                    .supabaseUrl(SUPABASE_URL)
//                    .supabaseKey(SUPABASE_KEY)
//                    .httpEngine(new OkHttp())
//                    .install(Postgrest.INSTANCE)
//                    .install(Realtime.INSTANCE)
//                    .build();
//        }
//        return client;
//    }
//}
