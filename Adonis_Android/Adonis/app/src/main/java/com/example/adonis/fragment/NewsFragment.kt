package com.example.adonis.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adonis.R
import com.example.adonis.activity.AddActivity
import com.example.adonis.activity.SingleChatActivity
import com.example.adonis.entity.DialogueInfoMessage
import com.example.adonis.utils.NewsAdapter
import kotlin.math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val newsAdapter = NewsAdapter()


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
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_news)
        val moreButton = view.findViewById<Button>(R.id.button_news_more)

        val layout = LinearLayoutManager(view.context)
        layout.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layout

        newsAdapter.setItemClickListener(object : NewsAdapter.OnItemClickListener {
            override fun onItemClick() {
                val intent = Intent(activity, SingleChatActivity::class.java)
                startActivity(intent)
            }

            override fun onItemLongClick() {

            }
        })
        recyclerView.adapter = newsAdapter

        moreButton.setOnClickListener{
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

//        val onGesture = OnGesture()
//        gestureDetector = GestureDetector(recyclerView.context, onGesture)
//        recyclerView.setOnTouchListener(this)
//        recyclerView.isLongClickable = true

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    fun initNewsList(list: List<DialogueInfoMessage>) {
        newsAdapter.initNewsList(list)
        newsAdapter.notifyDataSetChanged()
    }
}