package com.nurmiati.tok_ko.core.data.model

data class ProdukChild(
    var id : Int,
    var name : String,
    var harga : String,
    var deskripsi : String,
    var kategori_id : Int,
    var image : String,
    var created_at : String,
    var updated_at : String,
    var stok : String,
    var berat : String,
    var user_id : Int,
    var nama_toko : String
)
