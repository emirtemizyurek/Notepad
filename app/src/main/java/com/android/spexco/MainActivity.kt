package com.android.spexco

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    //itemOptionsDialog
    private lateinit var imageviewDialogItemDetailsCancel: ImageView
    private lateinit var imageviewDialogItemDetails: ImageView
    private lateinit var textviewDialogItemDetailsTitle: TextView
    private lateinit var textviewDialogItemDetailsNotes: TextView
    private lateinit var textviewDialogItemDetailsCreationDate: TextView
    private lateinit var textviewDialogItemDetailsUpdateDate: TextView
    private lateinit var cardview_dialog_item_details_delete: CardView
    private lateinit var cardview_dialog_item_details_edit: CardView


    private lateinit var getImage: ActivityResultLauncher<String>

    private lateinit var dataBaseHelper: DataBaseHelper
    private lateinit var audioRecordTest: AudioRecordTest

    private lateinit var cardViewAddItem: CardView

    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var tempArrayList: ArrayList<OOPItem>

    private lateinit var textViewAddItemOk: TextView
    private lateinit var textViewAddItemCancel: TextView

    private lateinit var textInputEditTextEnterTitle: TextInputEditText
    private lateinit var textInputEditTextEnterNote: TextInputEditText

    private lateinit var cardViewRecord: CardView

    private lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var recyclerViewArrayList: ArrayList<OOPItem>

    private var currentHourString: String = ""

    private var checkSpinner: Int = 0

    private lateinit var imageViewSelectImage: ImageView

    private lateinit var spinnerSelectPriority: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initThis()
        initClickableItems()

        dataBaseHelper = DataBaseHelper(this@MainActivity)

        getItems()
        searchViewMethod()

        getImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            imageViewSelectImage.setImageURI(it)
        }


    }

    private fun searchViewMethod() {
        searchView.isIconified = true
        tempArrayList = ArrayList()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(p0: String?): Boolean {
                println(p0)
                tempArrayList.clear()
                val searchText = p0!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    recyclerViewArrayList.forEach {
                        if (it.note.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempArrayList.add(it)
                        }
                        if (it.title.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempArrayList.add(it)
                        }
                    }

                    recyclerViewAdapter.changeItems(tempArrayList)
                } else {
                    tempArrayList.clear()
                    tempArrayList.addAll(recyclerViewArrayList)
                    recyclerViewAdapter.changeItems(recyclerViewArrayList)
                }
                return false
            }
        })
    }

    private fun initThis() {
        cardViewAddItem = findViewById(R.id.cardview_add_note)
        searchView = findViewById(R.id.searchview)
    }

    private fun initClickableItems() {
        cardViewAddItem.setOnClickListener {
            itemAdd()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getItems() {

        recyclerViewArrayList = ArrayList()

        for (v in dataBaseHelper.getAllItem()) {

            val add = OOPItem(
                v.id,
                v.title,
                v.note,
                v.image,
                v.creation_time,
                v.update_time,
                v.priority
            )

            recyclerViewArrayList.add(add)

            recyclerView = findViewById(R.id.recyclerview_notes)
            recyclerViewAdapter = RecyclerViewAdapter(this, recyclerViewArrayList)


            recyclerViewAdapter.setOnClickListener(object :
                RecyclerViewAdapter.OnItemClickListener {
                override fun onItemLongClick(
                    position: Int,
                    item: OOPItem,
                    arrayList: ArrayList<OOPItem>
                ) {
                    itemOptionsDialog(
                        item.id,
                        item.title,
                        item.note,
                        item.image,
                        item.creation_time,
                        item.update_time,
                        item.priority,
                        position,
                        arrayList,
                        recyclerViewAdapter
                    )
                }
            })

            recyclerView.layoutManager =
                LinearLayoutManager(this, GridLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.adapter = recyclerViewAdapter
            recyclerViewAdapter.notifyDataSetChanged()

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemOptionsDialog(
        id: Int?,
        title: String?,
        notes: String?,
        imageId: ByteArray?,
        creation_time: String?,
        update_time: String?,
        priority: String?,
        index: Int,
        arrayList: ArrayList<OOPItem>,
        recyclerViewAdapter: RecyclerViewAdapter
    ) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_item_details)
        dialog.show()

        imageviewDialogItemDetailsCancel =
            dialog.findViewById(R.id.imageview_dialog_item_details_cancel)
        textviewDialogItemDetailsTitle =
            dialog.findViewById(R.id.textview_dialog_item_details_title)
        textviewDialogItemDetailsNotes =
            dialog.findViewById(R.id.textview_dialog_item_details_notes)
        imageviewDialogItemDetails =
            dialog.findViewById(R.id.imageview_dialog_item_details)
        textviewDialogItemDetailsCreationDate =
            dialog.findViewById(R.id.textview_dialog_item_details_creation_date)
        textviewDialogItemDetailsUpdateDate =
            dialog.findViewById(R.id.textview_dialog_item_details_update_date)
        cardview_dialog_item_details_delete =
            dialog.findViewById(R.id.cardview_dialog_item_details_delete)
        cardview_dialog_item_details_edit =
            dialog.findViewById(R.id.cardview_dialog_item_details_edit)
        cardViewRecord = dialog.findViewById(R.id.cardview_dialog_item_details_record_audio)


        /**
         * Setleme işlemi
         * */
        textviewDialogItemDetailsTitle.text = title
        textviewDialogItemDetailsNotes.text = notes
        imageviewDialogItemDetails.setImageBitmap(byteArrayToBitmap(imageId))
        textviewDialogItemDetailsCreationDate.text = creation_time
        textviewDialogItemDetailsUpdateDate.text = update_time


        imageviewDialogItemDetailsCancel.setOnClickListener {
            dialog.dismiss()
        }

        /**
         * SETONCLICKLISTENERS
         * */
        cardview_dialog_item_details_delete.setOnClickListener {
            dataBaseHelper.deleteItem((id!!))
            arrayList.removeAt(index)
            getItems()
            recyclerViewAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        cardview_dialog_item_details_edit.setOnClickListener {
            dialog.dismiss()
            itemUpdate(
                id,
                title,
                notes,
                imageId,
                creation_time,
                update_time,
                priority
            )
        }

        cardViewRecord.setOnClickListener {
            AudioRecordTest.audioName = title!!
            val intent = Intent(this, AudioRecordTest::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemAdd() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_add_item)
        dialog.show()

        textViewAddItemOk = dialog.findViewById(R.id.textView_add_item_ok)
        textViewAddItemCancel = dialog.findViewById(R.id.textView_add_item_cancel)

        textInputEditTextEnterTitle = dialog.findViewById(R.id.textInputEditText_enter_tltle)
        textInputEditTextEnterNote = dialog.findViewById(R.id.textInputEditText_enter_note)

        imageViewSelectImage = dialog.findViewById(R.id.imageview_select_image)

        spinnerSelectPriority = dialog.findViewById(R.id.spinner_select_priority)
        val spinnerItemsForSelectLayout = arrayOf(
            "High",
            "Normal",
            "Low"
        )
        val arrayAdapterForSelectLayout =
            ArrayAdapter(this, R.layout.spinner_type_only_text, spinnerItemsForSelectLayout)
        spinnerSelectPriority.adapter = arrayAdapterForSelectLayout
        spinnerSelectPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        /**
         * SETONCLICKLISTENERS
         * */
        imageViewSelectImage.setOnClickListener {
            getImage.launch("image/*")
        }

        textViewAddItemCancel.setOnClickListener {
            dialog.dismiss()
        }

        textViewAddItemOk.setOnClickListener {
            val getTitle: String = textInputEditTextEnterTitle.text.toString()
            val getNote: String = textInputEditTextEnterNote.text.toString()
            val getImage = imageViewSelectImage.drawable.toBitmap(250, 250)
            val getPriority = spinnerSelectPriority.selectedItem

            //Eğer Edittext boşsa kullanıcıya uyarı veriliyor
            if (TextUtils.isEmpty(getNote)) {
                textInputEditTextEnterNote.error = "Please, Enter Note"
                return@setOnClickListener
            }

            takeDate()
            val itemData = ItemData(
                title = getTitle,
                note = getNote,
                image = bitmapToByteArray(getImage),
                creation_time = currentHourString,
                update_time = currentHourString,
                priority = getPriority.toString()
            )
            dataBaseHelper.addItem(itemData)
            getItems()
            dialog.dismiss()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemUpdate(
        id: Int?,
        title: String?,
        notes: String?,
        imageId: ByteArray?,
        creation_time: String?,
        update_time: String?,
        priority: String?
    ) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_add_item)
        dialog.show()

        textViewAddItemOk = dialog.findViewById(R.id.textView_add_item_ok)
        textViewAddItemCancel = dialog.findViewById(R.id.textView_add_item_cancel)

        textInputEditTextEnterTitle = dialog.findViewById(R.id.textInputEditText_enter_tltle)
        textInputEditTextEnterNote = dialog.findViewById(R.id.textInputEditText_enter_note)

        imageViewSelectImage = dialog.findViewById(R.id.imageview_select_image)

        spinnerSelectPriority = dialog.findViewById(R.id.spinner_select_priority)
        val spinnerItemsForSelectPriority = arrayOf(
            "High",
            "Normal",
            "Low"
        )
        val arrayAdapterForSelectLayout =
            ArrayAdapter(this, R.layout.spinner_type_only_text, spinnerItemsForSelectPriority)
        spinnerSelectPriority.adapter = arrayAdapterForSelectLayout
        spinnerSelectPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        imageViewSelectImage.setOnClickListener {
            getImage.launch("image/*")
        }

        textViewAddItemCancel.setOnClickListener {
            dialog.dismiss()
        }


        textInputEditTextEnterTitle.text!!.clear()
        textInputEditTextEnterNote.text!!.clear()

        textInputEditTextEnterTitle.append(title)
        textInputEditTextEnterNote.append(notes)
        imageViewSelectImage.setImageBitmap(byteArrayToBitmap(imageId))
        checkSpinnerSelect(priority)
        spinnerSelectPriority.setSelection(checkSpinner)



        textViewAddItemOk.setOnClickListener {
            val getTitle: String = textInputEditTextEnterTitle.text.toString()
            val getNote: String = textInputEditTextEnterNote.text.toString()
            val getImage = imageViewSelectImage.drawable.toBitmap(1000, 1000)
            val getPriority = spinnerSelectPriority.selectedItem

            //Eğer Edittext boşsa kullanıcıya uyarı veriliyor
            if (TextUtils.isEmpty(getNote)) {
                textInputEditTextEnterNote.error = "Please, Enter Note"
                return@setOnClickListener
            }




            takeDate()
            val itemData = ItemData(
                id = id,
                title = getTitle,
                note = getNote,
                image = bitmapToByteArray(getImage),
                creation_time = creation_time!!,
                update_time = currentHourString,
                priority = getPriority.toString()
            )
            dataBaseHelper.updateItem(itemData)
            getItems()
            dialog.dismiss()
        }

    }

    private fun checkSpinnerSelect(priority: String?) {
        when (priority) {
            "High" -> {
                checkSpinner = 0
            }
            "Normal" -> {
                checkSpinner = 1
            }
            "Low" -> {
                checkSpinner = 2
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()
        bitmap.recycle()
        return byteArray
    }

    private fun byteArrayToBitmap(imageData: ByteArray?): Bitmap {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
    }

    @SuppressLint("SimpleDateFormat")
    private fun takeDate() {
        val sdf = SimpleDateFormat("dd MM yyyy | hh:mm")
        currentHourString = sdf.format(Date())
    }
}