package com.cuse.myandroidpos.Post;

import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.Post.OrderAllJson.OrderAllJson;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.Post.OrderRefundJson.OrderRefundJson;
import com.cuse.myandroidpos.Post.OrderSummaryJson.OrderSummaryJson;
import com.cuse.myandroidpos.Post.Push.Push;
import com.cuse.myandroidpos.Post.RefundAllJson.RefundAllJson;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface HttpBinService {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("user/login")
    Call<LoginJson>login(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/oil_order/last")
    Call<OrderLastJson>orderLast(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/oil_order/all")
    Call<OrderAllJson>orderAll(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/oil_order/refund")
    Call<OrderRefundJson>orderRefund(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/oil_order/summary")
    Call<OrderSummaryJson>orderSummary(@Body RequestBody requestBody);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/refund/all")
    Call<RefundAllJson>refundAll(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("station/push/rquest")
    Call<Push>push(@Body RequestBody requestBody);


}
