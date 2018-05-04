package com.ceva.ubmobile.network;

import me.toptas.rssconverter.RssFeed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by brian on 11/04/2017.
 */

public interface RssService {
    /**
     * No baseUrl defined. Each RSS Feed will be consumed by it's Url
     *
     * @param url RSS Feed Url
     * @return Retrofit Call
     */
    @GET
    Call<RssFeed> getRss(@Url String url);

    @GET
    Call<String> getData(@Url String url);
}