package com.centerprime.tronsdk.sdk;

import java.util.HashMap;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by CenterPrime on 2020/12/14.
 */
public interface TronApi {

    @GET("/api/account")
    Single<HashMap<String, Object>> getTrx20Balance(@Query("address") String address,
                                                    @Query("tokenId") String tokenId);

    @GET("/api/account")
    Single<HashMap<String, Object>> getTrxBalance(@Query("address") String address);

    @GET("/api/new/transfer")
    Single<HashMap<String, Object>> getTransactionAddress(
            @Query("sort") String sort,
            @Query("count") String count,
            @Query("limit") int limit,
            @Query("start") int start,
            @Query("address") String toAddress
    );

    @GET("/api/transaction-info")
    Single<HashMap<String, Object>> getTransactionInfo(
            @Query("hash") String hash
    );
}
    //https://apilist.tronscanapi.com/api/new/transfer?sort=-timestamp&count=true&limit=20&start=0&address=TUXikxmsRW1ducWAg2th7QQeWmQA9N1MEP}
//https://apilist.tronscanapi.com/api/transaction-info?hash=738472dd8feab03d1ac87c159f4ca01abf289bcb8e02ce5b83afe32cf667d05d
