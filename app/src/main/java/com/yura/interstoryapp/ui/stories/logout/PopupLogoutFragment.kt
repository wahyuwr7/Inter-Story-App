package com.yura.interstoryapp.ui.stories.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.yura.interstoryapp.data.Utils.dataStore
import com.yura.interstoryapp.data.local.prefs.UserPrefs
import com.yura.interstoryapp.databinding.FragmentPopupLogoutBinding
import com.yura.interstoryapp.ui.splash.EnterAppActivity
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
            viewModel.getUsername().observe(viewLifecycleOwner){
                tvFirstUserChar.text = it[0].toString()
                tvPopupUsername.text = it
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val popupHeight = 1000
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            popupHeight
        )
    }

    private fun setupClickListeners(viewModel: PopupLogoutViewModel) {
        binding.btnLogout.setOnClickListener {
            viewModel.deleteUserPrefs()
            startActivity(Intent(requireContext(), EnterAppActivity::class.java))
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "PopupLogout"
    }
}