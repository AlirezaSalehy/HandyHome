package com.example.projectui.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projectui.database.entity.Device
import com.example.projectui.database.entity.DeviceInterface

@Database(entities = [Device::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun deviceEntityInterface(): DeviceInterface
}