package io.github.a2en.mydiario.ui.signup.welcome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {


    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentWelcomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupButton.setOnClickListener {
            view.findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignupFragment())
        }

        binding.loginButton.setOnClickListener {
            view.findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        }
    }

}