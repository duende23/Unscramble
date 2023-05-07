package com.villadevs.unscramble

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.villadevs.unscramble.databinding.FragmentGameBinding
import com.villadevs.unscramble.viewmodel.GameViewModel


class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    //private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            param2 = it.getString(ARG_PARAM2)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentScrambledWord.observe(viewLifecycleOwner) { newWord ->
            binding.tvUnscrambledWord.text = newWord
        }

        viewModel.score.observe(viewLifecycleOwner) { newScore ->
            binding.tvScore.text = getString(R.string.score, newScore)
        }

        viewModel.currentWordCount.observe(viewLifecycleOwner) { newCurrentWordCount ->
            binding.tvWordCount.text =
                getString(R.string.word_count, newCurrentWordCount, MAX_NO_OF_WORDS)
        }

        // Setup a click listener for the Submit and Skip buttons.
        binding.btSubmit.setOnClickListener { onSubmitWord() }
        binding.btSkip.setOnClickListener { onSkipWord() }

    }


    /*
        * Checks the user's word, and updates the score accordingly. Displays the next scrambled word.
        * After the last word, the user is shown a Dialog with the final score.
        */
    private fun onSubmitWord() {
        val playerWord = binding.etEnterWord.text.toString()
        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    /*
       * Skips the current word without changing the score.
       * Increases the word count.
       */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    /*
  * Sets and resets the text field error status.
  */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.tiLayoutEnterWord.isErrorEnabled = true
            // Set error text
            binding.tiLayoutEnterWord.error = getString(R.string.try_again)
        } else {
            binding.tiLayoutEnterWord.isErrorEnabled = false
            // Clear error text
            binding.etEnterWord.text = null
        }
    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Creates and shows an AlertDialog with the final score.
    */
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}