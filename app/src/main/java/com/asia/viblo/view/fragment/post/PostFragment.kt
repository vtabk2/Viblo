package com.asia.viblo.view.fragment.post

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asia.viblo.R
import com.asia.viblo.model.extraUrl
import com.asia.viblo.model.keyMaxPage
import com.asia.viblo.model.keyPagePresent
import com.asia.viblo.model.post.Post
import com.asia.viblo.utils.SharedPrefs
import com.asia.viblo.view.activity.detail.PostDetailActivity
import com.asia.viblo.view.activity.home.OnClickPostDetail
import com.asia.viblo.view.adapter.PostAdapter
import com.asia.viblo.view.asyncTask.*
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.include_layout_next_back_page.*

class PostFragment : Fragment(), OnClickPostDetail {
    private val mPostList: MutableList<Post> = arrayListOf()
    private lateinit var mPostAdapter: PostAdapter
    private var mPosition: Int = 0
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerPost()
        updateViewNextBackBottom()
        initListener()
    }

    private fun updateViewNextBackBottom() {
        val pagePresentStr = SharedPrefs.instance[keyPagePresent, String::class.java]
        val pageMaxStr = SharedPrefs.instance[keyMaxPage, String::class.java]
        textPageBack.visibility = if (TextUtils.equals("1", pagePresentStr)) View.GONE else View.VISIBLE
        textPageNext.visibility = if (TextUtils.equals(pageMaxStr, pagePresentStr)) View.GONE else View.VISIBLE
        textPagePresent.text = getPagePresent(pagePresentStr, pageMaxStr)
    }

    private fun getPagePresent(pagePresentStr: String, pageMaxStr: String): String {
        return pagePresentStr + "/" + pageMaxStr
    }

    private fun initRecyclerPost() {
        mPostAdapter = PostAdapter(context, mPostList, this)
        recyclerPost.adapter = mPostAdapter
        recyclerPost.layoutManager = LinearLayoutManager(context)
        loadData(baseUrlNewest, "")
    }

    private fun loadData(url: String, page: String) {
        mPostList.clear()
        if (TextUtils.isEmpty(page)) {
            mPostList.addAll(LoadPostAsyncTask().execute(url).get())
        } else {
            mPostList.addAll(LoadPostAsyncTask().execute(url, page).get())
        }
        mPostAdapter.notifyDataSetChanged()
    }

    private fun getLink(type: Int): String {
        return when (type) {
            0 -> baseUrlNewest
            1 -> baseUrlSeries
            2 -> baseUrlEditorsChoice
            3 -> baseUrlTrending
            4 -> baseUrlVideos
            else -> baseUrlNewest
        }
    }

    private fun initListener() {
        textPageNext.setOnClickListener {
            val pageNext = SharedPrefs.instance[keyPagePresent, String::class.java].toInt() + 1
            loadData(getLink(mPosition), pageNext.toString())
            updateViewNextBackBottom()
        }
        textPageBack.setOnClickListener {
            val pageBack = SharedPrefs.instance[keyPagePresent, String::class.java].toInt() - 1
            loadData(getLink(mPosition), pageBack.toString())
            updateViewNextBackBottom()
        }
        textPagePresent.setOnClickListener {
            loadData(getLink(mPosition), SharedPrefs.instance[keyPagePresent, String::class.java])
            updateViewNextBackBottom()
        }
    }

    override fun onClickPostDetail(url: String) {
        val intent = Intent(context, PostDetailActivity::class.java)
        intent.putExtra(extraUrl, url)
        startActivity(intent)
    }
}