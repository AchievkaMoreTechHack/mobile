package com.achievka.moretech3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.achievka.moretech3.api.APIConfig.ACTION_TYPE_BUTTON
import com.achievka.moretech3.api.APIConfig.ACTION_TYPE_EDITTEXT
import com.achievka.moretech3.api.GameRepository
import com.achievka.moretech3.api.GetStoriesService
import com.achievka.moretech3.model.Story
import com.achievka.moretech3.viewmodel.GameViewModel
import com.achievka.moretech3.viewmodel.GameViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.core.view.size
import androidx.core.widget.doOnTextChanged


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
            val isMale = viewModel.isMale.value
            val glide = Glide.with(this)
            val story_background = findViewById<ImageView>(R.id.story_background)
            val story_character = findViewById<ImageView>(R.id.story_character)
            val dialogText = findViewById<TextView>(R.id.dialog_text)

            //Setting background
            glide.load(story.background).into(story_background)

            //Setting character
            if (story.character != null)
            glide.load(story.character).into(story_character)

            var text = (if (isMale!!) story.storyMale?.get(0) else story.storyFemale?.get(0))

            if (viewModel.username.value != null){
                text = text?.replace("{username}", viewModel.username.value!!)
            }
            //Setting text with checking gender type
            dialogText.text = text

            //Setting answer action components with checking
            val actionData = if (isMale) story.actionDataMale else story.actionDataFemale
            if (story.actionType == ACTION_TYPE_BUTTON){
                if (story.actionDataMale?.size!! <= 4){
                    setDialogActionMode(0)
                    val button1 = findViewById<Button>(R.id.choose_button_1)
                    val button2 = findViewById<Button>(R.id.choose_button_2)
                    button1.text = actionData?.get(0)
                    button2.text = actionData?.get(2)
                    button1.setOnClickListener { viewModel.currentStoryId.value = Integer.parseInt(actionData?.get(1)!!) }
                    button1.setOnClickListener { viewModel.currentStoryId.value = Integer.parseInt(actionData?.get(3)!!) }
                }else{
                    setDialogActionMode(1)
                    val button1 = findViewById<Button>(R.id.choose_button_1)
                    val button2 = findViewById<Button>(R.id.choose_button_2)
                    val button3 = findViewById<Button>(R.id.choose_button_3)
                    button1.text = actionData?.get(0)
                    button2.text = actionData?.get(2)
                    button3.text = actionData?.get(4)
                    button1.setOnClickListener { viewModel.currentStoryId.value = Integer.parseInt(actionData?.get(1)!!) }
                    button1.setOnClickListener { viewModel.currentStoryId.value = Integer.parseInt(actionData?.get(3)!!) }
                    button3.setOnClickListener { viewModel.currentStoryId.value = Integer.parseInt(actionData?.get(5)!!) }
                }
            }else if (story.actionType == ACTION_TYPE_EDITTEXT){
                setDialogActionMode(2)
                val editText = findViewById<EditText>(R.id.text_input_edittext)
                val button = findViewById<Button>(R.id.button)
                val hint = actionData?.get(0)
                editText.hint = hint
                if(actionData?.size!! > 1) {
                    if (actionData.get(1) == "str") {
                        editText.inputType = InputType.TYPE_CLASS_TEXT
                    } else {
                        editText.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                }
                editText.doOnTextChanged { text, start, before, count ->
                    if (editText.inputType == InputType.TYPE_CLASS_TEXT ){
                        button.isEnabled = text?.all { char -> !char.isLetter() } == false
                    }else{
                        button.isEnabled = text?.length!! > 0
                    }
                }
                button.setOnClickListener {
                    //checking for username or userAge fields
                    if (editText.inputType == InputType.TYPE_CLASS_TEXT){
                        viewModel.username.value = editText.text.toString()
                    }else{
                        viewModel.userAge.value = Integer.parseInt(editText.text.toString())
                    }
                    if (actionData?.size!! > 2) {
                        viewModel.currentStoryId.value = Integer.parseInt(actionData.get(2))
                    }else{
                        viewModel.currentStoryId.value = Integer.parseInt(story.next!!)
                    }
                }


            } else{
                setDialogActionMode(0)
                val button1 = findViewById<Button>(R.id.choose_button_1)
                val button2 = findViewById<Button>(R.id.choose_button_2)
                button1.text = actionData?.get(0)
                button2.text = actionData?.get(1)
                button1.setOnClickListener {
                    viewModel.isMale.value = true
                    viewModel.currentStoryId.value = Integer.parseInt(story.next!!)
                }
                button1.setOnClickListener {
                    viewModel.isMale.value = false
                    viewModel.currentStoryId.value = Integer.parseInt(story.next!!)
                }
            }

        }
    }

    fun setDialogActionMode(actionTypeCode: Int){
        Log.d(TAG, "setDialogActionMode: ")
        val index = 2
        val parent = findViewById<ViewGroup>(R.id.dialog_linearlayout)
        var containerView = if (parent.size > index-1) parent[index] else null
        if (containerView != null) parent.removeView(containerView)
        if (actionTypeCode == 0){
            containerView = layoutInflater.inflate(R.layout.answer_duo_button, parent, false)
        }else if (actionTypeCode == 1){
            containerView = layoutInflater.inflate(R.layout.answer_trio_button, parent, false)
        }else{
//            Toast.makeText(this, "TEIKHSIAUFGUIOAJK", Toast.LENGTH_LONG).show()
            containerView = layoutInflater.inflate(R.layout.answer_text_input, parent, false)
        }

        parent.addView(containerView, index)
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