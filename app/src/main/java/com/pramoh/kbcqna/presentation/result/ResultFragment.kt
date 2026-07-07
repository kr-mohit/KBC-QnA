package com.pramoh.kbcqna.presentation.result

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentResultBinding
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.presentation.home.HomeViewModel
import com.pramoh.kbcqna.presentation.questionnaire.QuestionViewModel
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ResultFragment : BaseFragment() {

    private lateinit var binding: FragmentResultBinding
    private val resultViewModel: ResultViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(ResultFragmentDirections.actionResultFragmentToHomeFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        setUI()
        setOnClickListeners()
        setupObservers()
        promptSaveToLeaderboard()
        playMusic(MusicToPlay.RESULT_SCREEN)
    }

    private fun setUI() {
        val name = try {
            homeViewModel.getCurrentPlayerName()
        } catch (e: Exception) {
            ""
        }
        binding.tvPlayerName.text = name
        binding.tvPrizeAmount.text = MoneyTypeConversionUtil.convertToString(args.prizeMoney)
        if (args.didUserWin) {
            binding.tvBetterLuck.hide()
            binding.tvNextTime.hide()
        } else {
            binding.tvBetterLuck.show()
            binding.tvNextTime.show()
        }
    }

    private fun setOnClickListeners() {
        binding.btnShareScore.setOnClickListenerWithSfxAudio {
            shareScore()
        }

        binding.btnStartAgain.setOnClickListenerWithSfxAudio {
            findNavController().navigate(ResultFragmentDirections.actionResultFragmentToHomeFragment())
        }

        binding.btnHome.setOnClickListenerWithSfxAudio {
            findNavController().navigate(ResultFragmentDirections.actionResultFragmentToHomeFragment())
        }
    }

    private fun shareScore() {
        val rootLayout = binding.root
        // Create screenshot bitmap of the result fragment layout
        val bitmap = createBitmap(rootLayout.width, rootLayout.height)
        val canvas = Canvas(bitmap)
        rootLayout.draw(canvas)

        try {
            // 1. Try to save image to public gallery using MediaStore (so it's downloaded to device)
            var contentUri = saveImageToGallery(bitmap)

            // 2. Fallback to cache directory if MediaStore failed (e.g. older Android versions without write permissions)
            if (contentUri == null) {
                val cachePath = File(requireContext().cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "result_score.png")
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()
                contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.pramoh.kbcqna.fileprovider",
                    file
                )
            }

            if (contentUri != null) {
                val amountStr = MoneyTypeConversionUtil.convertToString(args.prizeMoney)
                val shareText =
                    "I won $amountStr in the KBC Q&A game! Can you beat my score? Play this game here: https://play.google.com/store/apps/details?id=${requireContext().packageName}"

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_SUBJECT, "My KBC Score")
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Score"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap): android.net.Uri? {
        return try {
            val resolver = requireContext().contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(
                    android.provider.MediaStore.MediaColumns.DISPLAY_NAME,
                    "kbc_score_${System.currentTimeMillis()}.png"
                )
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(
                        android.provider.MediaStore.MediaColumns.RELATIVE_PATH,
                        android.os.Environment.DIRECTORY_PICTURES + "/KBC_Scores"
                    )
                    put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            if (imageUri != null) {
                val stream = resolver.openOutputStream(imageUri)
                if (stream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.close()
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
                Toast.makeText(
                    context,
                    "Screenshot saved to Gallery",
                    Toast.LENGTH_SHORT
                ).show()
            }
            imageUri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveData() {
        if (questionViewModel.isOnlineGame()) {
            addPlayerToLeaderboard(homeViewModel.getCurrentPlayerName(), args.prizeMoney)
        }
    }

    private fun addPlayerToLeaderboard(playerName: String, moneyWon: Int) {
        resultViewModel.insertPlayer(PlayerData(0, playerName, moneyWon))
    }

    private var nameToCheck = ""

    private fun setupObservers() {
        homeViewModel.playerNameCheckResult.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.progressLoading.visibility = View.VISIBLE
                    binding.tvLoadingText.text = getString(R.string.checking_username)
                    binding.tvLoadingText.visibility = View.VISIBLE
                    disableAllButtonsClick()
                }

                is Response.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    binding.tvLoadingText.visibility = View.GONE
                    enableAllButtonsClick()
                    val exists = response.data ?: false
                    if (exists) {
                        Toast.makeText(
                            context,
                            "Name already exists. Please choose a different name.",
                            Toast.LENGTH_SHORT
                        ).show()
                        homeViewModel.resetPlayerNameCheck()
                        showLeaderboardSubmissionDialog(nameToCheck)
                    } else {
                        homeViewModel.resetPlayerNameCheck()
                        homeViewModel.setCurrentPlayerName(nameToCheck)
                        homeViewModel.setPlayerNameSharedPref(nameToCheck)
                        binding.tvPlayerName.text = nameToCheck
                        saveData()
                        Toast.makeText(context, "Score submitted successfully!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                is Response.Error -> {
                    binding.progressLoading.visibility = View.GONE
                    binding.tvLoadingText.visibility = View.GONE
                    enableAllButtonsClick()
                    Toast.makeText(
                        context,
                        response.error ?: "Failed to verify username",
                        Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.resetPlayerNameCheck()
                }
            }
        }
    }

    private fun showLeaderboardSubmissionDialog(prefilledName: String = "") {
        val context = requireContext()
        val dialog = AlertDialog.Builder(context).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_leaderboard_submit, null)

        val input = dialogView.findViewById<EditText>(R.id.et_leaderboard_name)
        val submitButton = dialogView.findViewById<Button>(R.id.btn_popup_window_button_1)
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_popup_window_button_2)

        val savedName = prefilledName.ifEmpty {
            homeViewModel.playerNameSharedPref.value ?: ""
        }
        input.setText(savedName)
        input.setSelection(savedName.length)

        submitButton.setOnClickListener {
            playSfxAudio()
            val enteredName = input.text.toString().trim()
            if (enteredName.isEmpty()) {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                nameToCheck = enteredName
                homeViewModel.checkPlayerNameExists(enteredName)
            }
        }

        cancelButton.setOnClickListener {
            playSfxAudio()
            dialog.dismiss()
        }

        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.setCancelable(false)
        dialog.show()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            dialog.window?.insetsController?.apply {
                hide(android.view.WindowInsets.Type.systemBars())
                systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            dialog.window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }

    private fun promptSaveToLeaderboard() {
        if (questionViewModel.isOnlineGame() && args.prizeMoney > 0) {
            showLeaderboardSubmissionDialog()
        }
    }

    private fun disableAllButtonsClick() {
        binding.btnShareScore.isClickable = false
        binding.btnStartAgain.isClickable = false
        binding.btnHome.isClickable = false
    }

    private fun enableAllButtonsClick() {
        binding.btnShareScore.isClickable = true
        binding.btnStartAgain.isClickable = true
        binding.btnHome.isClickable = true
    }
}