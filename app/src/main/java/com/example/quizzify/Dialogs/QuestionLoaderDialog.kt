package com.example.quizzify.Dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.quizzify.R
import com.example.quizzify.utils.QuizzifyLoader

class QuestionLoaderDialog : DialogFragment() {

    private lateinit var loaderMessage: TextView
    private lateinit var loaderSubtext: TextView
    private lateinit var loaderAnimation: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        dialog?.setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.quizzify_loader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loaderMessage = view.findViewById(R.id.tvLoaderMessage)
        loaderSubtext = view.findViewById(R.id.tvLoaderSubtext)
        loaderAnimation = view.findViewById(R.id.ivLoaderAnimation)

        // Start animation if it's an AnimatedVectorDrawable
        (loaderAnimation.drawable as? AnimatedVectorDrawable)?.start()
    }

    fun setMessage(message: String): QuestionLoaderDialog {
        loaderMessage.text = message
        return this
    }

    fun setSubtext(subtext: String): QuestionLoaderDialog {
        loaderSubtext.text = subtext
        return this
    }

    fun dismissLoader() {
        if (dialog?.isShowing == true) {
            dismiss()
        }
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult("dialogDismissed", Bundle())
    }
}

