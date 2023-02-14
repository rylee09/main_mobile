package com.example.st.arcgiscss.util;

import android.util.Log;

import com.example.st.arcgiscss.constant.Constants;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;


public class RetrofitUtils {

//   private static final String baseUrl = "http://192.168.1.244:3000/users/";
//   private static String baseUrl = "https://" + Constants.IP + ":" + Constants.PORT + "/users/";
//   private static String baseUrl = "http://" + Constants.IP + "/c2/";

    //ZN - 20201209
    //for SSL link
//    private static String baseUrl = "https://"+ Constants.getIP()+":4433/";

    //ZN - 20201209
    //for testing,  to comment off in deployment

    private static String protocol = Constants.getProtocol();
    private static String portD = Constants.getPort();
    private static String ip = Constants.getIP();

    private static String baseUrl;

//    private static String baseUrl = "http://"+ Constants.getIP()+":3333/";


    public static Retrofit retrofit = null;
    private static IRetrofitServer iServer;
    private static OkHttpClient mHttpClient;

    public static String TOKEN = "";

    /**
     *
     * @return
     */
    public static IRetrofitServer getInstance() {
        baseUrl =  protocol + "://" + ip + ":" + portD + "/";
        System.out.println("baseurl is: " + baseUrl);
        if (retrofit == null) {
            synchronized (RetrofitUtils.class) {
                if (retrofit == null) {
                    try {

                        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS);
                        mHttpClient = builder.build();

                        //ZN - 20201209
//                        for testing, to comment for deployment
                        retrofit = new Retrofit.Builder()
                                .baseUrl(baseUrl)
                                .client(mHttpClient)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        iServer = retrofit.create(IRetrofitServer.class);

                        //ZN - 20200710
                        //for SSL
//                        retrofit = new Retrofit.Builder()
//                                .baseUrl(baseUrl)
//                                .client(getUnsafeOkHttpClient().build())
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .build();
//                        iServer = retrofit.create(IRetrofitServer.class);
                    } catch (Exception e) {
                        Log.e("TEST", e.getMessage());
                    }
                }
            }
        }
        return iServer;
    }

    public interface IRetrofitServer<T> {

        String GETINCIDENTLOCATIONLIST = "getIncidentLocationList";

        String GETINCIDENTRECORD = "getIncidentRecord";

        String GETINCIDENTTYPELIST = "getIncidentTypeList";

        String CREATEPOSITIONRECORD = "createPositionRecord";

        String CREATEINCIDENT = "createIncident";

        String DRIVERLOGIN = "driverLogin";

        //ZN - 20200604
        String SETINCIDENTACK = "setIncidentAck";

        //ZN - 20200607
        String SETINCIDENTCOMPLETE = "setIncidentComplete";

        String SETRESPONDERATBASE= "setresponderAtbase";
        //ZN - 20201203
        String SETINCIDENTSTATUS = "setIncidentStatus";

        //ZN - 20201213
        String SETTIMESTAMP = "setTimestamp";

        //ZN - 20210123
        String SENDRESPONDERROUTE = "setResponderRoute";

        //ZN - 20210204
        String UPDATEINCIDENTPOC = "updateIncidentPOC";

        //ZN - 20210215
        String SENDRESPONDERETA = "setResponderETA";

        //ZN - 20210407
        String PROCESSFUELTOPUPREQUEST = "processFuelTopUpRequest";

        //ZN - 20210417
        String GETMAINTENANCEAPPROVAL = "getMaintenanceApproval";

        //ZN - 20210503
        String DRIVERLOGOUT = "driverLogout";

        //ZN - 20210616
        String UPDATERESPONDERINCIDENTINFO = "updateResponderIncidentInfo";

        //ZN - 20210627
        String PROCESSSYSTEMREADY = "processSystemReady";

        //ZN - 20210711
        String CHECKUPDATEPOC = "checkUpdatePOC";
        String RESETUPDATEPOC = "resetUpdatePOC";

        //ZN - 20210719
        String GETBACKUPEASACTIVATED = "getBackupEASActivated";

        //ZN - 20210819
        //ZN - 20211124 get camp responder peers - modified to get by Type
        String GETEASUSERSTATUSBYTYPE = "getEASUserStatusByType";

        //ZN - 20210922
        String GETINCOMPLETEINCIDENTRECORDBYUSERID = "getIncompleteIncidentRecordByUserId";

        //ZN - 20211201 cancel task assignment
        String GETCANCELLEDINCIDENT = "getCancelledIncident";

        String GETPEERREADINESSSTATUS = "getPeerReadinessStatus";

        //ZN - 20220208 check for valid IMEI
        String GETMATCHINGIMEI = "getMatchingIMEI";

        //ZN - 20220322 smart search feature for medical centre / hospital
        String GETEVACLOCATIONLIST = "getEvacLocationList";

        //ZN - 20220224 new send-out feature - add to incident record
        String CREATESENDOUTINCIDENT = "createSendOutIncident";

        //ZN - 20220224 new send-out feature - add to incident record
        String COMPLETESENDOUTINCIDENT = "completeSendOutIncident";

        //ZN - 20220606 store and forward
        String UPDATEACTIVATIONTIMING = "updateActivationTiming";

        //ZN - 20220629 sending of logs to server
        String UPLOADLOG = "uploadLog";

        //ZN - 20220930 for Export create Incident
        String CREATEEXPORTINCIDENT = "createExportIncident";

        @GET(GETINCIDENTLOCATIONLIST)
        Call<JsonObject> getIncidentLocationList();

        @GET(GETINCIDENTRECORD)
        Call<JsonObject> getIncidentRecord(@QueryMap Map<String,String> maps);

        @GET(GETINCIDENTTYPELIST)
        Call<JsonObject> getIncidentTypeList();

        @FormUrlEncoded
        @POST(CREATEPOSITIONRECORD)
        Call<ResponseBody> createPositionRecord(@FieldMap Map<String,String> maps);

        @FormUrlEncoded
        @POST(CREATEINCIDENT)
        Call<ResponseBody> createIncident(@FieldMap Map<String,String> maps);

        @FormUrlEncoded
        @POST(DRIVERLOGIN)
        Call<JsonObject> driverLogin(@FieldMap Map<String, String> maps);

        //ZN - 20200604
        @FormUrlEncoded
        @POST(SETINCIDENTACK)
        Call<JsonObject> setIncidentAck(@FieldMap Map<String, String> maps);

        //ZN - 20200607
        @FormUrlEncoded
        @POST(SETINCIDENTCOMPLETE)
        Call<JsonObject> setIncidentComplete(@FieldMap Map<String, String> maps);

        @FormUrlEncoded
        @POST(SETRESPONDERATBASE)
        Call<JsonObject> setresponderAtbase(@FieldMap Map<String, String> maps);

        //ZN - 20201203
        @FormUrlEncoded
        @POST(SETINCIDENTSTATUS)
        Call<JsonObject> setIncidentStatus(@FieldMap Map<String, String> maps);

        //ZN - 20201213
        @FormUrlEncoded
        @POST(SETTIMESTAMP)
        Call<JsonObject> setTimestamp(@FieldMap Map<String, String> maps);

        //ZN - 20210123
        @FormUrlEncoded
        @POST(SENDRESPONDERROUTE)
        Call<JsonObject> setResponderRoute(@FieldMap Map<String, String> maps);

        //ZN - 20210204
        @FormUrlEncoded
        @POST(UPDATEINCIDENTPOC)
        Call<JsonObject> updateIncidentPOC(@FieldMap Map<String, String> maps);

        //ZN - 20210215
        @FormUrlEncoded
        @POST(SENDRESPONDERETA)
        Call<JsonObject> setResponderETA(@FieldMap Map<String, String> maps);

        //ZN - 20210407
        @FormUrlEncoded
        @POST(PROCESSFUELTOPUPREQUEST)
        Call<JsonObject> processFuelTopUpRequest(@FieldMap Map<String, String> maps);

        //ZN - 20210417
        @FormUrlEncoded
        @POST(GETMAINTENANCEAPPROVAL)
        Call<JsonObject> getMaintenanceApproval(@FieldMap Map<String, String> maps);

        //ZN - 20210503
        @FormUrlEncoded
        @POST(DRIVERLOGOUT)
        Call<JsonObject> driverLogout(@FieldMap Map<String, String> maps);

        //ZN - 20210616
        @FormUrlEncoded
        @POST(UPDATERESPONDERINCIDENTINFO)
        Call<JsonObject> updateResponderIncidentInfo(@FieldMap Map<String, String> maps);

        //ZN - 20210627
        @FormUrlEncoded
        @POST(PROCESSSYSTEMREADY)
        Call<JsonObject> processSystemReady(@FieldMap Map<String, String> maps);

        //ZN - 20210711
        @FormUrlEncoded
        @POST(CHECKUPDATEPOC)
        Call<JsonObject> checkUpdatePOC(@FieldMap Map<String, String> maps);

        //ZN - 20210711
        @FormUrlEncoded
        @POST(RESETUPDATEPOC)
        Call<JsonObject> resetUpdatePOC(@FieldMap Map<String, String> maps);

        //ZN - 20210719
        @FormUrlEncoded
        @POST(GETBACKUPEASACTIVATED)
        Call<JsonObject> getBackupEASActivated(@FieldMap Map<String, String> maps);

        //ZN - 20211124 get camp responder peers - modified to get by Type
        @FormUrlEncoded
        @POST(GETEASUSERSTATUSBYTYPE)
//        Call<ArrayList<UserStatus>> getEASUserStatus();
        Call<JsonObject> getEASUserStatusByType(@FieldMap Map<String, String> maps);

        //ZN - 20210921
        @FormUrlEncoded
        @POST(GETINCOMPLETEINCIDENTRECORDBYUSERID)
        Call<JsonObject> getIncompleteIncidentRecordByUserId(@FieldMap Map<String, String> maps);

        //ZN - 20211201 cancel task assignment
        @FormUrlEncoded
        @POST(GETCANCELLEDINCIDENT)
        Call<JsonObject> getCancelledIncident(@FieldMap Map<String, String> maps);

        //ZN - 20220125 consolidate peer readiness method call
        @FormUrlEncoded
        @POST(GETPEERREADINESSSTATUS)
        Call<JsonObject> getPeerReadinessStatus(@FieldMap Map<String, String> maps);

        //ZN - 20220208 check for valid IMEI
        @FormUrlEncoded
        @POST(GETMATCHINGIMEI)
        Call<JsonObject> getMatchingIMEI(@FieldMap Map<String, String> maps);

        //ZN - 20220322 smart search feature for medical centre / hospital
        @GET(GETEVACLOCATIONLIST)
        Call<JsonObject> getEvacLocationList();

        //ZN - 20220224 new send-out feature - add to incident record
        @FormUrlEncoded
        @POST(CREATESENDOUTINCIDENT)
        Call<JsonObject> createSendOutIncident(@FieldMap Map<String, String> maps);

        //ZN - 20220224 new send-out feature - add to incident record
        @FormUrlEncoded
        @POST(COMPLETESENDOUTINCIDENT)
        Call<JsonObject> completeSendOutIncident(@FieldMap Map<String, String> maps);

        //ZN - 20220606 store and forward
        @FormUrlEncoded
        @POST(UPDATEACTIVATIONTIMING)
        Call<JsonObject> updateActivationTiming(@FieldMap Map<String, String> maps);

        //ZN - 20220629 sending of logs to server
        @Multipart
        @POST(UPLOADLOG)
        Call<JsonObject> uploadLog(@PartMap Map<String, RequestBody> maps);

        //ZN - 20220930 for Export create Incident
        @FormUrlEncoded
        @POST(CREATEEXPORTINCIDENT)
        Call<ResponseBody> createExportIncident(@FieldMap Map<String,String> maps);

    }

    //ZN - 20200710
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            //ZN - 20210606 token implementation
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", TOKEN)
                            .build();
                    return chain.proceed(newRequest);
                }
            });

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
