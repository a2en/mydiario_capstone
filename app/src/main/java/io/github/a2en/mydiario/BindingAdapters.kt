package io.github.a2en.mydiario

import android.opengl.Visibility
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visibility")
fun bindViewVisibility(view: View, visibility: Boolean) {
    view.visibility = if (visibility) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}