package com.samuelbernard.osis_election.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("mesin")
    Call<ResponseBody> userLogin(
            @Field("username") String username,
            @Field("password") String password
    );

    @POST("kandidat")
    Call<ResponseBody> addVote(
            @Field("id_kandidat") String id_kandidat,
            @Field("id_mesin") String id_mesin,
            @Field("id_pemilih") String id_pemilih
    );

    @POST("pemilih")
    Call<ResponseBody> voterVoted(
            @Field("id_pemilih") String id_pemilih,
            @Field("status") String status
    );
}