package com.example.adonis.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.adonis.R
import com.example.adonis.activity.AddActivity
import com.example.adonis.activity.NewFriendsActivity
import com.example.adonis.entity.FilterString
import com.example.adonis.entity.FriendInfoMessage
import com.example.adonis.entity.MessageCode
import com.example.adonis.utils.ContactsAdapter
import java.io.Serializable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val adapter = ContactsAdapter()
    private val contacts = mutableListOf<FriendInfoMessage>()
    private val newFriends = mutableListOf<FriendInfoMessage>()

    private var newNum: TextView? = null

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        val moreButton = view.findViewById<Button>(R.id.button_contacts_more)
        newNum = view.findViewById(R.id.text_contacts_new_num)
        val itemNew: LinearLayout = view.findViewById(R.id.item_contacts_new)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_contacts)
        val layout = LinearLayoutManager(recyclerView.context)
        layout.orientation = LinearLayoutManager.VERTICAL

        recyclerView.layoutManager = layout
        recyclerView.adapter = adapter

        itemNew.setOnClickListener {
            val intent = Intent(activity, NewFriendsActivity::class.java)
            val jsonData = JSON.toJSONString(newFriends)
            intent.putExtra(FilterString.DATA, jsonData)
            startActivity(intent)
        }

        moreButton.setOnClickListener {
            val popupMenu = PopupMenu(this.context, moreButton)
            popupMenu.menuInflater.inflate(R.menu.menu_main_more, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.item_more_add -> {
                        val intent = Intent(activity, AddActivity::class.java)
                        startActivity(intent)
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        return view
    }

    fun initContacts(info: List<FriendInfoMessage>) {
        for (item in info) {
            if (item.code == MessageCode.fif_already_add.id) {
                contacts.add(item)
            }
            else {
                newFriends.add(item)
            }
        }
        newNum?.text = newFriends.size.toString()
        adapter.initContacts(contacts)
        adapter.notifyDataSetChanged()
    }

    fun addContacts(user: FriendInfoMessage) {
        adapter.addContacts(user)
        adapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}