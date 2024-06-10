package com.example.device

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        showAddVitalDeviceDialog()
    }

    private fun showAddVitalDeviceDialog() {
        val dialogFragment = AddVitalDeviceDialogFragment.newInstance()
        dialogFragment.show(supportFragmentManager, "AddVitalDeviceDialogFragment")
    }
}
