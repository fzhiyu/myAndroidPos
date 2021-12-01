package com.cuse.myandroidpos.Post;

import com.cuse.myandroidpos.Post.LoginJson.LoginJson;
import com.cuse.myandroidpos.Post.OrderAllJson.OrderAllJson;
import com.cuse.myandroidpos.Post.OrderLastJson.OrderLastJson;
import com.cuse.myandroidpos.Post.OrderRefundJson.OrderRefundJson;
import com.cuse.myandroidpos.Post.OrderSummaryJson.OrderSummaryJson;
import com.cuse.myandroidpos.Post.RefundAllJson.RefundAllJson;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HttpBinService {

    @POST("/user/login")
    @FormUrlEncoded
    Call<LoginJson>login(@Field("staionId") String staionId, @Field("passWord") String passWord,
                        @Field("timestamp") String timestamp, @Field("signature") String signature);

    @POST("/station/oil_order/last")
    @FormUrlEncoded
    Call<OrderLastJson>orderLast(@Field("staionId") String staionId,
                                 @Field("timestamp") String timestamp,
                                 @Field("signature") String signature);

    @POST("/station/oil_order/all")
    @FormUrlEncoded
    Call<OrderAllJson>orderAll(@Field("staionId") String staionId, @Field("startTime") String startTime,
                               @Field("endTime") String endTime, @Field("start") String start,
                               @Field("count") String count, @Field("timestamp") String timestamp,
                               @Field("signature") String signature);

    @POST("/station/oil_order/refund")
    @FormUrlEncoded
    Call<OrderRefundJson>orderRefund(@Field("staionId") String staionId, @Field("oilOrderId") String oilOrderId,
                                     @Field("timestamp") String timestamp, @Field("signature") String signature);

    @POST("/station/oil_order/summary")
    @FormUrlEncoded
    Call<OrderSummaryJson>orderSummary(@Field("staionId") String staionId, @Field("startTime") String startTime,
                                       @Field("endTime") String endTime, @Field("timestamp") String timestamp,
                                       @Field("signature") String signature);

    @POST("/station/refund/all")
    @FormUrlEncoded
    Call<RefundAllJson>RefundAll(@Field("staionId") String staionId, @Field("startTime") String startTime,
                                 @Field("endTime") String endTime, @Field("start") String start,
                                 @Field("count") String count, @Field("timestamp") String timestamp,
                                 @Field("signature") String signature);

    @POST("/station/push/rquest")
    @FormUrlEncoded
    Call<OrderLastJson>push(@Field("staionId") String staionId,
                                 @Field("timestamp") String timestamp,
                                 @Field("signature") String signature);


}
