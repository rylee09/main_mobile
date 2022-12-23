package com.example.st.arcgiscss.util;

import android.util.Log;

import com.example.st.arcgiscss.constant.Constants;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public class UploadImageUtil {
    private static String baseUrl = "http://"+ Constants.getIP()+":3000/";
    //    private static String baseUrl = "http://"+ Constants.getHttpIP()+":"+Constants.getHttpPort()+"/";
//   private static String baseUrl = "http://" + Constants.IP + ":" + Constants.PORT + "/site/ws/cms/";
    public static Retrofit retrofit = null;
    private static IRetrofitServer iServer;
    private static OkHttpClient mHttpClient;

    /**
     *
     * @return
     */
    public static IRetrofitServer getInstance() {
        if (retrofit == null) {
            synchronized (UploadImageUtil.class) {
                if (retrofit == null) {
                    try {

                        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS);
                        mHttpClient = builder.build();
                        retrofit = new Retrofit.Builder()
                                .baseUrl(baseUrl)
                                .client(mHttpClient)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        iServer = retrofit.create(IRetrofitServer.class);
                    } catch (Exception e) {
                        Log.e("TEST", e.getMessage());
                    }
                }
            }
        }
        return iServer;
    }


    public interface IRetrofitServer<T> {

        String UPLOAD = "upload";

        @Multipart
        @POST(UPLOAD)
        Call<ResponseBody> uploadFile(@PartMap Map<String, RequestBody> maps);


    }
}
