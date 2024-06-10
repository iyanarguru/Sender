package com.example.device

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.device.databinding.FragmentDialogAddDeviceBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class AddVitalDeviceDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogAddDeviceBinding
    private var addVitalDevice = ArrayList<LocalVitalDevice>()
    private var addVitalDeviceAdapter: AddVitalDeviceAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance(): AddVitalDeviceDialogFragment {
            return AddVitalDeviceDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setBackgroundDrawableResource(android.R.color.transparent)
        requireDialog().window?.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT
        )
        requireDialog().setCancelable(false)
        requireDialog().setCanceledOnTouchOutside(false)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_add_device, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBindingWithView()
    }

    private fun setBindingWithView() {
        binding.ivBack.setOnClickListener {
            saveDeviceDetails()
            requireActivity().finish()
        }
        addVitalDeviceAdapter =
            AddVitalDeviceAdapter(object : AddVitalDeviceAdapter.OnVitalListener {
                override fun onVitalEdit(vitalDevice: LocalVitalDevice, position: Int) {
                    binding.etSerialNumber.setText(vitalDevice.serialNumber)
                    binding.etDeviceId.setText(vitalDevice.deviceId)
                }

                override fun onVitalDelete(vitalDevice: LocalVitalDevice) {
                    addVitalDevice.remove(vitalDevice)
                    saveDeviceDetails()
                }

            })
        val vitalDeviceString = getVitalDevice()

        if (vitalDeviceString.isNotEmpty()) {
            val vitalObj = JSONObject(vitalDeviceString)
            val vitalArray = vitalObj.getJSONArray(vitalArrayName())
            for (index in 0..<vitalArray.length()) {
                val localVitalDevice = LocalVitalDevice()
                localVitalDevice.deviceId =
                    vitalArray.getJSONObject(index).getString(getDeviceIdField())
                localVitalDevice.serialNumber =
                    vitalArray.getJSONObject(index).getString(getDeviceSerialNameField())

                addVitalDevice.add(localVitalDevice)
            }
            addVitalDeviceAdapter?.addData(addVitalDevice)
            updateDeviceVisibility()
        } else {
            updateDeviceVisibility()
        }

        binding.rvAddedDevice.adapter = addVitalDeviceAdapter
        binding.rvAddedDevice.addItemDecoration(SpaceItemDecoration(10))
        binding.btnAddDevice.setOnClickListener {
            if (binding.etDeviceId.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter Device Id", Toast.LENGTH_SHORT).show()

            } else if (binding.etSerialNumber.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter Serial Number", Toast.LENGTH_SHORT).show()
            } else {
                addVitalDevice.add(
                    LocalVitalDevice(
                        deviceId = binding.etDeviceId.text.toString(),
                        serialNumber = binding.etSerialNumber.text.toString()
                    )
                )
                addVitalDeviceAdapter?.addData(
                    LocalVitalDevice(
                        deviceId = binding.etDeviceId.text.toString(),
                        serialNumber = binding.etSerialNumber.text.toString()
                    )
                )
                binding.etDeviceId.setText("")
                binding.etSerialNumber.setText("")
                saveDeviceDetails()
            }

        }
    }

    private fun updateDeviceVisibility() {
        when {
            addVitalDevice.isEmpty() -> binding.rvNoRecordFound.visibility = View.VISIBLE
            else -> binding.rvNoRecordFound.visibility = View.GONE
        }
    }

    private fun saveDeviceDetails() {
        updateDeviceVisibility()
        val vitalDeviceObject = JSONObject()
        val completeVitalArray = JSONArray()

        addVitalDevice.forEach { device ->
            val singleVitalDevice = JSONObject()
            singleVitalDevice.put(getDeviceIdField(), device.deviceId)
            singleVitalDevice.put(getDeviceSerialNameField(), device.serialNumber)
            completeVitalArray.put(singleVitalDevice)
        }
        vitalDeviceObject.put(vitalArrayName(), completeVitalArray)
        val file = File(requireContext().filesDir, "vitalDevice")
        val fileWriter = FileWriter(file)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.write(vitalDeviceObject.toString())
        requireContext().grantUriPermission(
            requireContext().packageName,
            Uri.parse(file.absolutePath),
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        bufferedWriter.close()
        Log.e("abs Path", file.absolutePath)
    }

    private fun getDeviceIdField() = "deviceId"

    private fun getDeviceSerialNameField() = "serialNumber"

    private fun vitalArrayName() = "vitalArray"

    private fun getVitalDevice(): String {
        try {
            val file = File(requireContext().filesDir, "vitalDevice")
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            return stringBuilder.toString()
        } catch (ex: Exception) {
            ex.toString()
            return ""
        }
    }


}