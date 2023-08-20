package com.diet.diet_helper.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diet.diet_helper.model.Diet;

import java.util.List;

@Dao
public interface DietDao {

    @Query("SELECT * FROM diet")
    List<Diet> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertDiet(Diet diet);

    @Update
    void updateDiet(Diet diet);

    @Delete
    void deleteDiet(Diet diet);
}
