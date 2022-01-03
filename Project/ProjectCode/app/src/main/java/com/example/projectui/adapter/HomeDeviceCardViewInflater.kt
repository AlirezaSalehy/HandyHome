package com.example.projectui.adapter

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.projectui.DevicePageFragment
import com.example.projectui.R
import com.example.projectui.protocol.device.ESPDevice
import com.example.projectui.protocol.device.Lamp
import java.util.*
import kotlin.concurrent.thread

fun populateHomeDevice(p0: View, device: ESPDevice, fragmentManager: FragmentManager?) {
    var deviceName: TextView = p0.findViewById(R.id.HomeDeviceViewName)
    var deviceLocation: TextView = p0.findViewById(R.id.HomeDeviceViewLocation)
    var deviceIcon: ImageView = p0.findViewById(R.id.HomeDeviceViewIcon)
    var linearLayout: LinearLayout = p0.findViewById(R.id.linearLayout)

    val dev = device.device
    deviceIcon.setBackgroundResource(getIconId(device))
    deviceName.text = dev.type.split(" ").joinToString(" ") { it.capitalize(
        Locale.US) }.trimEnd()
    deviceLocation.text = dev.location.uppercase()

    linearLayout.setOnClickListener {
        val devicePage = DevicePageFragment()
        val bundle = Bundle()
        bundle.putParcelable("device", device)
        devicePage.arguments = bundle
        fragmentManager?.let {
            it.beginTransaction()
                .replace(R.id.mainFrame, devicePage)
                .addToBackStack("HomeFragment")
                .commit()
        }
    }

    if (device.device.type.lowercase() == "light") {
        deviceIcon.setOnClickListener {
            var result: String = ""
            thread {
                result = device.handleCommand(Lamp.Command.Toggle.desc)
            }.join()

            deviceIcon.setBackgroundResource(getIconId(device))
        }
    }
}

fun getIconId(device: ESPDevice) : Int {
    val typeLower = device.device.type.lowercase()
    val valueLower = (device.state ?: "off").lowercase()
    println(typeLower)

    return when {
        typeLower == "temperature sensor" -> R.drawable.temp_sens
        typeLower == "light sensor" -> R.drawable.light_sens
        typeLower == "light" && valueLower == "on" -> R.drawable.light_on
        typeLower == "light" && valueLower == "off" -> R.drawable.off_lamp
        else -> R.drawable.unknown_device
    }

}