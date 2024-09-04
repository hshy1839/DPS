package com.woosuk.AgingInPlace.medication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.woosuk.AgingInPlace.Medication
import com.woosuk.AgingInPlace.R


class MedicationAdapter(
    private val medicationNames: List<String>,
    private val diseaseNames: List<String>
) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicationName: TextView = itemView.findViewById(R.id.medicationName)
        val diseaseName: TextView = itemView.findViewById(R.id.diseaseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.medicationName.text = medicationNames[position]
        holder.diseaseName.text = if (position < diseaseNames.size) diseaseNames[position] else ""
    }

    override fun getItemCount(): Int {
        return medicationNames.size
    }
}



