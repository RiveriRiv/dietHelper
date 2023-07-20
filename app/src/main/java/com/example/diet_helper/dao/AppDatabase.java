package com.example.diet_helper.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.diet_helper.model.Diet;
import com.example.diet_helper.model.Product;

@Database(entities = {Diet.class, Product.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDietDao productDietDao();

    public abstract DietDao dietDao();

    public abstract ProductDao productDao();

    public static final String NAME = "AppDataBase";
}
