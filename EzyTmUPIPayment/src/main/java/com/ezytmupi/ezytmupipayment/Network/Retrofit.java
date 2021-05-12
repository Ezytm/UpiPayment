package com.ezytmupi.ezytmupipayment.Network;

import retrofit2.converter.gson.GsonConverterFactory;

import static com.ezytmupi.ezytmupipayment.Network.CommonUrl.baseUrl;

public class Retrofit {
    retrofit2.Retrofit retrofit=new retrofit2.Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public IGoogleApi api=retrofit.create(IGoogleApi.class);
}
