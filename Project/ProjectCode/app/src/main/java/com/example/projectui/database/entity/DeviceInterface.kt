package com.example.projectui.database.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DeviceInterface {
    @Query("SELECT * FROM Device")
    fun getAll(): List<Device>?

    @Query("SELECT * FROM Device WHERE id = (:location)")
    fun loadByLocation(location: String): List<Device>?

    @Query("SELECT * FROM Device WHERE type = (:type)")
    fun loadByType(type: String): List<Device>?

    @Query("SELECT * FROM Device WHERE custDesc like (:custDesc)")
    fun findByCustDesc(custDesc: String): List<Device>?

    @Query("SELECT * FROM Device WHERE type like (:type) and id like(:id)")
    fun findByTypeId(type: String, id: Int): List<Device>?

    @Query("UPDATE Device set custDesc = (:custDesc) WHERE type like (:type) and id like(:id)")
    fun updateDeviceDescription(type: String, id: Int, custDesc: String)

    @Insert
    fun insertAll(vararg devices: Device)

    @Delete
    fun delete(device: Device)
}