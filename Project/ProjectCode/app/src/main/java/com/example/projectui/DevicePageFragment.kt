package com.example.projectui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.projectui.adapter.getIconId
import com.example.projectui.database.DataBase
import com.example.projectui.protocol.device.ESPDevice
import com.example.projectui.protocol.device.LM35
import com.example.projectui.protocol.device.Photocell
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.thread


/**
 * A simple [Fragment] subclass.
 * Use the [DevicePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DevicePageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var device: ESPDevice? = null
    var selectedCommand: String? = null
    var graph: GraphView? = null
    var updateGraphTask: Thread? = null
    var graphSeries: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            device = it.getParcelable("device") as ESPDevice?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v: View? = inflater.inflate(R.layout.fragment_device_page, container, false)
        v?.let {
            val btn = v.findViewById<FloatingActionButton>(R.id.floatingActionButton)
            btn.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction().apply {
                    activity?.let{
                        thread {
                            it.applicationContext.let { it1 ->
                                val db = Room.databaseBuilder(
                                    it1,
                                    DataBase::class.java, "DeviceDatabase"
                                ).build()
                                device?.let { it2 ->
                                    it2.device.custDesc = (v.findViewById(R.id.devicePageCustomDescription) as TextInputLayout)
                                        .editText?.text.toString()
                                    db.deviceEntityInterface()
                                        .updateDeviceDescription(it2.device.type, it2.device.id, it2.device.custDesc)
                                    println("here is saving to data base")
                                }
                            }
                        }.join()
                    }
                    updateGraphTask?.interrupt()
                    updateGraphTask?.join()
                    graphSeries.resetData(arrayOf())
                    activity?.supportFragmentManager?.popBackStack()
                }
            }

            device?.let {
                val commandList: ArrayList<String> = it.getCommandList()

                val typeTextView = v.findViewById(R.id.devicePageTypeTextView) as TextView
                val locationTextView = v.findViewById(R.id.devicePageLocationTextView) as TextView
                val iconImageView = v.findViewById(R.id.devicePageIcon) as ImageView
                val descriptionTextEdit = v.findViewById(R.id.devicePageCustomDescription) as TextInputLayout
                val technicalDescTextEdit = v.findViewById(R.id.devicePageTechnicalDescription) as TextView
                val commandGroup = v.findViewById(R.id.deviceCommandRadioGroup) as RadioGroup
                val sendCommandIcon = v.findViewById(R.id.devicePageSendIcon) as ImageButton
                val param1TextEdit = v.findViewById(R.id.devicePageParam1) as TextInputLayout
                val param2TextEdit = v.findViewById(R.id.devicePageParam2) as TextInputLayout
                val devicePageResultTextView = v.findViewById(R.id.devicePageResult) as TextView
                val devicePageGraph = v.findViewById(R.id.graph) as GraphView

                param1TextEdit.visibility = View.GONE
                param2TextEdit.visibility = View.GONE
                devicePageGraph.visibility = View.GONE
                devicePageGraph.addSeries(graphSeries)

                typeTextView.text = it.device.type.split(" ").joinToString(" ") { it.capitalize(
                    Locale.US) }.trimEnd();
                locationTextView.text = it.device.location
                iconImageView.setBackgroundResource(getIconId(it))

                descriptionTextEdit.editText?.setText(it.device.custDesc.toCharArray(), 0, it.device.custDesc.toCharArray().size)
                technicalDescTextEdit.text = it.device.techDesc
                commandGroup.removeAllViews()
                commandList.forEach { it1 ->
                    val radioButton = RadioButton(context)
                    radioButton.text = it1
                    commandGroup.addView(radioButton)
                    radioButton.setOnClickListener { it2 ->
                        selectedCommand = (it2 as RadioButton).text as String
                        selectedCommand?.let {
                            param1TextEdit.visibility = View.INVISIBLE
                            param2TextEdit.visibility = View.INVISIBLE

                            devicePageGraph.visibility = View.GONE
                            updateGraphTask?.interrupt()
                            updateGraphTask?.join()
                            graphSeries.resetData(arrayOf())

                            devicePageResultTextView.text = " "

                            when (it.lowercase()) {
                                "dim" -> {
                                    param1TextEdit.hint = "Duty Cycle"
                                    param1TextEdit.visibility = View.VISIBLE
                                    param2TextEdit.visibility = View.INVISIBLE
                                }
                                "blink" -> {
                                    param1TextEdit.hint = "On Time"
                                    param2TextEdit.hint = "Off Time"
                                    param1TextEdit.visibility = View.VISIBLE
                                    param2TextEdit.visibility = View.VISIBLE
                                }
                                LM35.Command.ReadTemperatureRepeatedly.desc.lowercase() -> {
                                    devicePageGraph.visibility = View.VISIBLE

                                }
                                Photocell.Command.ReadLightSensorRepeatedly.desc.lowercase() -> {
                                    devicePageGraph.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }

                sendCommandIcon.setOnClickListener { it5 ->
                    var result: String? = null
                    val command = selectedCommand
                    val param1 = param1TextEdit.editText?.text.toString()
                    var param2 = param2TextEdit.editText?.text.toString()
                    if (command != null) {
                        if (command == LM35.Command.ReadTemperatureRepeatedly.desc) {
                            handleGraphUpdateThread(LM35.Command.ReadTemperatureOnce.desc, 1000, devicePageResultTextView)

                        } else if (command == Photocell.Command.ReadLightSensorRepeatedly.desc) {
                            handleGraphUpdateThread(Photocell.Command.ReadLightSensorOnce.desc, 1000, devicePageResultTextView)

                        } else if (param1TextEdit.visibility == View.INVISIBLE || (param1 != "" && param1.isDigitsOnly())) {
                            if (param2TextEdit.visibility == View.INVISIBLE || (param2 != "" && param2.isDigitsOnly())) {
                                thread {
                                    result = device?.handleCommand(command, param1, param2)
                                }.join()
                            }
                        }

                        result?.let { it4 ->
                            var value: Double? = null
                            val obj = JSONObject(it4.lowercase())

                            if (command.lowercase() == LM35.Command.ReadTemperatureOnce.desc.lowercase()) {
                                value = obj.getDouble("temp")

                            } else if (command.lowercase() == Photocell.Command.ReadLightSensorOnce.desc.lowercase()) {
                                value = obj.getDouble("lightnesspercentage")
                            } else if (it.state != "failed") {
                                iconImageView.setBackgroundResource(
                                    getIconId(
                                        it
                                    )
                                )
                            }

                            devicePageResultTextView.text = value?.toString() ?: " "
                        }
                    }
                }
            }
        }

        return v
    }

    fun handleGraphUpdateThread(
        desc: String,
        i: Long,
        devicePageResultTextView: TextView) {
        updateGraphTask = thread {
            var returnVal: Boolean = true
            var num: Double = 0.0
            while (returnVal) {
                try {
                    Thread.sleep(i)
                    val result = device?.handleCommand(desc)
                    result?.let { it4 ->
                        kotlin.runCatching {
                            var value: Double? = null
                            val obj = JSONObject(it4.lowercase())

                            if (desc == LM35.Command.ReadTemperatureOnce.desc) {
                                value = obj.getDouble("temp")

                            } else if (desc == Photocell.Command.ReadLightSensorOnce.desc) {
                                value = obj.getDouble("lightnesspercentage")
                            }

                            activity?.runOnUiThread {
                                devicePageResultTextView.text = value?.toString() ?: " "
                                graphSeries.appendData(DataPoint(num, value ?: 0.0), false, 40);
                                num += 1
                            }
                        }
                    }

                } catch (e: InterruptedException) {
                    returnVal = false
                    break
                }
            }
        }
    }

    fun updateGraph(command: String) {
        device?.handleCommand(command)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DevicePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =  DevicePageFragment()
    }
}