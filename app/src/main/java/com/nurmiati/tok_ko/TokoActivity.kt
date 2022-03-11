package com.nurmiati.tok_ko

import android.content.Context
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.nurmiati.tok_ko.core.data.adapter.AdapterKategori
import com.nurmiati.tok_ko.core.data.adapter.AdapterProduk
import com.nurmiati.tok_ko.core.data.adapter.AdapterProdukLimit
import com.nurmiati.tok_ko.core.data.model.Produk
import com.nurmiati.tok_ko.core.data.model.ProdukLimit
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.model.User
import com.nurmiati.tok_ko.core.data.room.MyDatabase
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import com.nurmiati.tok_ko.util.Util
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokoActivity : AppCompatActivity() {
    lateinit var mContext : Context
    lateinit var btnBack : ImageView
    lateinit var imgLogo : ImageView
    lateinit var txtNamaToko : TextView
    lateinit var txtEmail : TextView
    lateinit var txtPhone : TextView
    lateinit var txtAlamat : TextView
    //lateinit var txtJmlProduk : TextView
    //lateinit var txtJmlTransaksi : TextView
    lateinit var rcData : RecyclerView
    lateinit var myDb: MyDatabase
    var userId = ""
    private var listUser : ArrayList<User> = ArrayList()
    private var listProduk: ArrayList<ProdukLimit> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toko)
        mContext = this
        myDb = MyDatabase.getInstance(this)!!
        initComponent()
        getData()
        onAction()
    }

    private fun onAction() {
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initComponent() {
        btnBack = findViewById(R.id.btn_kembali)
        imgLogo = findViewById(R.id.img_logo)
        txtNamaToko = findViewById(R.id.nama_toko)
        txtEmail = findViewById(R.id.email)
        txtPhone = findViewById(R.id.phone)
        txtAlamat = findViewById(R.id.alamat)
        //txtJmlProduk = findViewById(R.id.txt_jml_produk)
        //txtJmlTransaksi = findViewById(R.id.txt_jml_transaksi)
        rcData = findViewById(R.id.rc_data)
    }

    private fun getData() {
       userId = intent.getStringExtra("user_id").toString()
        Log.e("getdata",""+userId)
        if (userId!="" && userId!="null" && userId.isNotEmpty()){
            ApiConfig.instanceRetrofit.getIdUser(userId.toInt()).enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                )
                {
                    val res = response.body()!!
                    if (res.success == 1) {
                        listUser = res.user
                        for (d in listUser){
                            txtNamaToko.text = d.nama_toko
                            txtEmail.text = d.email
                            txtPhone.text = d.phone
                            txtAlamat.text = d.alamat
                            val logo_toko = Util.logoToko + d.image
                            Picasso.get()
                                .load(logo_toko)
                                .placeholder(R.drawable.ic_shopping_bag2)
                                .error(R.drawable.ic_shopping_bag2)
                                .into(imgLogo)
                        }
                    } else {
                        setError(res.message)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    setError("Terjadi kesalahan koneksi!")
                    Log.d("Response", "Error: " + t.message)
                }

            })

            ApiConfig.instanceRetrofit.get_limit_id(userId.toInt()).enqueue(object : Callback<ResponsModel>{
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>)
                {
                    val res = response.body()!!
                    if (res.success == 1) {
                        listProduk = res.produklimit
                        val layoutManager = GridLayoutManager(mContext,2)

                        rcData.adapter = AdapterProdukLimit(this@TokoActivity, listProduk)
                        rcData.layoutManager = layoutManager
                    } else {
                        setError(res.message)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    setError("Terjadi kesalahan koneksi!")
                    Log.d("Response", "Error: " + t.message)
                }

            })
        }
    }

    private fun setError(pesan: String) {
        SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}