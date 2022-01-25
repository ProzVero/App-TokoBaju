package com.nurmiati.tok_ko

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.nurmiati.tok_ko.core.data.adapter.AdapterProduk
import com.nurmiati.tok_ko.core.data.model.Produk
import com.nurmiati.tok_ko.core.data.model.ProdukLimit
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.room.MyDatabase
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import com.nurmiati.tok_ko.util.Helper
import com.nurmiati.tok_ko.util.Util
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var txt_namaproduk: TextView
    private lateinit var txt_hargaproduk: TextView
    private lateinit var namabarang: TextView
    lateinit var txt_deskripsi: TextView
    private lateinit var image: ImageView
    private lateinit var btn_kembali: ImageView
    lateinit var btn_keranjang: ImageView
    lateinit var btn_notifikasi: ImageView
    private lateinit var btn_beli: LinearLayout
    lateinit var produkLimit: ProdukLimit
    lateinit var produk: Produk
    lateinit var myDb: MyDatabase
    lateinit var div_angka: RelativeLayout
    lateinit var tv_angka: TextView
    lateinit var img_logo: ImageView
    lateinit var nama_toko: TextView
    lateinit var tv_alamat: TextView
    lateinit var rc_data: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setIni()
        myDb = MyDatabase.getInstance(this)!!
        getDetail()
        displayProduk()
        ceckkeranjang()
    }

    @SuppressLint("SetTextI18n")
    private fun ceckkeranjang() {
        var dataKeranjang = myDb.daoKeranjang().getAll()
        if (dataKeranjang.isNotEmpty()) {
            div_angka.visibility = View.VISIBLE
            tv_angka.text = "" + dataKeranjang.size
        } else {
            div_angka.visibility = View.GONE
        }
    }

    private var listProduk: ArrayList<Produk> = ArrayList()
    private fun displayProduk() {
        val layoutManager = GridLayoutManager(this, 2)

        rc_data.adapter = AdapterProduk(this, listProduk)
        rc_data.layoutManager = layoutManager
    }

    private fun getProduk() {
        ApiConfig.instanceRetrofit.produkIdAll(1).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>
            ) {
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk = res.produk
                    displayProduk()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                setError(t.message.toString())
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun getDetail() {
        val data = intent.getStringExtra("extra")
        produkLimit = Gson().fromJson<ProdukLimit>(data, ProdukLimit::class.java)
        produk = Gson().fromJson<Produk>(data, Produk::class.java)

        txt_namaproduk.text = produkLimit.name
        nama_toko.text = produkLimit.nama_toko
        txt_hargaproduk.text = Helper().formatRupiah(produkLimit.harga)
        tv_alamat.text = produkLimit.user.alamat
        txt_deskripsi.text = produkLimit.deskripsi
        namabarang.text = produkLimit.name
        val imageUrl = Util.produkUrl + produkLimit.image
        val logo_image = Util.logoToko + produkLimit.user.image

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(image)

        Picasso.get()
            .load(logo_image)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(img_logo)

        btn_keranjang.setOnClickListener {
            val produkData = myDb.daoKeranjang().getProduk(produk.id)
            if (produkData == null) {
                insert()
            } else {
                produkData.jumlah = produkData.jumlah + 1
                update(produkData)
            }
        }
        btn_kembali.setOnClickListener {
            super.onBackPressed()
        }

        btn_notifikasi.setOnClickListener {
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }

        btn_beli.setOnClickListener {
            val produkData = myDb.daoKeranjang().getProduk(produk.id)
            if (produkData == null) {
                insert()
            } else {
                produkData.jumlah = produkData.jumlah + 1
                update(produkData)
            }
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }
    }

    private fun insert() {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().insert(produk) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                ceckkeranjang()
                Log.d("respons", "data inserted" + produk.user_id)
            })

    }

    private fun update(produkData: Produk) {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().update(produkData) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                ceckkeranjang()
                Log.d("respons", "data inserted")
            })

    }

    private fun setIni() {
        txt_namaproduk = findViewById(R.id.nama_p)
        txt_hargaproduk = findViewById(R.id.harga_produk)
        image = findViewById(R.id.image_produk)
        txt_deskripsi = findViewById(R.id.deskripsi)
        btn_keranjang = findViewById(R.id.btn_keranjang)
        namabarang = findViewById(R.id.nama)
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_notifikasi = findViewById(R.id.btn_notifikasi)
        div_angka = findViewById(R.id.div_angka)
        tv_angka = findViewById(R.id.tv_angka)
        btn_beli = findViewById(R.id.btn_beli1)
        rc_data = findViewById(R.id.rc_data)
        img_logo = findViewById(R.id.img_logo)
        nama_toko = findViewById(R.id.nama_toko)
        tv_alamat = findViewById(R.id.alamat)
    }

    private fun setError(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    override fun onResume() {
        getProduk()
        super.onResume()
    }
}