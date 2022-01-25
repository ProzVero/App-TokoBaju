package com.nurmiati.tok_ko.core.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nurmiati.tok_ko.R
import com.nurmiati.tok_ko.core.data.model.DetailTransaksi
import com.nurmiati.tok_ko.util.Helper
import com.nurmiati.tok_ko.util.Util
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class AdapterDetailProduk(var data: ArrayList<DetailTransaksi>) :
    RecyclerView.Adapter<AdapterDetailProduk.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image)
        val nama_produk = view.findViewById<TextView>(R.id.nama_produk)
        val berat_barang = view.findViewById<TextView>(R.id.berat_barang)
        val harga_produk = view.findViewById<TextView>(R.id.harga_produk)
        val item = view.findViewById<TextView>(R.id.item)
        val total = view.findViewById<TextView>(R.id.total)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detail_keranjang, parent, false)
        return HolderData(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]

        val nama_produk = a.produk.name
        val p = a.produk
        holder.nama_produk.text = nama_produk
//        holder.berat_barang.text =
        holder.harga_produk.text = Helper().formatRupiah(p.harga)
        holder.total.text = Helper().formatRupiah(a.total_harga)
        holder.item.text = a.total_item.toString() + " Items"

        val imageUrl = Util.produkUrl + p.image
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(holder.image)

        holder.layout.setOnClickListener {
//            listener.onClicked(a)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onClicked(data: DetailTransaksi)
    }
}