package com.nurmiati.tok_ko.core.data.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nurmiati.tok_ko.R
import com.nurmiati.tok_ko.core.data.model.Kategori

class AdapterKategori(var activity: Activity, var data: ArrayList<Kategori>) :
    RecyclerView.Adapter<AdapterKategori.HolderData>() {

    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val txt_kategori = view.findViewById<TextView>(R.id.txt_kategori)
        val rv_produk = view.findViewById<RecyclerView>(R.id.rv_produk)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return AdapterKategori.HolderData(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        holder.txt_kategori.text = data[position].nama_kategori
        holder.rv_produk.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        holder.rv_produk.layoutManager = layoutManager
        var rvProdukLimit_adapter : AdapterProdukChild? = null
        rvProdukLimit_adapter = AdapterProdukChild(activity, data[position].produk)
        holder.rv_produk.adapter = rvProdukLimit_adapter
        rvProdukLimit_adapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}