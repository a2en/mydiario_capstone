package io.github.a2en.mydiario.ui.signup.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.databinding.FragmentSignupBinding
import io.github.a2en.mydiario.ui.home.MainActivity
import io.github.a2en.mydiario.utils.Constants.Companion.AUTH_TOKEN
import io.github.a2en.mydiario.utils.Constants.Companion.IS_LOGGED_IN


class SignupFragment : Fragment() {


    private val viewModel: SignupViewModel by lazy {
        ViewModelProvider(this)[SignupViewModel::class.java]
    }

    private  lateinit var  binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showToast.observe(this, {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })

        viewModel.loading.observe(this,{
            if(it){
                binding.signupButton.visibility = View.INVISIBLE
                binding.progressCircular.visibility = View.VISIBLE
            }else{
                binding.signupButton.visibility = View.VISIBLE
                binding.progressCircular.visibility = View.INVISIBLE
            }
        })

        viewModel.saveAuthToken.observe(this,{
            val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE) ?: return@observe
            with (sharedPref.edit()) {
                putString(AUTH_TOKEN, it)
                putBoolean(IS_LOGGED_IN, true)
                apply()
            }
        })

        viewModel.navigate.observe(this, {
            view.findNavController().navigate(it)
            requireActivity().finish()
        })
    }



}