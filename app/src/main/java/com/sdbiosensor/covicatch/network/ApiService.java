package com.sdbiosensor.covicatch.network;

import com.sdbiosensor.covicatch.network.models.CreatePatientRequestModel;
import com.sdbiosensor.covicatch.network.models.CreatePatientResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @POST("patient")
    Call<CreatePatientResponseModel> uploadPatientDetails(@Body CreatePatientRequestModel body);

    @Multipart
    @POST("files/upload")
    Call<CreatePatientResponseModel> uploadPatientImage(@Part MultipartBody.Part image, @Header("category") String category, @Header("uniqueId") String uniqueId);
}
