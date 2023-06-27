package com.cryptocurrency.room.converters;

import androidx.room.TypeConverter;

import com.cryptocurrency.models.cryptolistmodel.CryptoMarketDataModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CryptoDataModelConverter {

    @TypeConverter
    public String tojson(CryptoMarketDataModel cryptoMarketDataModel) {
        if (cryptoMarketDataModel == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<CryptoMarketDataModel>() {
        }.getType();
        return gson.toJson(cryptoMarketDataModel, type);
    }

    @TypeConverter
    public CryptoMarketDataModel toDataClass(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<CryptoMarketDataModel>() {
        }.getType();
        return gson.fromJson(data, type);
    }
}