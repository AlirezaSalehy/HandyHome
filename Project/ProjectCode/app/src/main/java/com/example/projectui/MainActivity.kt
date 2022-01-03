package com.example.projectui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.projectui.database.DataBase
import com.example.projectui.protocol.device.ESPDevice
import com.example.projectui.protocol.device.ESPServer
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private val deviceDatabaseName: String = "DeviceDatabase"
    private val espServer = ESPServer("192.168.1.106")
    private lateinit var db: DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val waitingFragment = WaitConnectFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFrame, waitingFragment)
            commit()
        }

        db = Room.databaseBuilder(
            this.applicationContext,
            DataBase::class.java, deviceDatabaseName
        ).build()

        thread {
            // Connecting to ESP and getting devices JSON file
            var devices: ArrayList<ESPDevice>? = espServer.getDescription()
            if (devices == null) {
                showJSONFetchFailToast()
                var temp = db.deviceEntityInterface().getAll()
                devices = espServer.getESPDevices(temp)
            } else {
                saveDevicesInDB(devices)
                getDescriptionFromDb(devices)
            }

            val homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putParcelableArray("devicesList", devices.toTypedArray())
            homeFragment.arguments = bundle
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrame, homeFragment)
                commit()
            }
        }
    }

    private fun showJSONFetchFailToast() {
        runOnUiThread {
            val toast = Toast.makeText(
                applicationContext,
                "Failed to fetch JSON, check WiFi connection or ESPServer! We are currently offline",
                Toast.LENGTH_LONG
            )

            toast.show()
        }
    }

    private fun saveDevicesInDB(deviceList: ArrayList<ESPDevice>) {
        for (device: ESPDevice in deviceList) {
            var devList = db.deviceEntityInterface().findByTypeId(device.device.type, device.device.id)
            if (devList == null || devList.isEmpty())
                db.deviceEntityInterface().insertAll(device.device)
        }
    }

    private fun getDescriptionFromDb(deviceList: ArrayList<ESPDevice>) {
        for (i in 0 until deviceList.size) {
            var dev = db.deviceEntityInterface().findByTypeId(deviceList[i].device.type, deviceList[i].device.id)
            dev?.let {
                print(it[0].custDesc)
                deviceList[i].device.custDesc = it[0].custDesc
            }
        }
    }
}