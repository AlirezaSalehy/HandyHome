package com.example.projectui.protocol.device

import android.os.Parcelable
import com.example.projectui.database.entity.Device
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

@Parcelize
open class ESPDevice(private var server: ESPServer, var device: Device, var state: String? = null) : Parcelable {
    fun handleCommand(command: String, vararg params: String) : String {
        return when (device.type.lowercase()) {
            "temperature sensor" -> handleTemperatureCommands(LM35.Command.values().single { it.desc == command }, params)
            "light sensor" -> handleLightSensorCommands(Photocell.Command.values().single { it.desc == command }, params)
            "light" -> handleLightCommand(Lamp.Command.values().single { it.desc == command }, params)
            else -> "Unknown"
        }
    }

    private fun handleLightSensorCommands(command: Photocell.Command, params: Array<out String>) : String {
        val path = "photocell"
        val pramList = mutableListOf("id", "request")
        val valsList = mutableListOf(device.id.toString(), Lamp.Command.TurnOn.commandNumber.toString())
        var requestType = "get"
        when (command) {
            Photocell.Command.ReadLightSensorOnce -> ""
        }
        return server.sendRequest(requestType, path, pramList, valsList) ?: "failed"
    }

    private fun handleTemperatureCommands(command: LM35.Command, params: Array<out String>) : String {
        val path = "lm35"
        val pramList = mutableListOf("id", "request")
        val valsList = mutableListOf(device.id.toString(), Lamp.Command.TurnOn.commandNumber.toString())
        var requestType = "get"
        when (command) {
            LM35.Command.ReadTemperatureOnce -> ""
        }
        return server.sendRequest(requestType, path, pramList, valsList) ?: "failed"
    }

    private fun handleLightCommand(command: Lamp.Command, params: Array<out String>) : String {
        val path = "led"
        val pramList = mutableListOf("id", "request")
        val valsList = mutableListOf(device.id.toString(), command.commandNumber.toString())
        var requestType: String = "put"
        when (command) {
            Lamp.Command.TurnOn -> ""
            Lamp.Command.TurnOff -> ""
            Lamp.Command.Toggle -> ""
            Lamp.Command.Blink -> {
                pramList.add("offTime")
                pramList.add("onTime")
                valsList.add(params[0])
                valsList.add(params[1])
            }
            Lamp.Command.Dim -> {
                pramList.add("dutyCycle")
                valsList.add(params[0])
            }
            Lamp.Command.GetState -> {
                requestType = "put"
            }
        }

        val result = server.sendRequest(requestType, path, pramList, valsList) ?: "{\"state\":\"failed\"}"
        val jsObject = JSONObject(result.lowercase())
        state = jsObject.getString("state")

        return result
    }

    fun getCommandList(): ArrayList<String> {
        var commandList: ArrayList<String> = ArrayList()
        when (device.type.lowercase()) {
            "temperature sensor" -> LM35.Command.values().forEach{ commandList.add(it.desc) }
            "light sensor" -> Photocell.Command.values().forEach{ commandList.add(it.desc) }
            "light" -> Lamp.Command.values().forEach{ commandList.add(it.desc) }
            else -> commandList.add("Unknown")
        }

        return commandList
    }
}