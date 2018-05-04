package com.ceva.ubmobile.network;

/**
 * Created by brian on 26/04/2017.
 */

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.core.constants.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class GeoAPIClient {

    public static final String BASE_URL = Constants.NET_URL + Constants.GEO_CONTEXT;
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(180, TimeUnit.SECONDS).connectTimeout(180, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

// Can be Level.BASIC, Level.HEADERS, or Level.BODY
// See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(Constants.HOSTNAME, "sha256/34cUPKZxSs2idm8QLJp51m8pN1aCvFWOSIpWBSgeOLY=")
                .add(Constants.HOSTNAME, "sha256/5kJvNEMw0KjrCAu7eXY5HZdvyCS13BbA0VJG1RSP91w=")
                .add(Constants.HOSTNAME, "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")

                .build();

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (BuildConfig.DEBUG) {
            builder.networkInterceptors().add(httpLoggingInterceptor);
        }
        builder.certificatePinner(certificatePinner);
        OkHttpClient okHttpClient = builder.build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

}