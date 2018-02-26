package com.fortvision.minisites.network;

import com.fortvision.minisites.model.FVButton;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FVServerAPI {

    @POST("3/pageview/{publisherId}")
    Call<FVButton> getButton(@Path("publisherId") String publisherId, @Header("UID") String userId,
                             @Header("IP") String ip, @Header("user-agent") String userAgent);

    @FormUrlEncoded
    @POST("user_loc")
    Call<ResponseBody> reportLocation(@Header("UID") String userId, @Header("IP") String ip, @Header("user-agent") String userAgent,
                                      @Field("lat") String lat, @Field("long") String lon);

    @FormUrlEncoded
    @POST("battery_charge")
    Call<ResponseBody> reportBattery(@Header("UID") String userId, @Header("IP") String ip, @Header("user-agent") String userAgent,
                                     @Field("is_charging") int isCharging, @Field("battery_level") int batteryLevel);

    @POST("impr/{campaignId}/{designId}")
    Call<ResponseBody> reportImpression(@Path("campaignId") String campaignId, @Path("designId") String designId,
                                        @Header("UID") String userId, @Header("IP") String ip, @Header("user-agent") String userAgent);

    @POST("click/{campaignId}/{designId}")
    Call<ResponseBody> reportClick(@Path("campaignId") String campaignId, @Path("designId") String designId,
                                   @Header("UID") String userId, @Header("IP") String ip, @Header("user-agent") String userAgent);

    @POST("trash/{publisherId}/{campaignId}/{designId}/{eventType}")
    Call<ResponseBody> reportTrash(@Path("publisherId") String publisherId, @Path("campaignId") String campaignId, @Path("designId") String designId,
                                   @Path("eventType") int eventType,
                                   @Header("UID") String userId, @Header("IP") String ip, @Header("user-agent") String userAgent);

    @FormUrlEncoded
    @POST("video_event/{campaignId}/{designId}")
    Call<ResponseBody> reportVideoEvent(@Path("campaignId") String campaignId, @Path("designId") String designId,
                                        @Header("UID") String userId, @Header("IP") String ip, @Header("user-agent") String userAgent,
                                        @Field("player_id") String playerSize, @Field("event") String eventType,
                                        @Field("seconds_played") int secPlayed, @Field("seconds_in_video") int posInVideo);

    @FormUrlEncoded
    @POST("video_stats/{campaignId}/{designId}")
    Call<ResponseBody> reportVideoStats(@Path("campaignId") String campaignId, @Path("designId") String designId,
                                        @Header("UID") String userId, @Header("IP") String ip, @Header("user-agent") String userAgent,
                                        @Field("player_id") String playerSize, @Field("event") String eventType,
                                        @Field("seconds_played") int secPlayed, @Field("seconds_in_video") int posInVideo,
                                        @Field("stalled") int stalled, @Field("waiting") int waiting);

    @GET("sdk_assets")
    Call<JsonObject> getImages();

}
