package com.example.device

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.device.databinding.ItemVitalAddDeviceBinding

class AddVitalDeviceAdapter(private val onVitalListener: OnVitalListener) :
    RecyclerView.Adapter<AddVitalDeviceAdapter.ViewHolder>() {

    private var vitals = ArrayList<LocalVitalDevice>()

    interface OnVitalListener {

        fun onVitalEdit(vitalDevice: LocalVitalDevice, position: Int)

        fun onVitalDelete(vitalDevice: LocalVitalDevice)

    }

    fun addData(vitalDevice: LocalVitalDevice) {
        vitals.add(vitalDevice)
        notifyItemRangeChanged(0, vitals.size)
    }

    fun addData(vitalDevices: ArrayList<LocalVitalDevice>) {
        vitals.addAll(vitalDevices)
        notifyItemRangeChanged(0, vitals.size)
    }

    inner class ViewHolder(private val binding: ItemVitalAddDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(vitalDevice: LocalVitalDevice) {
            binding.editDevice.visibility = View.GONE
            binding.editDevice.setOnClickListener {
                onVitalListener.onVitalEdit(vitals[adapterPosition], adapterPosition)
            }
            binding.deleteDevice.setOnClickListener {
                onVitalListener.onVitalDelete(vitalDevice)
                notifyItemRemoved(adapterPosition)
                vitals.remove(vitalDevice)
            }
            binding.tvDeviceId.apply {
                text = vitalDevice.deviceId
            }
            binding.tvSerialNumber.apply {
                text = vitalDevice.serialNumber
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemVitalAddDeviceBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = vitals.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindInfo(vitals[position])
    }

}
