package com.example.diet_helper.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class DietWithProducts {
    @Embedded
    public Diet user;
    @Relation(
            parentColumn = "id",
            entityColumn = "dietId"
    )
    public List<Product> products;
}
