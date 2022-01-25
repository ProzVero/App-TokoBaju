package com.nurmiati.tok_ko

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.nurmiati.tok_ko.core.data.adapter.AdapterProduk
import com.nurmiati.tok_ko.core.data.adapter.AdapterProdukLimit
import com.nurmiati.tok_ko.core.data.model.Produk
import com.nurmiati.tok_ko.core.data.model.ProdukLimit
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllProduk : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var search: SearchView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_data: RecyclerView
    lateinit var shimmer: ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_produk)
        setInit()
    }

    private fun getData() {
        sw_data.isRefreshing = true
        shimmer.visibility = View.VISIBLE
        rc_data.visibility = View.GONE
        ApiConfig.instanceRetrofit.produk().enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data.isRefreshing = false
                shimmer.visibility = View.GONE
                rc_data.visibility = View.VISIBLE
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk = res.produklimit
                    setDisplay()
                } else {
                    setError(res.message)
                    sw_data.isRefreshing = false
                    shimmer.visibility = View.GONE
                    rc_data.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data.isRefreshing = false
                shimmer.visibility = View.GONE
                rc_data.visibility = View.VISIBLE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private var listProduk: ArrayList<ProdukLimit> = ArrayList()
    private fun setDisplay() {
        val layoutManager = GridLayoutManager(this, 2)
        rc_data.setHasFixedSize(true);
        rc_data.adapter = AdapterProdukLimit(this, listProduk)
        rc_data.layoutManager = layoutManager

        sw_data.setOnRefreshListener { getData() }
        val adapter = AdapterProdukLimit(this, listProduk)
        search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.getSearchData().filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getData()
                return false
            }

        })
    }

    private fun setError(pesan: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        search = findViewById(R.id.search)
        sw_data = findViewById(R.id.sw_data)
        rc_data = findViewById(R.id.rc_data)
        shimmer = findViewById(R.id.shimmer)

        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        getData()
        shimmer.startShimmerAnimation()
        super.onResume()
    }

    override fun onStop() {
        shimmer.stopShimmerAnimation()
        super.onStop()
    }
}