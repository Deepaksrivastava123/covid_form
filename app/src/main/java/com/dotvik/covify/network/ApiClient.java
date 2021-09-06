package com.dotvik.covify.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dotvik.covify.events.CloseAllScreens;
import com.dotvik.covify.screens.SplashActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.utils.SharedPrefUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiService baseInstance = null;

    public static ApiService getBaseInstance(Context context) {
        if (baseInstance == null) {
            OkHttpClient okClient = new OkHttpClient.Builder()
                    .addInterceptor(new CustomInterceptor(context))
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(Constants.BASE_URL)
                    .client(okClient)
                    .build();
            baseInstance = retrofit.create(ApiService.class);
        }
        return baseInstance;
    }

    public static class CustomInterceptor implements Interceptor {
        Context context;
        private Response response;

        CustomInterceptor(Context context){
            this.context=context;
        }
        @Override
        public Response intercept(final Chain chain) throws IOException {
            try {
                if (context != null && CheckConnection.isConnected(context)) {
                    final Request original = chain.request();

                    // Request customization: add request headers
                    final Request.Builder requestBuilder = original.newBuilder()
                            .method(original.method(), original.body());

                    String token = SharedPrefUtils.getInstance(context).getString(Constants.PREF_LOGGED_IN_TOKEN, "");
                    if (token.length() > 0) {
                        requestBuilder.addHeader("Authorization", "Bearer " + SharedPrefUtils.getInstance(context).getString(Constants.PREF_LOGGED_IN_TOKEN, ""));
                    }
                    final Request modifiedRequest = requestBuilder.build();
                    response = chain.proceed(modifiedRequest);
                    //TODO uncomment 2 lines to see response, download PDF does not work when this is uncommented
//                    Object[] clones = cloneResponseBody(response);
//                    response = (Response) clones[1];
<<<<<<< HEAD:app/src/main/java/com/sdbiosensor/covicatch/network/ApiClient.java

                    if (response.code() == 401 || response.code() == 403) {
                        Log.d("Covi-Catch", "Authorization error, relogin needed");
                    }

=======
                    if (response.code() == 401 || response.code() == 403) {
                        SharedPrefUtils.getInstance(context).resetAll();
                        EventBus.getDefault().post(new CloseAllScreens());
                        context.startActivity(new Intent(context, SplashActivity.class));
                    }
>>>>>>> 5a38f3ac6be6601e8ce6e8b5c394b10bcbaf8246:app/src/main/java/com/dotvik/covify/network/ApiClient.java
                    return response;
                } else {
                    return chain.proceed(chain.request());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return chain.proceed(chain.request());
            }
        }

        Object[] cloneResponseBody(Response response) {
            Object[] clones = new Object[2];
            try {
                MediaType contentType = null;
                String bodyString = "";
                if (response.body() != null) {
                    contentType = response.body().contentType();
                    bodyString = response.body().string();
                }

                Log.d("Debug", "Response body : " + bodyString);

                ResponseBody body = ResponseBody.create(contentType, bodyString);
                ResponseBody body1 = ResponseBody.create(contentType, bodyString);
                response = response.newBuilder().body(body1).build();

                clones[0] = body;
                clones[1] = response;
            } catch (Exception e) {
                clones[0] = response.body();
                clones[1] = response;
                e.printStackTrace();
            }
            return clones;
        }

    }
}
