package com.example.diet_helper.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.diet_helper.model.Diet;

import java.util.List;

@Dao
public interface DietDao {

    @Query("SELECT * FROM diet")
    List<Diet> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertDiet(Diet diet);

    @Delete
    void deleteDiet(Diet diet);
}
