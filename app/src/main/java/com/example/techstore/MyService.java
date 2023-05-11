package com.example.techstore;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.techstore.config.StaticConfig;
import com.example.techstore.retrofit.ApiService;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyService extends Service {

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // Make API call here
//
//
//        // Make API call here
//        String logoutapi = new LocalNetwork().getUrl()+"/auth/logout/";
//        ApiService apiService = new Retrofit.Builder()
//                .baseUrl(logoutapi)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(ApiService.class);
//        Call<String> call = apiService.logout(StaticConfig.CURRENT_KEY,"customer");
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                FirebaseAuth.getInstance().signOut();
//                LoginManager.getInstance().logOut();
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                // handle failure
//            }
//        });
//
//
//        // Return START_NOT_STICKY to indicate that the Service should be stopped after onStartCommand() returns
//        return super.onStartCommand(intent, flags, startId);
//    }
    @Override
    public void onDestroy() {
        // Gọi API ở đây để thực hiện công việc của bạn
        String logoutapi = new LocalNetwork().getUrl()+"/auth/logout/";
        ApiService apiService = new Retrofit.Builder()
                .baseUrl(logoutapi)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
        Call<String> call = apiService.logout(StaticConfig.CURRENT_KEY,"customer");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // handle failure
            }
        });
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}