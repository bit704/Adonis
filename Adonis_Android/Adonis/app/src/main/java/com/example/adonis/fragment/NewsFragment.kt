package com.example.adonis.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adonis.R
import com.example.adonis.activity.SingleChatActivity
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
class NewsFragment : Fragment(), OnTouchListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val flingMinDistance: Int = 280
    private lateinit var gestureDetector: GestureDetector
    private var  leftSwipeListener: OnLeftSwipeListener? = null


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
        val newsAdapter = NewsListAdapter()
        newsAdapter.setItemClickListener(object :OnItemClickListener{
            override fun onItemClick() {
                val intent = Intent(this@NewsFragment.context, SingleChatActivity::class.java)
                startActivity(intent)
            }

            override fun onItemLongClick() {

            }
        })
        recyclerView.adapter = newsAdapter

        moreButton.setOnClickListener{
            val popupMenu: PopupMenu = PopupMenu(this.context, moreButton)
            popupMenu.menuInflater.inflate(R.menu.menu_main_more, popupMenu.menu)
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

    inner class NewsListAdapter: RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {

        var onItemClickListener: OnItemClickListener? = null
        inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            val avatar:ImageView = view.findViewById(R.id.news_avatar)
            val name:TextView = view.findViewById(R.id.news_name)
            val message:TextView = view.findViewById(R.id.news_message)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val newsView = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_news, null)
            return ViewHolder(newsView)
        }

        override fun getItemCount(): Int {
            return 14
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.setOnClickListener {
                if (onItemClickListener != null){
                    onItemClickListener?.onItemClick()
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(position)
        }

        fun setItemClickListener(onItemClickListener: OnItemClickListener){
            this.onItemClickListener = onItemClickListener
        }


    }
    interface OnItemClickListener{
        fun onItemClick()
        fun onItemLongClick()
    }

    inner class OnGesture: GestureDetector.SimpleOnGestureListener(){
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val x = e1.x - e2.x
            Log.i("distance", x.toString())
            if (x < 0 && abs(x) > flingMinDistance){
                leftSwipeListener?.onLeftSwipe()
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return if (p1 != null)
            gestureDetector.onTouchEvent(p1)
        else
            false
    }

    open interface OnLeftSwipeListener {
        fun onLeftSwipe()
    }

    fun setLeftSwipeListener(listener: OnLeftSwipeListener){
        this.leftSwipeListener = listener
    }
}