package com.achievka.moretech3

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.loader.content.AsyncTaskLoader
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    override fun onBackPressed() {
        showExitAlertDialog()
    }

    private fun showExitAlertDialog() {
        val li: LayoutInflater
        val promptsView: View
        val mDialogBuilder: MaterialAlertDialogBuilder
        //Получаем вид с файла popup_exit.xml, который применим для диалогового окна:
        li = LayoutInflater.from(this)
        promptsView = li.inflate(R.layout.popup_exit, null)

        //Создаем AlertDialog

        //Создаем AlertDialog
        mDialogBuilder = MaterialAlertDialogBuilder(this)

        //Настраиваем popup_exit.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView)

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
            .setCancelable(true)
            .setPositiveButton(
                R.string.yes
            ) { dialog, id ->

                // exiting from app
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton(R.string.no){
                    dialog, id -> dialog.cancel() }

        val alertDialog = mDialogBuilder.create()

//        val alertDialog: AlertDialog
        alertDialog.show()

    }

}