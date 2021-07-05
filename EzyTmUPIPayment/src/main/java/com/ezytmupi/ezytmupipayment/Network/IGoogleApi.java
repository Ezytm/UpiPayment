package com.ezytmupi.ezytmupipayment.Network;



import com.ezytmupi.ezytmupipayment.models.WalletRequestResponse;
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


    //Vendor me retailer upi id ja rhi hai use vendor ki hi bhejni hai
    //OurRefId me Txn id bhejni hai jo WalletRequest se milti hai
    //UpiTxnStatus me txn id ja rhi hai us me status bhejna hai




    @FormUrlEncoded
    @POST("WalletRequest")
    Call<WalletRequestResponse> WalletRequest(@Field("userid") String userid,
                                              @Field("UToken") String UToken,
                                              @Field("Amount") String Amount,
                                              @Field("ClientRefId") String ClientRefId,
                                              @Field("RetailerUserID") String RetailerUserID,
                                              @Field("CustomerName") String CustomerName,
                                              @Field("RetailerUpiID") String RetailerUpiID,
                                              @Field("PhoneInfo") String PhoneInfo,
                                              @Field("IPadd") String IPadd);

}

