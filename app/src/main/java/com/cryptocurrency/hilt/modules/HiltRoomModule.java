package com.cryptocurrency.hilt.modules;


import android.content.Context;


import com.cryptocurrency.room.AppDatabase;
import com.cryptocurrency.room.RoomDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class HiltRoomModule  {

    @Provides
    @Singleton
    AppDatabase ProvideAppDatabase(@ApplicationContext Context context){
        return AppDatabase.getInstance(context);
    }

    @Provides
    @Singleton
    RoomDao provideRoomDao(AppDatabase appDatabase){
        return appDatabase.roomDao();
    }
}
