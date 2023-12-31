package com.cryptocurrency.room.converters;

import androidx.room.TypeConverter;

import com.cryptocurrency.models.cryptolistmodel.AllMarketModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AllMarketModelConverter {

    @TypeConverter
    public String tojson(AllMarketModel allMarketModel) {
        if (allMarketModel == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<AllMarketModel>() {
        }.getType();
        return gson.toJson(allMarketModel, type);
    }

    @TypeConverter
    public AllMarketModel toDataClass(String allMarket) {
        if (allMarket == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<AllMarketModel>() {
        }.getType();
        return gson.fromJson(allMarket, type);
    }
}