package com.example.p4


import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.NonCancellable.start
import kotlin.random.Random

/**
 * The activity where users answer the questions from the previous screen
 */
class NextActivity : AppCompatActivity() {
    /** TextView that displays the question */
    private lateinit var questionText: TextView

    /** TextView where users enter their answer. */
    private lateinit var answerText: TextView

    /** Button that users press when they've answered a question */
    private lateinit var doneButton: Button

    /** Tracks the current question number */
    private var currentQuestionNumber = 0

    /** Tracks the number of correctly answered questions */
    private var correctAnswers = 0

    /** The total number of questions to be answered */
    private var numQuestions = 0

    /** The selected difficulty level */
    private lateinit var selectedDifficulty: String

    /** The selected operation type */
    private lateinit var selectedOperation: String


    /**
     * Initializes the activity and sets up the UI components for the questioning process
     *
     * @param savedInstanceState The saved state of the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        questionText = findViewById(R.id.questionText)
        answerText = findViewById(R.id.answerText)
        doneButton = findViewById(R.id.doneButton)

        numQuestions = intent.getIntExtra("numQuestions", 5)

        selectedDifficulty = intent.getStringExtra("difficulty") ?: "easy"
        selectedOperation = intent.getStringExtra("operation") ?: "+"

        doneButton.setOnClickListener {
            checkAnswer()
            if (currentQuestionNumber < numQuestions) {
                showQuestion()
            } else {
                showResults()
            }
        }
        showQuestion()
    }

    /**
     * Displays a new question for the user to answer
     */
    private fun showQuestion() {
        val question = randomQuestion()
        questionText.text = question
        answerText.text = Editable.Factory.getInstance().newEditable("")
    }


    /**
     * Generates a random even number within a specified range (i chose this for the division because
     * it was giving me only odd numbers and it was making the division annoying for the user
     *
     * @param range The range within which to generate the even number
     * @return An even number within the specified range
     */
    private fun randomEvenNumber(range: IntRange): Int {
        var evenNumber = Random.nextInt(range.first, range.last + 1)
        if (evenNumber % 2 != 0) {
            evenNumber++
        }
        return evenNumber.coerceIn(range)
    }

    /**
     * Generates a random question based on selected difficulty and operation
     *
     * @return A string representing the arithmetic question
     */
    private fun randomQuestion(): String {
        val range = when (selectedDifficulty) {
            "Easy" -> 1..5
            "Medium" -> 6..10
            "Hard" -> 12..19
            else -> 1..5
        }

        var num1: Int
        var num2: Int
        val operator = when (selectedOperation) {
            "Addition" -> {
                num1 = Random.nextInt(range.first, range.last + 1)
                num2 = Random.nextInt(range.first, range.last + 1)
                "+"
            }
            "Subtraction" -> {
                num1 = Random.nextInt(range.first, range.last + 1)
                num2 = Random.nextInt(range.first, range.last + 1)
                if (num1 < num2) {
                    val temp = num1
                    num1 = num2
                    num2 = temp
                }
                "-"
            }
            "Multiplication" -> {
                num1 = Random.nextInt(range.first, range.last + 1)
                num2 = Random.nextInt(range.first, range.last + 1)
                "*"
            }
            "Division" -> {
                num1 = randomEvenNumber(range)
                num2 = randomEvenNumber(range)
                while (num2 == 0) {
                    num2 = randomEvenNumber(range)
                }
                "/"
            }
            else -> {
                num1 = Random.nextInt(range.first, range.last + 1)
                num2 = Random.nextInt(range.first, range.last + 1)
                "+"
            }
        }

        return "$num1 $operator $num2"
    }

    /**
     * Checks the user's answer against the correct answer and updates the score
     */
    private fun checkAnswer() {
        val enteredAnswer = answerText.text.toString().toFloatOrNull()
        val correctAnswerString = calculateCorrectAnswer(questionText.text.toString())
        val correctAnswer = correctAnswerString.toFloatOrNull()
        var isCorrect = false

        if (enteredAnswer != null && correctAnswer != null) {
            val epsilon = 0.0005 // to the hundredth place
            if (Math.abs(enteredAnswer - correctAnswer) < epsilon) {
                correctAnswers++
                isCorrect = true
            }
        }
        currentQuestionNumber++

        if (isCorrect) {
            Toast.makeText(this, "Correct. Good work!", Toast.LENGTH_SHORT).show()
            MediaPlayer.create(this, R.raw.correct_sound).apply {
                start()
                setOnCompletionListener { mp -> mp.release() }
            }
        } else {
            Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show()
            MediaPlayer.create(this, R.raw.wrong_sound).apply {
                start()
                setOnCompletionListener { mp -> mp.release() }
            }
        }
    }

    /**
     * Calculates the correct answer for the question
     *
     * @param question The question as a string
     * @return A string representing the correct answer
     */
    private fun calculateCorrectAnswer(question: String): String {
        val parts = question.split(" ")
        val num1 = parts[0].toFloat()
        val operator = parts[1]
        val num2 = parts[2].toFloat()

        return when (operator) {
            "+" -> (num1 + num2).toString()
            "-" -> (num1 - num2).toString()
            "*" -> (num1 * num2).toString()
            "/" -> {
                if (num2 != 0.0f) {
                    (num1 / num2).toString()
                } else {
                    "NaN"
                }
            }
            else -> ""
        }

    }

    /**
     * Shows the user's final score then shows the results activity
     */
    private fun showResults() {
        val data = Intent().apply {
            putExtra("correctAnswers", correctAnswers)
            putExtra("numQuestions", numQuestions)
            putExtra("selectedOperation", selectedOperation)
        }
        setResult(RESULT_OK, data)
        finish()
    }
}

