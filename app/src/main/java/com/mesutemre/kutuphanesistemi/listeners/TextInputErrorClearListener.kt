package com.mesutemre.kutuphanesistemi.listeners

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

class TextInputErrorClearListener(private val textInput: TextInputLayout): TextWatcher {

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        textInput.isErrorEnabled = false;
    }


}