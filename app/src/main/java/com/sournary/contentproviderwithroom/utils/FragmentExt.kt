package com.sournary.contentproviderwithroom.utils

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Created: 30/08/2018
 * By: Sang
 * Description:
 */
fun Fragment.setupSupportActionBar(toolbar: Toolbar, action: ActionBar.() -> Unit) {
    with(activity as AppCompatActivity) {
        setSupportActionBar(toolbar)
        supportActionBar!!.action()
    }
}

fun Fragment.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    @StringRes actionLabel: Int = android.R.string.ok,
    actionListener: ((View) -> Unit) = {}
) {
    view?.let {
        Snackbar.make(it, message, duration).apply { setAction(actionLabel, actionListener).show() }
    }
}
