package io.github.a2en.mydiario.ui.signup.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.databinding.FragmentLoginBinding
import io.github.a2en.mydiario.ui.home.MainActivity
import io.github.a2en.mydiario.ui.signup.signup.SignupViewModel
import io.github.a2en.mydiario.utils.Constants


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
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
                binding.loginButton.visibility = View.INVISIBLE
                binding.progressCircular.visibility = View.VISIBLE
            }else{
                binding.loginButton.visibility = View.VISIBLE
                binding.progressCircular.visibility = View.INVISIBLE
            }
        })
        viewModel.saveAuthToken.observe(this,{
            val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE) ?: return@observe
            with (sharedPref.edit()) {
                putString(Constants.AUTH_TOKEN, it)
                putBoolean(Constants.IS_LOGGED_IN, true)
                apply()
            }
        })
        viewModel.navigate.observe(this, {
            view.findNavController().navigate(it)
            requireActivity().finish()
        })
    }


}