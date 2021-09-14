package com.example.testsimplecursoradapter

import android.app.Activity
import android.database.Cursor
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import android.widget.SimpleCursorAdapter


class MainActivity : Activity() {
    var lvData: ListView? = null
    var db: DB? = null
    var scAdapter: SimpleCursorAdapter? = null
    var cursor: Cursor? = null

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // открываем подключение к БД
        db = DB(this)
        db!!.open()

        // получаем курсор
        cursor = db!!.allData
        startManagingCursor(cursor)

        // формируем столбцы сопоставления
        val from = arrayOf(DB.COLUMN_IMG, DB.COLUMN_TXT)
        val to = intArrayOf(R.id.ivImg, R.id.tvText)

        // создааем адаптер и настраиваем список
        scAdapter = SimpleCursorAdapter(this, R.layout.item, cursor, from, to)
        lvData = findViewById<View>(R.id.lvData) as ListView
        lvData!!.adapter = scAdapter

        // добавляем контекстное меню к списку
        registerForContextMenu(lvData)
    }

    // обработка нажатия кнопки
    fun onButtonClick(view: View?) {
        // добавляем запись
        db!!.addRec("sometext " + (cursor!!.count + 1), R.drawable.ic_launcher)
        // обновляем курсор
        cursor!!.requery()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            val acmi = item.menuInfo as AdapterContextMenuInfo
            // извлекаем id записи и удаляем соответствующую запись в БД
            db!!.delRec(acmi.id)
            // обновляем курсор
            cursor!!.requery()
            return true
        }
        return super.onContextItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
        db!!.close()
    }

    companion object {
        private const val CM_DELETE_ID = 1
    }
}