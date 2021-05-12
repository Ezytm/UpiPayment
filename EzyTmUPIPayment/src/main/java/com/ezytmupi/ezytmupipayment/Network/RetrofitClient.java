package com.ezytmupi.ezytmupipayment.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static retrofit2.Retrofit retrofit = null;

    public static Retrofit getClient(String baseurl){


        if(retrofit == null){



          /*  retrofit = new Retrofit.Builder().baseUrl(baseurl);
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();*/

            retrofit = new retrofit2.Retrofit.Builder().baseUrl(baseurl)
                   /// .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
