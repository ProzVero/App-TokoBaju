package com.nurmiati.tok_ko

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.drjacky.imagepicker.ImagePicker
import com.google.gson.Gson
import com.inyongtisto.myhelper.extension.toMultipartBody
import com.nurmiati.tok_ko.core.data.adapter.AdapterDetailProduk
import com.nurmiati.tok_ko.core.data.model.DetailTransaksi
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.model.Transaksi
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import com.nurmiati.tok_ko.util.Helper
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DetailRiwayatActivity : AppCompatActivity() {
    lateinit var status: TextView
    lateinit var tanggal: TextView
    lateinit var rc_data: RecyclerView
    lateinit var nama_pengirim: TextView
    lateinit var phone: TextView
    lateinit var alamat: TextView
    lateinit var total_belanja: TextView
    lateinit var total_kirim: TextView
    lateinit var kode_unik: TextView
    lateinit var total: TextView
    lateinit var btn_batal: LinearLayout
    lateinit var btn_kembali: ImageView
    lateinit var progress: ProgressBar
    lateinit var btn_buktitransfer: LinearLayout

    var transaksi = Transaksi()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_riwayat)
        setInit()
        setGetData()
        setButton()
    }

    private fun setButton() {
        btn_batal.setOnClickListener {
            setBatal()
        }
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_buktitransfer.setOnClickListener {
            imagePick()
        }
    }

    private fun batalTransaksi() {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.titleText = "Loading..."
        dialog.show()
        ApiConfig.instanceRetrofit.batalcheckout(transaksi.id)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    dialog.dismissWithAnimation()
                    val res = response.body()!!
                    if (res.success == 1) {
                        sukses("Transaksi telah berhasil dibatalkan.")
                        onBackPressed()
                    } else {
                        setError(res.message)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    Log.d("Message", "Error: $t")
                    dialog.dismissWithAnimation()
                    setError(t.message.toString())
                }
            })
    }

    private fun setBatal() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Apakah anda yakin?")
            .setContentText("Ingin membatalkan transaksi tersebut!")
            .setConfirmText("Ya")
            .setConfirmClickListener { sDialog -> // Showing simple toast message to user
                batalTransaksi()
                sDialog.dismissWithAnimation()
            }
            .setCancelText("Tidak")
            .setCancelClickListener {
                it.dismissWithAnimation()
            }
            .show()
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                Log.d("TAG", "URL Image: $uri")
                val fileUri: Uri = uri
                dialogUpload(File(fileUri.path))
            }
        }
    var alertDialog: AlertDialog? = null

    @SuppressLint("InflateParams")
    private fun dialogUpload(file: File) {
        val view = layoutInflater
        val layout = view.inflate(R.layout.upload_gambar, null)

        val imageView: ImageView = layout.findViewById(R.id.image)
        val btnUpload: LinearLayout = layout.findViewById(R.id.btn_upload)
        val btnGambar: LinearLayout = layout.findViewById(R.id.btn_gambarlain)

        Picasso.get()
            .load(file)
            .into(imageView)

        btnUpload.setOnClickListener {
            upload(file)
        }

        btnGambar.setOnClickListener {
            imagePick()
        }
        alertDialog = AlertDialog.Builder(this).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()
    }

    private fun upload(file: File) {
        val dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        dialog.titleText = "Loading..."
        dialog.show()

        val fileImage = file.toMultipartBody()
        ApiConfig.instanceRetrofit.uploadbukti(transaksi.id, fileImage!!)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    dialog.dismissWithAnimation()
                    if (response.isSuccessful) {
                        if (response.body()!!.success == 1) {
                            SweetAlertDialog(
                                this@DetailRiwayatActivity,
                                SweetAlertDialog.SUCCESS_TYPE
                            )
                                .setTitleText("Upload Transaksi")
                                .setContentText("Bukti transaksi telah berhasil diupload.")
                                .setConfirmText("Oke")
                                .setConfirmClickListener {
                                    alertDialog!!.dismiss()
                                    status.text = "DIBAYAR"
                                    onBackPressed()
                                    it.dismissWithAnimation()
                                }
                                .show()
                        } else {
                            setError(response.body()!!.message)
                        }
                    } else {
                        setError(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    dialog.dismissWithAnimation()
                    setError(t.message.toString())
                }
            })
    }

    private fun imagePick() {
        ImagePicker.with(this)
            .crop()
            .maxResultSize(512, 512)
            .createIntentFromDialog { launcher.launch(it) }
    }

    private fun setGetData() {
        val json = intent.getStringExtra("transaksi")
        transaksi = Gson().fromJson(json, Transaksi::class.java)
        setData(transaksi)
        displayProduk(transaksi.details)
    }

    private fun setData(transaksi: Transaksi) {
        total_belanja.text = Helper().formatRupiah(transaksi.total_harga)
        total_kirim.text = Helper().formatRupiah(transaksi.ongkir)
        kode_unik.text = Helper().formatRupiah(transaksi.kode_unik)
        total.text = Helper().formatRupiah(transaksi.total_transfer)

        nama_pengirim.text = transaksi.name
        phone.text = transaksi.phone
        alamat.text = transaksi.detail_lokasi

        status.text = transaksi.status
        val formatBaru = "dd MMMM yyyy, kk:mm:ss"
        tanggal.text = Helper().convertTanggal(transaksi.created_at, formatBaru)

        if (transaksi.status != "MENUNGGU") {
            btn_batal.visibility = View.GONE
            btn_buktitransfer.visibility = View.GONE
        }

        var color = getColor(R.color.menunggu)
        if (transaksi.status == "SELESAI") color = getColor(R.color.selesai)
        else if (transaksi.status == "BATAL") color = getColor(R.color.batal)
        status.setTextColor(color)
    }

    private fun displayProduk(transaksi: ArrayList<DetailTransaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rc_data.adapter = AdapterDetailProduk(transaksi)
        rc_data.layoutManager = layoutManager
    }

    private fun sukses(pesan: String) {
        SweetAlertDialog(this@DetailRiwayatActivity, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Transaksi")
            .setContentText(pesan)
            .setConfirmText("Oke")
            .setConfirmClickListener {
                it.dismissWithAnimation()
            }
            .show()
    }

    private fun setError(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    private fun setInit() {
        status = findViewById(R.id.status)
        tanggal = findViewById(R.id.tanggal)
        rc_data = findViewById(R.id.rc_data)
        nama_pengirim = findViewById(R.id.nama_pengirim)
        phone = findViewById(R.id.phone)
        alamat = findViewById(R.id.alamat)
        total_belanja = findViewById(R.id.total_belanja)
        total_kirim = findViewById(R.id.total_kirim)
        kode_unik = findViewById(R.id.kode_unik)
        total = findViewById(R.id.total)
        btn_batal = findViewById(R.id.btn_batal)
        btn_kembali = findViewById(R.id.btn_kembali)
        progress = findViewById(R.id.progress)
        btn_buktitransfer = findViewById(R.id.btn_buktitransfer)
    }
}