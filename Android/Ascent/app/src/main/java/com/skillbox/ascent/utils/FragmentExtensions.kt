package com.skillbox.ascent.utils


import android.app.Dialog
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.button.MaterialButton
import com.skillbox.ascent.R
import com.skillbox.ascent.di.networking.NetworkState
import com.skillbox.ascent.di.networking.NetworkStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import permissions.dispatcher.PermissionRequest


@OptIn(ExperimentalCoroutinesApi::class)
fun ViewModel.setupNetworkStateMonitoring(
    networkState: NetworkState,
    isConnectedMutableLiveData: MutableLiveData<NetworkStatus>
): Job {
    return viewModelScope.launch {
        networkState.changes(isConnectedMutableLiveData)
    }
}





fun Fragment.showDialog(
    affirmativeAction: () -> Unit,
    negativeAction: () -> Unit?,
    @StringRes rationaleTxt: Int,
) {
    val dialog = Dialog(requireActivity())


    with(dialog) {
        setContentView(R.layout.layout_dialog)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setCancelable(false)

        window?.attributes?.windowAnimations = R.style.animation

        val positiveBtn = this.findViewById<MaterialButton>(R.id.positiveBtn)
        val negativeBtn = this.findViewById<MaterialButton>(R.id.negativeBtn)
        val exitDialogText = this.findViewById<TextView>(R.id.rationaleTxt)

        exitDialogText.setText(rationaleTxt)

        negativeBtn.setOnClickListener {
            negativeAction()
            this.dismiss()
        }

        positiveBtn.setOnClickListener {
            affirmativeAction()
            this.dismiss()
        }

        this.show()
    }



}

fun Fragment.showRationaleDialog(@StringRes ratio: Int, request: PermissionRequest) {
    this.showDialog(
        rationaleTxt = ratio,
        affirmativeAction = { request.proceed() },
        negativeAction = { request.cancel() }
    )
}









