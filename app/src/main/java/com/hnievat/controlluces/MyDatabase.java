package com.hnievat.controlluces;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={MyDatalist.class},version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract MyDao myDao();
}