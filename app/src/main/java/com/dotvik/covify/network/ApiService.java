package com.dotvik.covify.network;

import com.google.gson.JsonObject;
import com.dotvik.covify.network.models.CreatePatientRequestModel;
import com.dotvik.covify.network.models.CreatePatientResponseModel;
import com.dotvik.covify.network.models.GenericResponseModel;
import com.dotvik.covify.network.models.GetHistoryResponseModel;
import com.dotvik.covify.network.models.GetPatientResponseModel;
import com.dotvik.covify.network.models.GetProfileResponseModel;
import com.dotvik.covify.network.models.LoginRequestModel;
import com.dotvik.covify.network.models.LoginResponseModel;
import com.dotvik.covify.network.models.RegisterRequestModel;
import com.dotvik.covify.network.models.RegisterResponseModel;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("authenticate")
    Call<LoginResponseModel> loginUser(@Body LoginRequestModel body);

    @POST("api/user/register")
    Call<RegisterResponseModel> registerUser(@Body RegisterRequestModel body);

    @POST("api/patient/test/record")
    Call<CreatePatientResponseModel> uploadPatientDetails(@Body CreatePatientRequestModel body);

    @Multipart
    @POST("api/files/upload")
    Call<CreatePatientResponseModel> uploadPatientImage(@Part MultipartBody.Part image, @Header("category") String category, @Header("uniqueId") String uniqueId);

    @GET("api/user/otp/sent")
    Call<GenericResponseModel> sendOtp(@Query("mobileNo") String mobileNo);

    @GET("api/user/otp/verify")
    Call<GenericResponseModel> verifyOtp(@Query("mobileNo") String mobileNo, @Query("otp") String otp);

    @GET("api/patient/test/record/{id}")
    Call<GetPatientResponseModel> getPatientById(@Path("id") String id);

    @GET("api/patient/profile/user")
    Call<GetProfileResponseModel> getProfiles();

    @POST("api/patient/test/record/user/records")
    Call<GetHistoryResponseModel> getPatientHistory(@Body JsonObject body);

    @GET("api/files/download")
    Call<ResponseBody> downloadFile(@Query("category") String category, @Query("uniqueId") String uniqueId);
}
