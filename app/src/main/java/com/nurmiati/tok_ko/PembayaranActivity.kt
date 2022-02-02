package com.nurmiati.tok_ko

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.nurmiati.tok_ko.core.data.adapter.AdapterBank
import com.nurmiati.tok_ko.core.data.model.Bank
import com.nurmiati.tok_ko.core.data.model.Checkout
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.model.Transaksi
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {
    lateinit var rc_data: RecyclerView
    lateinit var btn_kembali: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        setInit()
        setButton()
        displayBank()
    }

    private fun displayBank() {
        val arrarBank = ArrayList<Bank>()
        arrarBank.add(Bank("Bank BCA", "091271231010", "Adi Murdayani", R.drawable.bca))
        arrarBank.add(Bank("Bank BRI", "019271231289", "Dewi Astuti", R.drawable.bri))
        arrarBank.add(Bank("Bank Mandiri", "090912837110", "Murdayani", R.drawable.mandiri))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rc_data.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rc_data.layoutManager = layoutManager
        rc_data.adapter = AdapterBank(arrarBank, object : AdapterBank.Listeners {
            override fun onCreate(data: Bank, index: Int) {
                bayar(data)
            }
        })
    }

    fun bayar(bank: Bank) {
        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank.nama

        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.titleText = "Loading..."
        dialog.show()
        ApiConfig.instanceRetrofit.checkout(checkout)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    dialog.setCancelable(false)
                    if (response.body() == null) {
                        Log.d("Response", "errors: " + response.message())
                    }else{
                        val res = response.body()!!
                        if (res.success == 1) {
                            val jsBank = Gson().toJson(bank, Bank::class.java)
                            val jsTransaksi = Gson().toJson(res.transaksi, Transaksi::class.java)
                            val jsCheckout = Gson().toJson(checkout, Checkout::class.java)
                            Log.d("Respon: ", "Data Bank: " + jsBank + "Data Transaksi: " + jsTransaksi)

                            SweetAlertDialog(this@PembayaranActivity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Sukses!")
                                .setContentText("Anda telah berhasil memilih metode pembayaran, klik tombol untuk melihat detail.")
                                .setConfirmText("Oke")
                                .setConfirmClickListener {
                                    val intent =
                                        Intent(this@PembayaranActivity, SuksesActivity::class.java)
                                    intent.putExtra("bank", jsBank)
                                    intent.putExtra("transaksi", jsTransaksi)
                                    intent.putExtra("checkout", jsCheckout)
                                    startActivity(intent)
                                    finish()
                                    it.dismissWithAnimation()
                                }
                                .show()
                        } else {
                            setError(res.message)
                            dialog.dismissWithAnimation()
                        }
                    }

                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    Log.d("Message", "Error: " + t.stackTraceToString())
                    setError("Terjadi kesalahan koneksi!")
                    dialog.dismissWithAnimation()
                }
            })
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setInit() {
        rc_data = findViewById(R.id.rc_data)
        btn_kembali = findViewById(R.id.btn_kembali)
    }

    private fun setError(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    override fun onRestart() {
        displayBank()
        super.onRestart()
    }
}