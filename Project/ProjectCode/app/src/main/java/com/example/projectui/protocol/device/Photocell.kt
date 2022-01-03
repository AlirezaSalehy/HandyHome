package com.example.projectui.protocol.device

class Photocell() {
    enum class Command(val commandNumber: Int, val desc: String) {
        ReadLightSensorOnce(0, "Read Photocell Once"),
        ReadLightSensorRepeatedly(0, "Read Photocell Repeatedly"),

    }
}