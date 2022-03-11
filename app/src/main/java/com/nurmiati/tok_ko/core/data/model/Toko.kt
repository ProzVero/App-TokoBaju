package com.nurmiati.tok_ko.core.data.model

data class Toko(
    val id : Int = 0,
    val nama_kategori : String = "",
    val produk : ArrayList<ProdukChild>
)