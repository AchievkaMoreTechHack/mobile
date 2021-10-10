package com.achievka.moretech3.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.opengl.Visibility
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
import com.achievka.moretech3.R
import com.achievka.moretech3.api.APIConfig.ACTION_TYPE_GENDER
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView


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
            viewModel.currentDialogIndex.value = 0
        })
        viewModel.currentDialogIndex.observe(this, {
            Log.d(TAG, "onCreate, viewModel.currentSceneId.observe: $it")
            updateStory(getStoryById(viewModel.currentStoryId.value!!, viewModel.stories.value?.stories))       //TODO CHECK ?
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

    fun changeStory(id: Int){
        viewModel.currentStoryId.value = id
        viewModel.currentDialogIndex.value = 0

    }
    var descriptionFlag = true

    fun updateStory(story: Story?) {       //TODO CHECK ? (and !!)

        Log.d(TAG, "updateScene: $story")
        if (story != null){
            val isMale = viewModel.isMale.value
            val glide = Glide.with(this)
            val story_background = findViewById<ImageView>(R.id.story_background)
            val story_character = findViewById<ImageView>(R.id.story_character)
            val dialogText = findViewById<TextView>(R.id.dialog_text)

            //Setting background
            if (story.background != null) {
                glide.load(story.background).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        story_background.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        story_background.visibility = View.VISIBLE
                        return false
                    }
                }).into(story_background)
            }else{
                story_background.visibility = View.INVISIBLE
            }
            //Setting character
            if (story.character != null) {
                glide.load(story.character).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        story_character.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        story_character.visibility = View.VISIBLE
                        return false
                    }
                }).into(story_character)
            }else{
                story_character.visibility = View.INVISIBLE
            }
            val currentDialogIndex = viewModel.currentDialogIndex.value!!

            val dialogs = if (isMale!!) story.storyMale else story.storyFemale

            var text = dialogs?.get(currentDialogIndex)

            if (viewModel.username.value != null){
                text = text?.replace("{username}", viewModel.username.value!!)
            }
            //Setting text with checking gender type
            dialogText.text = text

            if(story.description != null){
                findViewById<FrameLayout>(R.id.description_frame).visibility = View.VISIBLE
                val textView = findViewById<TextView>(R.id.description_text)
                val descriptionCard = findViewById<MaterialCardView>(R.id.description_card)
                textView.text = story.descriptionPreview
                descriptionCard.setOnClickListener { if(descriptionFlag){
                    textView.text = story.description
                }else{
                    textView.text = story.descriptionPreview
                }
                    descriptionFlag = !descriptionFlag
                }
            }else{
                findViewById<FrameLayout>(R.id.description_frame).visibility = View.INVISIBLE
            }

            val actionData = if (isMale) story.actionDataMale else story.actionDataFemale

            if (currentDialogIndex < dialogs?.size!!-1){
                setDialogActionMode(0)
                val button = findViewById<Button>(R.id.button)
                button.text = "Далее"
                button.setOnClickListener { viewModel.currentDialogIndex.value = currentDialogIndex+1 }
                return
            }

            //Setting answer action components with checking
            if (story.actionType == ACTION_TYPE_BUTTON){
                if (story.actionDataMale?.size!! <= 4){
                    setDialogActionMode(1)
                    val button1 = findViewById<Button>(R.id.choose_button_1)
                    val button2 = findViewById<Button>(R.id.choose_button_2)
                    button1.text = actionData?.get(0)
                    button2.text = actionData?.get(2)
                    if (actionData?.get(1)!!.contains("http")){
                        button1.setOnClickListener {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ru.vtb.invest")))
                            } catch (e: ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(actionData?.get(1)!!)))
                            }
                        }
                        button2.setOnClickListener {
                            val url = actionData?.get(3)!!
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        }
                    }else {
                        button1.setOnClickListener { changeStory(Integer.parseInt(actionData?.get(1)!!)) }
                        button2.setOnClickListener { changeStory(Integer.parseInt(actionData?.get(3)!!)) }
                    }
                }else{
                    setDialogActionMode(2)
                    val button1 = findViewById<Button>(R.id.choose_button_1)
                    val button2 = findViewById<Button>(R.id.choose_button_2)
                    val button3 = findViewById<Button>(R.id.choose_button_3)
                    button1.text = actionData?.get(0)
                    button2.text = actionData?.get(2)
                    button3.text = actionData?.get(4)
                    button1.setOnClickListener { changeStory(Integer.parseInt(actionData?.get(1)!!)) }
                    button2.setOnClickListener { changeStory(Integer.parseInt(actionData?.get(3)!!)) }
                    button3.setOnClickListener { changeStory(Integer.parseInt(actionData?.get(5)!!)) }

                }
            }else if (story.actionType == ACTION_TYPE_EDITTEXT){
                setDialogActionMode(3)
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
                    if (actionData.size > 2) {
                        changeStory(Integer.parseInt(actionData.get(2)))
                    }else{
                        changeStory(Integer.parseInt(story.next!!))
                    }
                }


            } else if(story.actionType == ACTION_TYPE_GENDER){
                setDialogActionMode(1)
                val button1 = findViewById<Button>(R.id.choose_button_1)
                val button2 = findViewById<Button>(R.id.choose_button_2)
                button1.text = actionData?.get(0)
                button2.text = actionData?.get(1)
                button1.setOnClickListener {
                    viewModel.isMale.value = true
                    changeStory(Integer.parseInt(story.next!!))
                }
                button2.setOnClickListener {
                    viewModel.isMale.value = false
                    changeStory(Integer.parseInt(story.next!!))
                }
            }else{
                setDialogActionMode(0)
                val button = findViewById<Button>(R.id.button)
                button.text = "Далее"
                button.setOnClickListener { changeStory(Integer.parseInt(story.next!!)) }
                return
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
            containerView = layoutInflater.inflate(R.layout.answer_one_button, parent, false)
        }else if (actionTypeCode == 1){
            containerView = layoutInflater.inflate(R.layout.answer_duo_button, parent, false)
        }else if (actionTypeCode == 2){
            containerView = layoutInflater.inflate(R.layout.answer_trio_button, parent, false)
        }else if (actionTypeCode == 3){
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