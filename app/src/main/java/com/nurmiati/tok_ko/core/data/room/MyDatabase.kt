package com.nurmiati.tok_ko.core.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nurmiati.tok_ko.core.data.model.Alamat
import com.nurmiati.tok_ko.core.data.model.Produk

@Database(entities = [Produk::class, Alamat::class] /* List model Ex:keranjangmodel */, version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun daoKeranjang(): DaoKeranjang // daokeranjang
    abstract fun daoAlamat(): DaoAlamat // daokeranjang

    companion object {
        private var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase? {
            if (INSTANCE == null) {
                synchronized(MyDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MyDatabase::class.java, "MyDatabaseName1" // Database Name
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
