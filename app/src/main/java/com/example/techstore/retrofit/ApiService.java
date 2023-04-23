package com.example.techstore.retrofit;



import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/logout")
    Call<String> logout(@Query("role") String role, @Query("key") String key);

}
