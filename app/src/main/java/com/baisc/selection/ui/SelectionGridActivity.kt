package com.baisc.selection.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.baisc.selection.R
import com.baisc.selection.adapter.BaseSelectionRecyclerAdapter

/**
 * Created by basic on 2018/4/18.
 */
class SelectionGridActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grid_layout)
        var gridView = findViewById<RecyclerView>(R.id.grid)
        var layoutManager = GridLayoutManager(this, 4)
        var gridAdapter = SelectionGridAdapter(this)
        layoutManager.spanSizeLookup = LoopSize(gridAdapter)
        gridView.layoutManager = layoutManager

        gridView.adapter = gridAdapter
        gridAdapter.refreshData(initData())

    }

    private fun initData(): MutableList<Group> {
        var datas: MutableList<Group> = mutableListOf()

        for (groupIndex in 0..5) {
            val title = "我是第${groupIndex}组"
            var group = Group(title)
            for (child in 0 until (10 - groupIndex)) {
                var item = GridItem("第$child", R.mipmap.ic_launcher)
                group.add(item)
            }
            datas.add(group)
        }
        return datas
    }

    class LoopSize(adapter: SelectionGridAdapter) : GridLayoutManager.SpanSizeLookup() {
        private var mAdapter = adapter

        override fun getSpanSize(position: Int): Int {
            if (mAdapter.isSelectionHeaderForPosition(position)) {
                return 4
            }
            return 1
        }

    }

    class SelectionGridAdapter(context: Context) :
            BaseSelectionRecyclerAdapter<HeaderHolder,
                    ItemHolder, RecyclerView.ViewHolder>(context) {

        private val mDatas: MutableList<Group> = mutableListOf()


        fun refreshData(list: List<Group>?) {
            mDatas.clear()
            if (list !== null && !list.isEmpty()) {
                mDatas.addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getSelection(): Int {
            return mDatas.size
        }


        override fun getItemCountInSelection(selection: Int): Int {
            if (selection < mDatas.size) {
                return mDatas[selection].itemList.size
            }
            return 0
        }

        override fun onCreateSelectionHeaderHolder(parent: ViewGroup?, viewType: Int): HeaderHolder {
            var header = TextView(context)
            header.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            header.textSize = 30f
            header.setBackgroundColor(Color.GRAY)
            header.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100)
            return HeaderHolder(header)
        }

        override fun onCreateSelectionItemHolder(parent: ViewGroup?, viewType: Int): ItemHolder {

            var root = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false)
            return ItemHolder(root)
        }

        override fun onBindSelectionHeaderHolder(holder: HeaderHolder?, viewType: Int, selection: Int) {
            if (holder === null) {
                return
            }
            var data = mDatas[selection]
            holder.selectionTitle.text = (data.selectionTitle)
        }

        override fun onBindSelectionItemHolder(holder: ItemHolder?, viewType: Int, position: Int, selection: Int, childIndex: Int) {
            if (holder === null) {
                return
            }
            var data = mDatas[selection].itemList[childIndex]
            if (data !== null) {
                holder.icon.setImageResource(data.image)
                holder.title.text = data.title
            }

        }
    }

    class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectionTitle: TextView = itemView as TextView
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon = itemView.findViewById<ImageView>(R.id.image)
        var title = itemView.findViewById<TextView>(R.id.title)
    }


    data class Group(val selectionTitle: String) {
        var itemList = mutableListOf<GridItem>()

        fun add(item: GridItem) {
            itemList.add(item)
        }

        fun get(position: Int): GridItem? {
            if (position > itemList.size) {
                return itemList.get(position)
            }
            return null
        }
    }


    data class GridItem(val title: String, val image: Int)
}