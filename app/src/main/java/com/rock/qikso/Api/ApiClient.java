package com.rock.qikso.Api;

import com.rock.reward.volleyWebservice.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rockers on 14/3/17.
 */

public class ApiClient {
    public static final String BASE_URL = Constants.WebServiceUrls.API_DOMAIN;


    // public static final String BASE_URL = Constants.WebServiceUrls.API_DOMAIN;
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        if (retrofit == null) {

            OkHttpClient client =
                    new OkHttpClient.Builder().build();

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();




        }

        return retrofit;
    }
}
