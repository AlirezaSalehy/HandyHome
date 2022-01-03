package com.example.projectui.protocol.device

import android.os.Parcelable
import com.example.projectui.database.entity.Device
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
class ESPServer(private var ip: String = "192.168.1.101", private var port: Int = 80) : Parcelable {

    fun getDescription() : ArrayList<ESPDevice>? {
        var devArrayList: ArrayList<ESPDevice>
        val devicesJson = sendRequest("get", "desc", null, null)
        Thread.sleep(1200)
        if (devicesJson == null)
            return null

        val jsObject = JSONObject(devicesJson.lowercase())
        var devicesJsonArray: JSONArray = jsObject.getJSONArray("devices")
        devArrayList = generateDeviceArrayList(devicesJsonArray)
        return devArrayList
    }

    private fun generateDeviceArrayList(devices: JSONArray) : ArrayList<ESPDevice> {
        var deviceList: ArrayList<ESPDevice> = ArrayList()

        for (i in 0 until devices.length()) {
            val deviceObject = devices.getJSONObject(i)
            val device = Device(deviceObject)
            val state: String? = null
            val deviceData = ESPDevice(this, device, state)
            if (device.type.lowercase() == "light") {
                deviceData.handleCommand(Lamp.Command.GetState.desc)
            }
            deviceList.add(deviceData)
        }

        return deviceList
    }


    internal fun sendRequest(requestType: String, path: String, params: List<String>?,
                             vals: List<String>?): String? {
        var paramsString = String()
        params?.let {
            vals?.let {
                if (params.isNotEmpty())
                    paramsString += "?${params[0]}=${vals[0]}"
                for (i in 1 until params.size) {
                    paramsString += "&${params[i]}=${vals[i]}"
                }
            }

        }

        val url = "http://$ip:$port/$path$paramsString"
        println(url)
        var requestObject = if(requestType=="get") url.httpGet() else url.httpPut()
        val (request, response, result) = requestObject
            .timeout(2000)
            .timeoutRead(2000)
            .responseString()

        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println(ex)
            }
            is Result.Success -> {
                val data = result.get()
                println(data)
                return String(response.body().toByteArray())
            }
        }

        return null
    }

    fun getESPDevices(devices: List<Device>?): java.util.ArrayList<ESPDevice> {
        var espDeviceList: ArrayList<ESPDevice> = ArrayList()
        devices?.let {
            for (device in it) {
                espDeviceList.add(ESPDevice(this, device))
            }

        }

        return espDeviceList
    }
}