package com.example.projectui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import com.example.projectui.R
import com.example.projectui.protocol.device.ESPDevice

class HomeDeviceArrayAdapter : ArrayAdapter<ESPDevice> {
    private var fragmentManager: FragmentManager? = null
    private var vi: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    constructor(context: Context,
                resource: Int,
                devices: ArrayList<ESPDevice>,
                supportFragmentManager: FragmentManager?) : super(context, resource, devices) {
                    fragmentManager = supportFragmentManager
                }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device: ESPDevice? = getItem(position)
        val retView: View = convertView ?: vi.inflate(R.layout.home_device_view, parent, false)

        device?.let {
            populateHomeDevice(retView, device, fragmentManager)
        }

        return retView
    }
}