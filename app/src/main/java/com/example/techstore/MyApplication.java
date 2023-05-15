package com.example.techstore;

import android.app.Application;

import com.example.techstore.config.StaticConfig;
import com.example.techstore.retrofit.ApiService;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.paypal.http.Environment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
//        PayPalCheckout.setConfig(new CheckoutConfig(
//                this,
//                YOUR_CLIENT_ID,
//                Environment.SANDBOX,
//                BuildConfig.APPLICATION_ID + "://paypalpay",
//                CurrencyCode.USD,
//                UserAction.PAY_NOW
//        ));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
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
        // Gọi API ở đây để thực hiện công việc của bạn
    }

}
