package com.ezytmupi.ezytmupipayment.Network;



import com.ezytmupi.ezytmupipayment.models.WalletResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IGoogleApi {

    @FormUrlEncoded
    @POST("WalletResponse")
    Call<WalletResponse> WalletResponse(@Field("userid") String userid,
                                        @Field("UToken") String UToken,
                                        @Field("Amount") String Amount,
                                        @Field("VendorUpiID") String VendorUpiID,
                                        @Field("OurRefID") String OurRefID,
                                        @Field("RetailerUpiID") String RetailerUpiID,
                                        @Field("UpiResponse") String UpiResponse,
                                        @Field("UpiTxnStatus") String UpiTxnStatus);



}

