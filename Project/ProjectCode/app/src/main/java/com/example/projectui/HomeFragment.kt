package com.example.projectui

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projectui.adapter.HomeDeviceArrayAdapter
import com.example.projectui.protocol.device.ESPDevice
import com.example.projectui.protocol.device.Lamp
import com.example.projectui.protocol.device.Photocell
import com.example.projectui.utils.dpTpPx
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var currentSection: Button? = null
    private var deviceList: ArrayList<ESPDevice> = ArrayList()
    private var lightUpObserverTask: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("homeFragment", "onCreate: ")
        arguments?.let {
            val devicesParcelableArray : Array<Parcelable>? =  it.getParcelableArray("devicesList")
            devicesParcelableArray?.let {
                for (parcelable: Parcelable in devicesParcelableArray) {
                    deviceList.add(parcelable as ESPDevice)
                }
            }
        }
    }


    private fun createHomeSections() {
        val homeSections = view!!.findViewById(R.id.homeSections) as LinearLayout
        homeSections.removeAllViews()

        val sectionsSet = mutableSetOf(getString(R.string.all_devices))
        for (device in deviceList) {
            sectionsSet.add(device.device.location)
        }

        for (i in 0 until sectionsSet.size) {
            val section = Button(context)
            section.maxLines = 1
            section.isAllCaps = false
            section.text = sectionsSet.elementAt(i).split(" ").joinToString(" ") { it.capitalize(
                Locale.US) }.trimEnd()
            section.setBackgroundColor(Color.parseColor("#AFEDB0"))
            section.setPadding(dpTpPx(10), dpTpPx(10), 0, dpTpPx(10))
            section.setOnClickListener {
                handleNewSectionSelect(section)
            }
            homeSections.addView(section)

            if (sectionsSet.elementAt(i) == getString(R.string.all_devices))
                handleNewSectionSelect(section)
        }
    }

    private fun handleNewSectionSelect(section: Button) {
        currentSection?.setBackgroundColor(Color.parseColor("#AFEDB0"))
        createDevicesViews(section.text as String)
        section.setBackgroundColor(resources.getColor((R.color.purple_200)))
        currentSection = section
    }

    private fun createDevicesViews(section: String) {
        val deviceListView = view!!.findViewById(R.id.devicesList) as ListView
        val selectedSectionDevices = ArrayList(deviceList.filter { section == "All Devices" || it.device.location.lowercase() == section.lowercase() })
        var adapter = HomeDeviceArrayAdapter(requireContext(), R.layout.home_device_view, selectedSectionDevices, activity?.supportFragmentManager)
        deviceListView.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onDetach() {
        lightUpObserverTask?.interrupt()
        lightUpObserverTask?.join()
        super.onDetach()
    }

    override fun onDestroy() {
        lightUpObserverTask?.interrupt()
        lightUpObserverTask?.join()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createHomeSections()

        val lightUpButton = view.findViewById(R.id.lightUpButton) as Button
        lightUpButton.setOnClickListener {
            lightUpObserverTask?.interrupt()
            lightUpObserverTask?.join()

            val photocells = deviceList.filter { it.device.type.lowercase() == "light sensor" }
            val lamps = deviceList.filter { it.device.type.lowercase() == "light" }
            if (photocells.isNotEmpty() && lamps.isNotEmpty()) {
                lightUpHandler(photocells[0], lamps)
            }
        }

        val wakeUpButton = view.findViewById(R.id.getUpButton) as Button
        wakeUpButton.setOnClickListener {

        }
    }

    private fun lightUpHandler(photocell: ESPDevice, lamps: List<ESPDevice>) {
        lightUpObserverTask = thread {
            var prevAssessment: Boolean? = null
            var returnVal: Boolean = true
            var num: Double = 0.0
            while (returnVal) {
                try {
                    Thread.sleep(500)
                    val isTrue = checkCondition(photocell, 50.0)
                    if (prevAssessment != null && prevAssessment != isTrue) {
                        conditionChangesToast()
                    }

                    if (isTrue) {
                        lamps.forEach {
                            it.handleCommand(Lamp.Command.TurnOn.desc)
                        }
                    } else {
                        lamps.forEach {
                            it.handleCommand(Lamp.Command.TurnOff.desc)
                        }
                    }
                    activity?.runOnUiThread {
                        kotlin.runCatching {
                            createDevicesViews(currentSection?.text as String)
                        }.isSuccess
                    }
                    prevAssessment = isTrue

                    } catch (e: InterruptedException) {
                    returnVal = false
                    break
                }
            }
        }
    }

    private fun conditionChangesToast() {
        activity?.runOnUiThread {
            val toast = Toast.makeText(
                activity?.applicationContext,
                "State Changed",
                Toast.LENGTH_SHORT
            )

            toast.show()
        }
    }


    private fun checkCondition(photocell: ESPDevice, threshold: Double) : Boolean {
        val result = photocell.handleCommand(Photocell.Command.ReadLightSensorOnce.desc)
        kotlin.runCatching {
            var value: Double? = null
            val obj = JSONObject(result.lowercase())
            value = obj.getDouble("lightnesspercentage")
                if (value < threshold)
                    return true
            }
        return false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}