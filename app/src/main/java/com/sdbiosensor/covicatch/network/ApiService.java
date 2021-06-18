package com.sdbiosensor.covicatch.network;

import com.sdbiosensor.covicatch.network.models.CreatePatientRequestModel;
import com.sdbiosensor.covicatch.network.models.CreatePatientResponseModel;
import com.sdbiosensor.covicatch.network.models.GenericResponseModel;
import com.sdbiosensor.covicatch.network.models.LoginRequestModel;
import com.sdbiosensor.covicatch.network.models.LoginResponseModel;
import com.sdbiosensor.covicatch.network.models.RegisterRequestModel;
import com.sdbiosensor.covicatch.network.models.RegisterResponseModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @POST("authenticate")
    Call<LoginResponseModel> loginUser(@Body LoginRequestModel body);

    @POST("user/register")
    Call<RegisterResponseModel> registerUser(@Body RegisterRequestModel body);

    @POST("patient/test/record")
    Call<CreatePatientResponseModel> uploadPatientDetails(@Body CreatePatientRequestModel body);

    @Multipart
    @POST("files/upload")
    Call<CreatePatientResponseModel> uploadPatientImage(@Part MultipartBody.Part image, @Header("category") String category, @Header("uniqueId") String uniqueId);

    @GET("user/otp/sent")
    Call<GenericResponseModel> sendOtp(@Query("mobileNo") String mobileNo);

    @GET("user/otp/verify")
    Call<GenericResponseModel> verifyOtp(@Query("mobileNo") String mobileNo, @Query("otp") String otp);
}
