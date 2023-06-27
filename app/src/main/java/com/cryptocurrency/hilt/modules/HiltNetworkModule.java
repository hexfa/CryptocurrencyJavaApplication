package com.cryptocurrency.hilt.modules;


import com.cryptocurrency.repository.AppRepository;
import com.cryptocurrency.retrofit.RequestApi;
import com.cryptocurrency.room.RoomDao;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class HiltNetworkModule {

    String Base_Url = "https://pro-api.coinmarketcap.com";
    @Provides
    @Singleton
    OkHttpClient ProvideOkHttpClient(){
        return new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("X-CMC_PRO_API_KEY","2f7b1b18-cbeb-4861-8515-bca6b05e3777")
                        .build();
                return chain.proceed(request);
            }
        }).build();
    }

    @Provides
    @Singleton
    Retrofit ProvideRetrofit(OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .baseUrl(Base_Url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    RequestApi provideRequestApi(Retrofit retrofit){
        return retrofit.create(RequestApi.class);
    }

    @Provides
    @Singleton
    AppRepository ProvideAppRepository(RequestApi requestApi,
                                       RoomDao roomDao){
        return new AppRepository(requestApi,roomDao);
    }
}
