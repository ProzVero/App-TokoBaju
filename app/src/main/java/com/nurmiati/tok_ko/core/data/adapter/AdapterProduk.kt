package com.nurmiati.tok_ko.core.data.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nurmiati.tok_ko.DetailActivity
import com.nurmiati.tok_ko.R
import com.nurmiati.tok_ko.TokoActivity
import com.nurmiati.tok_ko.core.data.model.Produk
import com.nurmiati.tok_ko.util.Helper
import com.nurmiati.tok_ko.util.Util
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterProduk(var activity: Activity, var data: ArrayList<Produk>) :
    RecyclerView.Adapter<AdapterProduk.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val tv_nama = view.findViewById<TextView>(R.id.nama_produk)
        val tv_toko = view.findViewById<TextView>(R.id.toko)
        val tv_harga = view.findViewById<TextView>(R.id.harga)
        val tv_gambar = view.findViewById<ImageView>(R.id.image)
        val layout = view.findViewById<CardView>(R.id.layout)
        val div_status = view.findViewById<LinearLayout>(R.id.div_status)
        val text_status = view.findViewById<TextView>(R.id.text_status)
        val lay_toko = view.findViewById<LinearLayout>(R.id.lay_toko)
    }
    lateinit var contex: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        contex = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return HolderData(view)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: HolderData, position: Int) {

        holder.tv_nama.text = data[position].name
        holder.tv_toko.text = data[position].nama_toko
        holder.tv_harga.text = Helper().formatRupiah(data[position].harga)
            .format(Integer.valueOf(data[position].harga))
        val imageUrl =
            Util.produkUrl + data[position].image
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(holder.tv_gambar)

        val stokBarang = Integer.valueOf(data[position].stok)
        var bg = contex.getDrawable(R.drawable.bg_btn2)
        if (stokBarang <= 0) {
            holder.text_status.text = "Habis"
            bg = contex.getDrawable(R.drawable.bg_btn4)
        } else {
            holder.text_status.text = "Tersedia"
            bg = contex.getDrawable(R.drawable.bg_btn3)
        }
        holder.div_status.background = bg

        holder.layout.setOnClickListener {
            val str = Gson().toJson(data[position], Produk::class.java)
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra("extra", str)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }

        holder.lay_toko.setOnClickListener {
            val intent = Intent(activity, TokoActivity::class.java)
            intent.putExtra("user_id", data[position].user_id.toString())
            activity.startActivity(intent)
        }
    }

    private var searchData: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val searchList: java.util.ArrayList<Produk> = java.util.ArrayList<Produk>()
            if (constraint.toString().isEmpty()) {
                searchList.addAll(data)
            } else {
                for (getRekamMedik in data) {
                    if (getRekamMedik.name.toLowerCase(Locale.ROOT)
                            .contains(constraint.toString().toLowerCase(Locale.ROOT))
                    ) {
                        searchList.add(getRekamMedik)
                    }
                }
            }
            val results = FilterResults()
            results.values = searchList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            data.clear()
            data.addAll(results.values as Collection<Produk>)
            notifyDataSetChanged()
        }
    }

    fun getSearchData(): Filter {
        return searchData
    }

    override fun getItemCount(): Int {
        return data.size
    }
}