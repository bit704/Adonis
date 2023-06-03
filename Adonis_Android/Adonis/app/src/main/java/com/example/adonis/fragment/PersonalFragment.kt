package com.example.adonis.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.adonis.R
import com.example.adonis.activity.MainActivity
import com.example.adonis.application.AdonisApplication
import com.example.adonis.entity.FriendInfoMessage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PersonalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var data: AdonisApplication
    private var myInfo: FriendInfoMessage? = null
    private var idView: TextView? = null
    private var nameView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        data = activity?.application as AdonisApplication

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personal, container, false)
        val exitButton: Button = view.findViewById(R.id.button_personal_exit)
        idView = view.findViewById(R.id.personal_id)
        nameView = view.findViewById(R.id.personal_nickname)

        exitButton.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.sendExitMessage()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PersonalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PersonalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun initInfo() {
        myInfo = data.getMyInfo()
        idView?.text = myInfo?.id
        nameView?.text = myInfo?.nickname
    }


}