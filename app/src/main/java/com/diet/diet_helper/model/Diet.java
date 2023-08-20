package com.diet.diet_helper.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public final class Diet {
    @ColumnInfo
    @PrimaryKey(autoGenerate=true)
    public Long id;
    @ColumnInfo
    public String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Diet(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Diet(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diet diet = (Diet) o;

        if (!getId().equals(diet.getId())) return false;
        return getName().equals(diet.getName());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
