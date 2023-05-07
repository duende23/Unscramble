package com.villadevs.unscramble.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.villadevs.unscramble.MAX_NO_OF_WORDS
import com.villadevs.unscramble.SCORE_INCREASE
import com.villadevs.unscramble.allWordsList

class GameViewModel : ViewModel() {

    // List of words used in the game
    private var wordsList: MutableList<String> = mutableListOf()

    private lateinit var currentWord: String

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    private var _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    init {
        getNextWord()

    }

    private fun getNextWord() {
        //Get a random word from the allWordsList and assign it to currentWord
        currentWord = allWordsList.shuffled().random()
        //convert the currentWord string to an array of characters and assign it to a new val called tempWord
        val tempWord = currentWord.toCharArray()
        //shuffle characters in this array using the Kotlin method, shuffle()
        tempWord.shuffle()
        //Sometimes the shuffled order of characters is the same as the original word. Add the
        //following while loop around the call to shuffle, to continue the loop until the scrambled word is not the same as the original word
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }

        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            //_currentScrambledWord.value = tempWord.toString()
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }

    }

    /*
        * Returns true if the current word count is less than MAX_NO_OF_WORDS
        */
    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }


    //Returns true if the player word is correct.Increases the score accordingly.

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }


    /*
    * Increases the game score if the player's word is correct.
    */
    private fun increaseScore() {
        _score.value = _score.value?.plus(SCORE_INCREASE)
    }

    //Re-initializes the game data to restart the game.
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

}