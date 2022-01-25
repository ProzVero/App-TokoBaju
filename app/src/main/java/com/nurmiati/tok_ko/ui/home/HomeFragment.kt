package com.nurmiati.tok_ko.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.nurmiati.tok_ko.AllProduk
import com.nurmiati.tok_ko.DetailProduk
import com.nurmiati.tok_ko.R
import com.nurmiati.tok_ko.core.data.adapter.AdapterProdukLimit
import com.nurmiati.tok_ko.core.data.adapter.AdapterSliderView
import com.nurmiati.tok_ko.core.data.model.ProdukLimit
import com.nurmiati.tok_ko.core.data.model.ResponsModel
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import com.nurmiati.tok_ko.util.SharedPref
import com.smarteist.autoimageslider.SliderView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    lateinit var rc_data: RecyclerView
    lateinit var rc_data2: RecyclerView
    lateinit var rc_data3: RecyclerView
    lateinit var rc_data4: RecyclerView
    lateinit var rc_data5: RecyclerView
    lateinit var sw_data1: SwipeRefreshLayout
    lateinit var btn_all: TextView
    lateinit var btn_all2: TextView
    lateinit var btn_all3: TextView
    lateinit var btn_all4: TextView
    lateinit var btn_all5: TextView
    lateinit var search_data: ImageView
    lateinit var s: SharedPref
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        setInit(view)
        s = SharedPref(requireActivity())
        setButton()
        return view
    }

    private var listProduk1: ArrayList<ProdukLimit> = ArrayList()
    private var listProduk2: ArrayList<ProdukLimit> = ArrayList()
    private var listProduk3: ArrayList<ProdukLimit> = ArrayList()
    private var listProduk4: ArrayList<ProdukLimit> = ArrayList()
    private var listProduk5: ArrayList<ProdukLimit> = ArrayList()
    private fun setDisplay() {
        val layoutManager1 = LinearLayoutManager(activity)
        layoutManager1.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager3 = LinearLayoutManager(activity)
        layoutManager3.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager4 = LinearLayoutManager(activity)
        layoutManager4.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager5 = LinearLayoutManager(activity)
        layoutManager5.orientation = LinearLayoutManager.HORIZONTAL

        rc_data.adapter = AdapterProdukLimit(requireActivity(), listProduk1)
        rc_data.layoutManager = layoutManager1

        sw_data1.setOnRefreshListener { getProduk() }

        rc_data2.adapter = AdapterProdukLimit(requireActivity(), listProduk2)
        rc_data2.layoutManager = layoutManager2

        rc_data3.adapter = AdapterProdukLimit(requireActivity(), listProduk3)
        rc_data3.layoutManager = layoutManager3

        rc_data4.adapter = AdapterProdukLimit(requireActivity(), listProduk4)
        rc_data4.layoutManager = layoutManager4

        rc_data5.adapter = AdapterProdukLimit(requireActivity(), listProduk5)
        rc_data5.layoutManager = layoutManager5
    }

    private fun getProduk() {
        sw_data1.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.produkId(13).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk1 = res.produklimit
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun getProduk2() {
        sw_data1.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.produkId(14).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk2 = res.produklimit
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun getProduk3() {
        sw_data1.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.produkId(15).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk3 = res.produklimit
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun getProduk4() {
        sw_data1.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.produkId(16).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk4 = res.produklimit
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun getProduk5() {
        sw_data1.isRefreshing = true
        shimmerFrameLayout.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.produkId(17).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk5 = res.produklimit
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private fun setButton() {
        btn_all.setOnClickListener {
            val toko1 = Intent(requireActivity(), DetailProduk::class.java)
            toko1.putExtra("user_id",13)
            startActivity(toko1)
        }
        btn_all2.setOnClickListener {
            val toko2= Intent(requireActivity(), DetailProduk::class.java)
            toko2.putExtra("user_id",14)
            startActivity(toko2)
        }
        btn_all3.setOnClickListener {
            val toko3 = Intent(requireActivity(), DetailProduk::class.java)
            toko3.putExtra("user_id",15)
            startActivity(toko3)
        }
        btn_all4.setOnClickListener {
            val toko4 = Intent(requireActivity(), DetailProduk::class.java)
            toko4.putExtra("user_id",16)
            startActivity(toko4)
        }
        btn_all5.setOnClickListener {
            val toko5 = Intent(requireActivity(), DetailProduk::class.java)
            toko5.putExtra("user_id",17)
            startActivity(toko5)
        }

        search_data.setOnClickListener {
            startActivity(Intent(requireActivity(), AllProduk::class.java))
        }
    }

    private fun setError(pesan: String) {
        SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText(pesan)
            .show()
    }

    private fun setInit(view: View) {
        val imageSlider = view.findViewById<SliderView>(R.id.imageSlider)
        rc_data = view.findViewById(R.id.rc_data)
        rc_data2 = view.findViewById(R.id.rc_data2)
        rc_data3 = view.findViewById(R.id.rc_data3)
        rc_data4 = view.findViewById(R.id.rc_data4)
        rc_data5 = view.findViewById(R.id.rc_data5)
        sw_data1 = view.findViewById(R.id.sw_data1)
        btn_all = view.findViewById(R.id.btn_all)
        btn_all2 = view.findViewById(R.id.btn_all2)
        btn_all3 = view.findViewById(R.id.btn_all3)
        btn_all4 = view.findViewById(R.id.btn_all4)
        btn_all5 = view.findViewById(R.id.btn_all5)
        search_data = view.findViewById(R.id.search_data)
        shimmerFrameLayout = view.findViewById(R.id.shimmer)

        val imageList: ArrayList<Int> = ArrayList()
        imageList.add(R.drawable.slide1)
        imageList.add(R.drawable.slide2)
        imageList.add(R.drawable.slide3)
        setImageInSlider(imageList, imageSlider)
    }

    private fun setImageInSlider(images: ArrayList<Int>, imageSlider: SliderView) {
        val adapter = AdapterSliderView()
        adapter.renewItems(images)
        imageSlider.setSliderAdapter(adapter)
        imageSlider.isAutoCycle = true
        imageSlider.startAutoCycle()
    }

    override fun onResume() {
        getProduk()
        getProduk2()
        getProduk3()
        getProduk4()
        getProduk5()
        shimmerFrameLayout.startShimmerAnimation()
        super.onResume()
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }
}