package com.hnievat.controlluces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDao  {
    @Insert
    void addData(MyDatalist mydatalist);

    @Query("select * from mydatalist")
    List<MyDatalist>getMyData();

    @Query("DELETE FROM mydatalist")
    void deleteAll();

    @Update
    void update(MyDatalist mydatalist);

}
