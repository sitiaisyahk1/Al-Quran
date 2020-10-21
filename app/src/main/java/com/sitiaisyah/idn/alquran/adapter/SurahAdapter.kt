package com.sitiaisyah.idn.alquran.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sitiaisyah.idn.alquran.R
import com.sitiaisyah.idn.alquran.model.ModelSurah

class SurahAdapter(
    private val mContext: Context,
    private val items: List<ModelSurah>,
    private val onSelectData: onSelectData
) : RecyclerView.Adapter<SurahAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_surah, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val data = items[position]
        holder.txtNumber.text = data.nomor
        holder.txtAyat.text = data.nama
        holder.txtInfo.text = data.type + " - " + data.ayat + " Ayat "
        holder.txtName.text = data.asma
        holder.cvSurah.setOnClickListener { onSelectData.onSelected(data) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //Class Holder
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cvSurah: CardView
        var txtNumber: TextView
        var txtAyat: TextView
        var txtInfo: TextView
        var txtName: TextView

        init {
            cvSurah = itemView.findViewById(R.id.cvSurah)
            txtNumber = itemView.findViewById(R.id.txtNumber)
            txtAyat = itemView.findViewById(R.id.txtAyat)
            txtInfo = itemView.findViewById(R.id.txtInfo)
            txtName = itemView.findViewById(R.id.txtName)
        }
    }

    companion object {
        interface onSelectData {
            fun onSelected(modelSurah: ModelSurah)
        }
    }

}