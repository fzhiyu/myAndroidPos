package com.cuse.myandroidpos.Post;

import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.Post.OrderAllJson.OrderAllJson;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.Post.OrderAllJson.orderAllRequest;
import com.cuse.myandroidpos.Post.OrderRefundJson.OrderRefundJson;
import com.cuse.myandroidpos.Post.OrderSummaryJson.OrderSummaryJson;
import com.cuse.myandroidpos.Post.Push.Push;
import com.cuse.myandroidpos.Post.RefundAllJson.RefundAllJson;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpBinService {

    @POST("user/login")
    Call<LoginJson>login(@Query("stationId") String stationId,
                         @Query("passWord") String passWord,
                         @Query("timestamp") String timestamp,
                         @Query("imei") String imei,
                         @Query("signature") String signature);

    @POST("station/oil_order/last")
    Call<OrderLastJson> orderLast(@Query("token") String token,
                                  @Query("timestamp") String timestamp,
                                  @Query("signature") String signature);

//    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/oil_order/all")
    Call<OrderAllJson>orderAll(@Query("token") String token,
                               @Query("startTime") String startTime,
                               @Query("endTime") String endTime,
                               @Query("start") String start,
                               @Query("count") String count,
                               @Query("timestamp") String timestamp,
                               @Query("signature") String signature);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/oil_order/refund")
    Call<OrderRefundJson>orderRefund(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/oil_order/summary")
    Call<OrderSummaryJson>orderSummary(@Query("token") String token,
                                       @Query("startTime") String startTime,
                                       @Query("endTime") String endTime,
                                       @Query("timestamp") String timestamp,
                                       @Query("signature") String signature);

    @POST("station/refund/all")
    Call<RefundAllJson>refundAll(@Query("token") String token,
                                 @Query("startTime") String startTime,
                                 @Query("endTime") String endTime,
                                 @Query("start") String start,
                                 @Query("count") String count,
                                 @Query("timestamp") String timestamp,
                                 @Query("signature") String signature);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/push/rquest")
    Call<Push>push(@Body RequestBody requestBody);
}
