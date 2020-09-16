package com.mypet.www.ui.main.d_my

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mypet.www.R
import com.mypet.www.ui.launcher.LauncherActivity
import kotlinx.android.synthetic.main.fragment_my_page.*
import java.text.SimpleDateFormat
import java.util.*



class MyPageFragment : Fragment(R.layout.fragment_my_page), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        initView()
    }

    private fun initView() {
        tv_email_address.text = firebaseAuth.currentUser?.email
        tv_joined_date.text = SimpleDateFormat("yyyy/MM/dd hh:mm:ss a")
            .format(Date(firebaseAuth.currentUser?.metadata?.creationTimestamp!!))
        button_logout.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(
                this.requireContext()
            )
            builder.setTitle("로그아웃")
            builder.setMessage("정말 로그아웃 하시겠습니까?")
            builder.setPositiveButton(
                "확인"
            ) { d, _ ->
                firebaseAuth.signOut()
                d.dismiss()
                requireActivity().startActivity(Intent(requireActivity(), LauncherActivity::class.java))
                requireActivity().finishAffinity()
            }
            builder.setNegativeButton(
                "취소"
            ) { d, _ ->
            }
            builder.show()
        }

        // Buttons
        // My Pets
        iv_my_cats.setOnClickListener(this)
        tv_my_cats.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        p0?.let{
            when(it.id){
                R.id.iv_my_cats, R.id.tv_my_cats->{
                    it.findNavController().navigate(R.id.action_myPageFragment_to_myPetsFragment)
                }
            }
        }
    }
}