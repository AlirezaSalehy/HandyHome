package com.example.projectui.protocol.device

import com.example.projectui.database.entity.Device

class Lamp(server: ESPServer, device: Device) : ESPDevice(server, device) {
    enum class Command(val commandNumber: Int, val desc: String) {
        TurnOn(0, "Turn On"),
        TurnOff(1, "Turn Off"),
        Toggle(2, "Toggle"),
        Blink(3, "Blink"),
        Dim(4, "Dim"),
        GetState(5, "Get State")
    }
}