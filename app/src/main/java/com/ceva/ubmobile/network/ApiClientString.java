package com.ceva.ubmobile.network;

/**
 * Created by brian on 29/09/2016.
 */

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.core.constants.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClientString {

    //public static final String BASE_URL = Constants.NET_URL + Constants.NET_PATH;
    //public static final String BASE_URL = "https://unionmobile.unionbankng.com" + "/ubcustomermobileapi/rest/ubcustomerws/"; //prod url
    //public static final String BASE_URL = "http://196.6.204.47" + "/ubcustomermobileapi/rest/ubcustomerws/"; //preprod url
    //public static final String BASE_URL = "http://196.6.204.54:8089" + "/ubmobileapisec/rest/ubcustomerws/";//uat with sec
    public static final String BASE_URL = "http://196.6.204.54:8089" + "/ubmobileapisec/rest/ubws/"; //uat without sec

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        int KEY_TIMEOUT = 240;
        builder.readTimeout(KEY_TIMEOUT, TimeUnit.SECONDS).connectTimeout(KEY_TIMEOUT, TimeUnit.SECONDS).writeTimeout(KEY_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

// Can be Level.BASIC, Level.HEADERS, or Level.BODY
// See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        /*CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(Constants.HOSTNAME, "sha256/34cUPKZxSs2idm8QLJp51m8pN1aCvFWOSIpWBSgeOLY=")
                .add(Constants.HOSTNAME, "sha256/5kJvNEMw0KjrCAu7eXY5HZdvyCS13BbA0VJG1RSP91w=")
                .add(Constants.HOSTNAME, "sha256/r/mIkG3eEpVdm+u/ko/cwxzOMo1bk4TyHIlByibiA5E=")

                .build();*/

        //web logic ssl
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(Constants.HOSTNAME, "sha256/rqU8h4fgcUQ/pRFO98oK5FD8k9zcSWDoRMDke2hjaQc=")
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