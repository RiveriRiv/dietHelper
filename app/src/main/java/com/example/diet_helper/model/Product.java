package com.example.diet_helper.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(onDelete = ForeignKey.CASCADE, entity=Diet.class, parentColumns="id", childColumns="dietId"),
        indices = {@Index("name"), @Index(value = {"dietId"})})
public final class Product {
    @ColumnInfo
    @PrimaryKey(autoGenerate=true)
    public Long id;
    @ColumnInfo
    public String name;
    @ColumnInfo
    public Long dietId;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getDietId() {
        return dietId;
    }

    public Product(Long id, String name, Long dietId) {
        this.id = id;
        this.name = name;
        this.dietId = dietId;
    }

    @Ignore
    public Product(String name, Long dietId) {
        this.name = name;
        this.dietId = dietId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (!getId().equals(product.getId())) return false;
        if (!getName().equals(product.getName())) return false;
        return getDietId().equals(product.getDietId());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getDietId().hashCode();
        return result;
    }
}
