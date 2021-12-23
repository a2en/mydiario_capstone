package io.github.a2en.mydiario.ui.home.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.databinding.FragmentHomeBinding
import io.github.a2en.mydiario.ui.home.detail.makeButtonTextWhite
import io.github.a2en.mydiario.ui.signup.welcome.WelcomeActivity
import io.github.a2en.mydiario.utils.Constants


class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HomeAdapter({
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(it))
        },{
            viewModel.deleteEntry(it.id)
        })


        binding.fab.setOnClickListener {
            view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(null))
        }

        binding.logout.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setMessage("Do you want to logout?")
                .setPositiveButton("YES") { dialog, which ->
                    val sharedPref = requireActivity().getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                    with (sharedPref.edit()) {
                        clear()
                        apply()
                    }
                    startActivity(Intent(requireContext(),WelcomeActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("NO") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
            dialog.makeButtonTextWhite()
        }

        binding.recyclerView.adapter = adapter

        viewModel.diaryEntryLiveData.observe(viewLifecycleOwner,{
            adapter.submitList(it)
        })
    }

}