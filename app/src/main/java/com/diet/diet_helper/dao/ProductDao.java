package com.diet.diet_helper.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.diet.diet_helper.model.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product")
    List<Product> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertProduct(Product product);

    @Delete
    void deleteProduct(Product product);
}
