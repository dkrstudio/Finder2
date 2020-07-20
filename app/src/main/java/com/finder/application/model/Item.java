package com.finder.application.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public long unic;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "author_id")
    public String author_id;

    @ColumnInfo(name = "author_name")
    public String author_name;

    @ColumnInfo(name = "author_avatar")
    public String author_avatar;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "public")
    public int is_public;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;
}
