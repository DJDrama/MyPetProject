package com.mypet.www.ui.launcher.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mypet.www.R
import com.mypet.www.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        //getting activity context as AppcompatActivity in order to access
        //supportActionBar
        val act = requireActivity() as AppCompatActivity
        //Hide ActionBar
        act.supportActionBar?.hide()
        //Set show hide animation to false
        act.supportActionBar?.setShowHideAnimationEnabled(false)
        button_login.setOnClickListener(this)
        button_register.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.button_login -> {
                    login()
                }
                R.id.button_register -> {
                    navigateToRegisterFragment(it)
                }
            }
        }
    }

    private fun login() {
        val email = edt_email.text.toString()
        val password = edt_password.text.toString()
        if(email.isEmpty() || email.isBlank()){
            Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(password.isEmpty() || password.isBlank()){
            Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Log.w("LoginFragment", "signInWithEmail:failure", task.exception);
                    when(task.exception){
                        is FirebaseAuthInvalidUserException ->{
                            Toast.makeText(requireContext(), "일치하는 회원이 없습니다.",
                                Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidCredentialsException ->{
                            Toast.makeText(requireContext(), "이메일 비밀번호를 확인해주세요.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun navigateToRegisterFragment(view: View) {
        view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
}