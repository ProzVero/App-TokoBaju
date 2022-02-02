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
import com.nurmiati.tok_ko.core.data.adapter.AdapterKategori
import com.nurmiati.tok_ko.core.data.adapter.AdapterProdukLimit
import com.nurmiati.tok_ko.core.data.adapter.AdapterSliderView
import com.nurmiati.tok_ko.core.data.model.*
import com.nurmiati.tok_ko.core.data.source.ApiConfig
import com.nurmiati.tok_ko.util.SharedPref
import com.smarteist.autoimageslider.SliderView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    lateinit var rv_data: RecyclerView
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

    private var listUser : ArrayList<User> = ArrayList()
    private var listKategori : ArrayList<Kategori> = ArrayList()
    private fun setDisplay() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        rv_data.adapter = AdapterKategori(requireActivity(), listKategori, listUser)
        rv_data.layoutManager = layoutManager
    }

    private fun getProduk() {
        shimmerFrameLayout.visibility = View.VISIBLE

        ApiConfig.instanceRetrofit.kategori().enqueue(object : Callback<ResponsModel>{
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>)
            {
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listKategori = res.kategori
                    setDisplay()
                } else {
                    setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                shimmerFrameLayout.visibility = View.GONE
                setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }

        })

        ApiConfig.instanceRetrofit.user().enqueue(object : Callback<ResponsModel>{
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>)
            {
                shimmerFrameLayout.visibility = View.GONE
                val res = response.body()!!
                if (res.success == 1) {
                    listUser = res.user
                } else {
                    //setError(res.message)
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                //shimmerFrameLayout.visibility = View.GONE
                //setError("Terjadi kesalahan koneksi!")
                Log.d("Response", "Error: " + t.message)
            }

        })
    }

    private fun setButton() {
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
        rv_data = view.findViewById(R.id.rv_data)
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
        shimmerFrameLayout.startShimmerAnimation()
        super.onResume()
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }
}