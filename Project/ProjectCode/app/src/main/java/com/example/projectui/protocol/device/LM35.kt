package com.example.projectui.protocol.device

class LM35() {
    enum class Command(val commandNumber: Int, val desc: String) {
        ReadTemperatureOnce(0, "Read Temperature Once"),
        ReadTemperatureRepeatedly(0, "Read Temperature Repeatedly"),
    }
}