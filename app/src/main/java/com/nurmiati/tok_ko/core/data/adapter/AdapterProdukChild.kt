package com.nurmiati.tok_ko.core.data.adapter

import android.app.Activity
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
import com.nurmiati.tok_ko.core.data.model.ProdukChild
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.model.User
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import com.nurmiati.tok_ko.util.Helper
import com.nurmiati.tok_ko.util.Util
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class AdapterProdukChild(var activity: Activity, var data: ArrayList<ProdukChild>, var user : ArrayList<User>) :
    RecyclerView.Adapter<AdapterProdukChild.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val tv_nama = view.findViewById<TextView>(R.id.nama_produk)
        val tv_toko = view.findViewById<TextView>(R.id.toko)
        val tv_harga = view.findViewById<TextView>(R.id.harga)
        val tv_gambar = view.findViewById<ImageView>(R.id.image)
        val layout = view.findViewById<CardView>(R.id.layout)
        val img_logo = view.findViewById<ImageView>(R.id.img_logo)
        val lay_toko = view.findViewById<LinearLayout>(R.id.lay_toko)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        holder.tv_nama.text = data[position].name
        holder.tv_toko.text = data[position].nama_toko
        holder.tv_harga.text = Helper().formatRupiah(data[position].harga)
            .format(Integer.valueOf(data[position].harga))

        val imageUrl =
            Util.produkUrl + data[position].image
        var image = ""
        var email = ""
        for (i in user){
            if (i.id == data[position].user_id){
                val logo_toko = Util.logoToko + i.image
                image = i.image
                email = i.email
                Picasso.get()
                    .load(logo_toko)
                    .placeholder(R.drawable.ic_shopping_bag2)
                    .error(R.drawable.ic_shopping_bag2)
                    .into(holder.img_logo)
            }
        }


        //Log.d("Response " , "Image: "+ data[position].user.image)

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(holder.tv_gambar)

        holder.layout.setOnClickListener {
            val intent = Intent(activity, DetailActivity::class.java)
            val str = Gson().toJson(data[position], ProdukChild::class.java)
            intent.putExtra("extra", str)
            intent.putExtra("toko_image", image)
            intent.putExtra("toko_email", email)
            activity.startActivity(intent)
        }
        //Log.e("userid",data[position].user_id.toString())
        holder.lay_toko.setOnClickListener {
            val intent = Intent(activity, TokoActivity::class.java)
            intent.putExtra("user_id", data[position].user_id.toString())
            activity.startActivity(intent)
        }
    }

    private var searchData: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val searchList: java.util.ArrayList<ProdukChild> = java.util.ArrayList<ProdukChild>()
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
            data.addAll(results.values as Collection<ProdukChild>)
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