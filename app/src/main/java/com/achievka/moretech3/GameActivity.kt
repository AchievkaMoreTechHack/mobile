package com.achievka.moretech3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.achievka.moretech3.api.APIConfig.ACTION_TYPE_BUTTON
import com.achievka.moretech3.api.APIConfig.ACTION_TYPE_EDITTEXT
import com.achievka.moretech3.api.APIConfig.ACTION_TYPE_GENDER
import com.achievka.moretech3.api.GameRepository
import com.achievka.moretech3.api.GetStoriesService
import com.achievka.moretech3.model.Story
import com.achievka.moretech3.viewmodel.GameViewModel
import com.achievka.moretech3.viewmodel.GameViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameActivity : AppCompatActivity() {

    companion object{
        const val TAG = "GameActivity"
    }

    lateinit var viewModel: GameViewModel

    private val getStoryService = GetStoriesService.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        viewModel = ViewModelProvider(this, GameViewModelFactory(GameRepository(GetStoriesService.getStoryService!!))).get(GameViewModel::class.java)


        viewModel.currentStoryId.observe(this, {
            Log.d(TAG, "onCreate, viewModel.currentSceneId.observe: $it")
            updateStory(getStoryById(it, viewModel.stories.value?.stories))       //TODO CHECK ?
        })

        viewModel.stories.observe(this, Observer {
            Log.d(TAG, "onCreate, viewModel.scenes.observe: $it")
            viewModel.currentStoryId.value = it.startId
        })
        viewModel.errorMessage.observe(this, Observer {
            //TODO Observe
        })

        viewModel.getAllStories()


    }

    override fun onBackPressed() {
        showExitAlertDialog()
    }

    fun getStoryById(sceneId: Int, scenes: List<Story>?): Story? {       //TODO CHECK ?
        if (scenes != null) {       //TODO CHECK FOR NULL
            for (scene in scenes){
                if (scene.id == sceneId){
                    return scene
                }
            }
            return null
        }else{
            return null
        }
    }

    fun updateStory(story: Story?) {       //TODO CHECK ? (and !!)
        Log.d(TAG, "updateScene: $story")
        if (story != null){
            val glide = Glide.with(this)
            val story_background = findViewById<ImageView>(R.id.story_background)
            val story_foreground = findViewById<ImageView>(R.id.story_foreground)
            val dialogText = findViewById<TextView>(R.id.dialog_text)

            //Setting background
            glide.load(story.background).into(story_background)

            //Setting text with checking gender type
            dialogText.text = if (viewModel.isMale.value!!) story.storyMale?.get(0) else story.storyFemale?.get(0)

            //Setting answer action components with checking
            setDialogActionMode(story.actionType!!)

//            if (story_background != null) {
//                glide.load(story.character).into(story_background)
//            }

        }
    }

    fun setDialogActionMode(actionType: String){
        if (actionType == ACTION_TYPE_BUTTON){

        }else if (actionType == ACTION_TYPE_EDITTEXT){

        }else if (actionType == ACTION_TYPE_GENDER){

        }else{

        }
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