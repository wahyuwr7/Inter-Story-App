package com.yura.interstoryapp.ui.stories.popup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.R
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.FragmentPopupLogoutBinding
import com.yura.interstoryapp.ui.auth.login.LoginActivity
import com.yura.interstoryapp.ui.stories.maps.MapsActivity
import com.yura.interstoryapp.ui.viewmodel.VMFactory

class PopupLogoutFragment : DialogFragment() {

    private lateinit var binding: FragmentPopupLogoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPopupLogoutBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = UserPrefs.getInstance(requireContext().dataStore)
        val viewModel = ViewModelProvider(this, VMFactory(pref))[PopupLogoutViewModel::class.java]
        setupClickListeners(viewModel)
        setupView(viewModel)
    }

    private fun setupView(viewModel: PopupLogoutViewModel) {
        binding.apply {
            viewModel.getUsername().observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    tvFirstUserChar.text = it[0].toString()
                    tvPopupUsername.text = it
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = resources.displayMetrics.heightPixels / 1.5
        dialog?.window?.apply {
            setLayout(
                width,
                height.toInt()
            )
        }
    }

    private fun setupClickListeners(viewModel: PopupLogoutViewModel) {
        with(binding){
            btnLogout.setOnClickListener {
                ConfirmationDialogFragment(viewModel).show(
                    childFragmentManager, ConfirmationDialogFragment.TAG
                )
            }
            btnMaps.setOnClickListener {
                startActivity(Intent(requireContext(), MapsActivity::class.java))
                dismiss()
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "PopupLogout"
    }
}

class ConfirmationDialogFragment(viewModel: PopupLogoutViewModel) : DialogFragment(){
    private val vm = viewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.confirmation_logout))
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                vm.deleteUserPrefs()
                startActivity(
                    Intent(
                        requireContext(),
                        LoginActivity::class.java
                    )
                )
                activity?.finish()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            .create()

    companion object {
        const val TAG = "ConfirmationDialog"
    }
}