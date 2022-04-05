package com.android.spexco

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val database_name = "Spexco.db"

class DataBaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    database_name, null, 1
) {

    var itemArrayList = ArrayList<ItemData>()
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE IF NOT EXISTS " +
                "${DBConstants.TABLE_NAME}( " +
                "${DBConstants.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${DBConstants.COLUMN_TITLE} VARCHAR, " +
                "${DBConstants.COLUMN_NOTE} VARCHAR, " +
                "${DBConstants.COLUMN_IMAGE} BLOB, " +
                "${DBConstants.COLUMN_CREATION_TIME} VARCHAR, " +
                "${DBConstants.COLUMN_UPDATE_TIME} VARCHAR, " +
                "${DBConstants.COLUMN_PRIORITY} VARCHAR )"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun addItem(itemData: ItemData): Long {
        val values = ContentValues()

        values.put(DBConstants.COLUMN_TITLE, itemData.title)
        values.put(DBConstants.COLUMN_NOTE, itemData.note)
        values.put(DBConstants.COLUMN_IMAGE, itemData.image)
        values.put(DBConstants.COLUMN_CREATION_TIME, itemData.creation_time)
        values.put(DBConstants.COLUMN_UPDATE_TIME, itemData.update_time)
        values.put(DBConstants.COLUMN_PRIORITY, itemData.priority)

        val db = this.writableDatabase
        val success = db.insert(DBConstants.TABLE_NAME, null, values)
//        db.close()
        return success

    }

    fun deleteItem(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            DBConstants.TABLE_NAME,
            "${DBConstants.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun updateItem(itemData: ItemData): Int {
        val values = ContentValues()
        values.put(DBConstants.COLUMN_TITLE, itemData.title)
        values.put(DBConstants.COLUMN_NOTE, itemData.note)
        values.put(DBConstants.COLUMN_IMAGE, itemData.image)
        values.put(DBConstants.COLUMN_CREATION_TIME, itemData.creation_time)
        values.put(DBConstants.COLUMN_UPDATE_TIME, itemData.update_time)
        values.put(DBConstants.COLUMN_PRIORITY, itemData.priority)

        val db = this.writableDatabase
        val success = db.update(
            DBConstants.TABLE_NAME, values, "${DBConstants.COLUMN_ID} = ?",
            arrayOf(itemData.id.toString())
        )
        db.close()
        return success
    }


    fun getAllItem(): ArrayList<ItemData> {
        val sql = "select * from ${DBConstants.TABLE_NAME}"
        val db = this.readableDatabase
        itemArrayList = ArrayList<ItemData>()
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()) {
            do {
                val id: Int = cursor.getInt(0)
                val title = cursor.getString(1)
                val note = cursor.getString(2)
                val image = cursor.getBlob(3)
                val creationDate = cursor.getString(4)
                val updateDate = cursor.getString(5)
                val priority = cursor.getString(6)

                itemArrayList.add(
                    ItemData(
                        id,
                        title,
                        note,
                        image,
                        creationDate,
                        updateDate,
                        priority
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return itemArrayList
    }

}