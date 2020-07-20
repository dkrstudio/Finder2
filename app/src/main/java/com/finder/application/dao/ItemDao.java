package com.finder.application.dao;

import androidx.room.*;
import com.finder.application.model.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM ITEMS")
    List<Item> getAll();

    @Insert
    void Insert(Item item);

    @Insert
    long InsertId(Item item);

    @Update
    void Update(Item item);

    @Delete
    void Delete(Item item);

    @Query("SELECT * FROM ITEMS WHERE UNIC=:unic")
    Item get(Long unic);

    @Query("SELECT * FROM ITEMS WHERE author_id=:user_id")
    List<Item> getAllByUserId(String user_id);
}
