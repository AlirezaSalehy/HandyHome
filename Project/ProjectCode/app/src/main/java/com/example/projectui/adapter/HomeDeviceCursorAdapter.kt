package com.example.projectui.adapter

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import com.example.projectui.R
import com.example.projectui.database.entity.Device

class HomeDeviceCursorAdapter : CursorAdapter {
    constructor(context: Context, cursor: Cursor) : super(context, cursor, 0)

    override fun newView(p0: Context?, p1: Cursor?, p2: ViewGroup?): View {
        return View.inflate(p0, R.layout.home_device_view, p2)
    }

    override fun bindView(p0: View?, p1: Context?, p2: Cursor?) {
        p0?.let {
            val dev: Device = Device(cursor)
//            populateHomeDevice(p0, dev)
        }
    }
}