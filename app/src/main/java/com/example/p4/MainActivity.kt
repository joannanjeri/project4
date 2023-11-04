package com.example.p4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * The main activity class has everything that involves user input
 */

class MainActivity : AppCompatActivity() {

    // UI
    private lateinit var difficultyRadioGroup: RadioGroup
    private lateinit var operationRadioGroup: RadioGroup
    private lateinit var numQuestionsEditText: EditText
    private lateinit var resultText: TextView

    companion object {
        /**
         * request code for starting NextActivity
         */
        const val NEXT_ACTIVITY_REQUEST_CODE = 1
    }

    /**
     * initializes the activity
     *
     * @param savedInstanceState contains the most recent data from onSaveInstanceState(Bundle)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup)
        operationRadioGroup = findViewById(R.id.operationRadioGroup)
        numQuestionsEditText = findViewById(R.id.numQuestionsEditText)
        resultText = findViewById(R.id.resultText)
        val startButton: Button = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            val selectedDifficulty = getSelectedRadioButton(difficultyRadioGroup)
            val selectedOperation = getSelectedRadioButton(operationRadioGroup)
            val numQuestionsText = numQuestionsEditText.text.toString()
            val numQuestions = if (numQuestionsText.isNotEmpty()) numQuestionsText.toInt() else 0

            val nextActivityIntent = Intent(this, NextActivity::class.java).apply {
                putExtra("difficulty", selectedDifficulty)
                putExtra("operation", selectedOperation)
                putExtra("numQuestions", numQuestions)
            }
            startActivityForResult(nextActivityIntent, NEXT_ACTIVITY_REQUEST_CODE)

            val message = "Starting quiz: $numQuestions questions on $selectedOperation at $selectedDifficulty difficulty."
            showToast(message)
        }
    }

    /**
     * this returns the text of the selected RadioButton
     *
     * @param radioGroup the RadioGroup that has all the RadioButtons
     * @return this is the text of the selected RadioButton
     */
    private fun getSelectedRadioButton(radioGroup: RadioGroup): String {
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        return selectedRadioButton.text.toString()
    }

    /**
     * @param message this shows a toast message on the screen
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Increases the value in the number input field by 1 and it goes up to 10
     *
     * @param view Shows the button that triggered the method
     */

    fun incrementNumber(view: View) {
        val currentNumber = numQuestionsEditText.text.toString().toIntOrNull() ?: 0
        if (currentNumber < 10) {
            numQuestionsEditText.setText((currentNumber + 1).toString())
        }
    }

    /**
     * Decreases the value in the number input field by 1 and sets that as the min
     *
     * @param view Shows the button that triggered the method
     */

    fun decrementNumber(view: View) {
        val currentNumber = numQuestionsEditText.text.toString().toIntOrNull() ?: 0
        if (currentNumber > 1) {
            numQuestionsEditText.setText((currentNumber - 1).toString())
        }
    }

    /**
     * this processes the result from the NextActivity. It calculates the score percentage
     * and updates the UI with the result
     *
     * @param requestCode where the result came from
     * @param resultCode the integer result code returned by the child activity through its setResult()
     * @param data this returns the result data to the user
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEXT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val correctAnswers = data.getIntExtra("correctAnswers", 0)
            val numQuestions = data.getIntExtra("numQuestions", 0)

            val percentage = (correctAnswers.toDouble() / numQuestions.toDouble()) * 100

            resultText.visibility = View.VISIBLE

            if (percentage >= 80) {
                resultText.setTextColor(ContextCompat.getColor(this, R.color.green))
                resultText.text = "Congratulations! You got $correctAnswers out of $numQuestions correct!"
            } else {
                resultText.setTextColor(ContextCompat.getColor(this, R.color.red))
                resultText.text = "You got $correctAnswers out of $numQuestions correct. Try again, you need more practice!"
            }
        }

    }
}
