package com.nurmiati.tok_ko

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import com.nurmiati.tok_ko.util.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UbahPassword : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var l_password: TextInputLayout
    lateinit var e_password: TextInputEditText
    lateinit var l_konf_password: TextInputLayout
    lateinit var e_konf_password: TextInputEditText
    lateinit var btn_simpan: CardView
    lateinit var progress: ProgressBar
    lateinit var txt_register: TextView
    lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_password)
        s = SharedPref(this)
        setInit()
        setButton()
        cekvalidasi()
    }

    private fun cekvalidasi() {
        e_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_password.text.toString().isEmpty()) {
                    l_password.isErrorEnabled = false
                } else if (e_password.text.toString().length > 7) {
                    l_password.isErrorEnabled = false
                } else if (e_password.text.toString().isNotEmpty()) {
                    l_password.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        e_konf_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_konf_password.text.toString().isEmpty()) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString().length > 7) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString()
                        .matches(e_password.text.toString().toRegex())
                ) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString().isNotEmpty()) {
                    l_konf_password.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_simpan.setOnClickListener {
            if (validasi()) {
                ubahpassword()
            }
        }
    }

    private fun ubahpassword() {
        val id = s.getUser()!!.id
        val password = e_password.text.toString()

        progress.visibility = View.VISIBLE
        txt_register.visibility = View.GONE
        ApiConfig.instanceRetrofit.ubahpassword(id, password)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    progress.visibility = View.GONE
                    txt_register.visibility = View.VISIBLE
                    val respon = response.body()!!
                    if (respon.success == 1) {
                        sukses("Password anda berhasil diubah!")
                    } else {
                        progress.visibility = View.GONE
                        txt_register.visibility = View.VISIBLE
                        setError(respon.message)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    txt_register.visibility = View.VISIBLE
                    Log.d("Respon", "Pesan: " + t.message)
                    setError(t.message.toString())
                }
            })
    }

    private fun validasi(): Boolean {

        if (e_password.text.toString().isEmpty()) {
            l_password.isErrorEnabled = true
            l_password.error = "Kolom password tidak boleh kosong!"
            e_password.requestFocus()
            return false
        } else if (e_password.text.toString().length < 6) {
            l_password.isErrorEnabled = true
            l_password.error = "Password tidak boleh kurang dari 6 karakter!"
            e_password.requestFocus()
            return false
        }
        if (e_konf_password.text.toString().isEmpty()) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Kolom konfirmasi password tidak boleh kosong!"
            e_konf_password.requestFocus()
            return false
        } else if (e_konf_password.text.toString().length < 6) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Konfirmasi password tidak boleh kurang dari 6 karakter!"
            e_konf_password.requestFocus()
            return false
        } else if (!e_konf_password.text.toString().matches(e_password.text.toString().toRegex())) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Konfirmasi password tidak sama dengan password!"
            e_konf_password.requestFocus()
            return false
        }
        return true
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        l_password = findViewById(R.id.l_password)
        e_password = findViewById(R.id.e_password)
        l_konf_password = findViewById(R.id.l_konf_password)
        e_konf_password = findViewById(R.id.e_konf_password)
        btn_simpan = findViewById(R.id.btn_simpan)
        progress = findViewById(R.id.progress)
        txt_register = findViewById(R.id.txt_register)
    }

    private fun sukses(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Sukses!")
            .setContentText(pesan)
            .setConfirmText("Oke")
            .setConfirmClickListener {
                s.setStatusLogin(false)
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
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
}