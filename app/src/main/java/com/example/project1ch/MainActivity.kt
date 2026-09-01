package com.example.project1ch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import android.view.View
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class MainActivity : AppCompatActivity() {
    private var wordToGuess = ""
    private var guessCount = 0
    private var streak = 0
    private var currentTheme = "Common"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize game
        wordToGuess = FourLetterWordList.getRandomFourLetterWord()
        
        val etGuess = findViewById<EditText>(R.id.etGuess)
        val btnGuess = findViewById<Button>(R.id.btnGuess)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val targetWordTv = findViewById<TextView>(R.id.targetWord)
        val streakTv = findViewById<TextView>(R.id.streakLabel)
        val tvStar = findViewById<TextView>(R.id.tvStar)

        // Theme Buttons
        findViewById<Button>(R.id.btnCommon).setOnClickListener { switchTheme("Common") }
        findViewById<Button>(R.id.btnSports).setOnClickListener { switchTheme("Sports") }
        findViewById<Button>(R.id.btnSchool).setOnClickListener { switchTheme("School") }
        findViewById<Button>(R.id.btnMovies).setOnClickListener { switchTheme("Movies") }

        btnGuess.setOnClickListener {
            val guess = etGuess.text.toString().uppercase().trim()
            
            if (guess.length != 4) {
                Toast.makeText(this, "Please enter a 4-letter word", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!guess.all { it.isLetter() }) {
                Toast.makeText(this, "Please use alphabetical characters only", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Word Bank Validation
            val allWords = FourLetterWordList.getAllFourLetterWords(currentTheme)
            if (guess !in allWords) {
                Toast.makeText(this, "Not in $currentTheme list!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            guessCount++
            val result = checkGuess(guess)
            updateUI(guess, result)
            etGuess.text.clear()

            if (guess == wordToGuess || guessCount >= 3) {
                targetWordTv.text = "Target Word: $wordToGuess"
                targetWordTv.visibility = View.VISIBLE
                btnGuess.isEnabled = false
                etGuess.isEnabled = false
                btnReset.visibility = View.VISIBLE
                
                if (guess == wordToGuess) {
                    streak++
                    streakTv.text = "Streak: $streak"
                    tvStar.visibility = View.VISIBLE
                    Toast.makeText(this, "You Win!", Toast.LENGTH_LONG).show()
                } else {
                    streak = 0
                    streakTv.text = "Streak: $streak"
                    Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnReset.setOnClickListener {
            resetGame()
        }
    }

    private fun switchTheme(theme: String) {
        currentTheme = theme
        Toast.makeText(this, "Theme switched to $theme", Toast.LENGTH_SHORT).show()
        resetGame()
    }

    private fun resetGame() {
        guessCount = 0
        wordToGuess = FourLetterWordList.getRandomFourLetterWord(currentTheme)
        
        findViewById<TextView>(R.id.guess1Word).text = ""
        findViewById<TextView>(R.id.guess1Check).text = ""
        findViewById<TextView>(R.id.guess2Word).text = ""
        findViewById<TextView>(R.id.guess2Check).text = ""
        findViewById<TextView>(R.id.guess3Word).text = ""
        findViewById<TextView>(R.id.guess3Check).text = ""
        
        findViewById<TextView>(R.id.targetWord).visibility = View.GONE
        findViewById<TextView>(R.id.tvStar).visibility = View.GONE
        findViewById<Button>(R.id.btnGuess).isEnabled = true
        findViewById<EditText>(R.id.etGuess).isEnabled = true
        findViewById<Button>(R.id.btnReset).visibility = View.GONE
        findViewById<EditText>(R.id.etGuess).text.clear()
    }

    private fun checkGuess(guess: String): String {
        var result = ""
        for (i in 0..3) {
            if (guess[i] == wordToGuess[i]) {
                result += "O"
            } else if (guess[i] in wordToGuess) {
                result += "+"
            } else {
                result += "X"
            }
        }
        return result
    }

    private fun updateUI(guess: String, result: String) {
        val coloredWord = SpannableString(guess)
        for (i in 0..3) {
            val color = when (result[i]) {
                'O' -> Color.GREEN
                '+' -> Color.YELLOW
                else -> Color.GRAY
            }
            coloredWord.setSpan(ForegroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        when (guessCount) {
            1 -> {
                findViewById<TextView>(R.id.guess1Word).text = coloredWord
                findViewById<TextView>(R.id.guess1Check).text = result
            }
            2 -> {
                findViewById<TextView>(R.id.guess2Word).text = coloredWord
                findViewById<TextView>(R.id.guess2Check).text = result
            }
            3 -> {
                findViewById<TextView>(R.id.guess3Word).text = coloredWord
                findViewById<TextView>(R.id.guess3Check).text = result
            }
        }
    }
}

object FourLetterWordList {
    private val commonWords = "Area,Army,Baby,Back,Ball,Band"
    private val sportsWords = "BALL,GOAL,GOLF,TEAM,RACE,SURF"
    private val schoolWords = "BOOK,DESK,EXAM,MATH,READ,QUIZ"
    private val movieWords = "FILM,ROLE,CAST,STAR,PLOT,SHOT"

    fun getAllFourLetterWords(theme: String = "Common"): List<String> {
        val words = when (theme) {
            "Sports" -> sportsWords
            "School" -> schoolWords
            "Movies" -> movieWords
            else -> commonWords
        }
        return words.split(",").map { it.uppercase().trim() }
    }

    fun getRandomFourLetterWord(theme: String = "Common"): String {
        val allWords = getAllFourLetterWords(theme)
        val randomNumber = allWords.indices.random()
        return allWords[randomNumber].uppercase().trim()
    }
}
