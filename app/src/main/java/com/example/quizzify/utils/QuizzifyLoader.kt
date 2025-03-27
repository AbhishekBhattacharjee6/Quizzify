package com.example.quizzify.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.example.quizzify.R

class QuizzifyLoader(context: Context) {
    val dialog: Dialog = Dialog(context)
    private var loaderMessage: TextView
    private var loaderSubtext: TextView
    private var loaderAnimation: ImageView

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val view = LayoutInflater.from(context).inflate(R.layout.quizzify_loader, null)
        dialog.setContentView(view)

        loaderMessage = view.findViewById(R.id.tvLoaderMessage)
        loaderSubtext = view.findViewById(R.id.tvLoaderSubtext)
        loaderAnimation = view.findViewById(R.id.ivLoaderAnimation)
    }

    fun setMessage(message: String): QuizzifyLoader {
        loaderMessage.text = message
        return this
    }

    fun setSubtext(subtext: String): QuizzifyLoader {
        loaderSubtext.text = subtext
        return this
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
            (loaderAnimation.drawable as? AnimatedVectorDrawable)?.start()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }
}