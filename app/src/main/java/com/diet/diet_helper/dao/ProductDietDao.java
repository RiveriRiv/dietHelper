package com.diet.diet_helper.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.diet.diet_helper.model.Diet;
import com.diet.diet_helper.model.Product;

import java.util.List;
import java.util.Map;

@Dao
public interface ProductDietDao {

    @Query("SELECT * FROM diet JOIN product ON diet.id = product.dietId WHERE diet.id = :dietId")
    Map<Diet, List<Product>> loadDietAndProducts(Long dietId);

}
