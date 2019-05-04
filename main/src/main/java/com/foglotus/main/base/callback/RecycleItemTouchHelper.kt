package com.foglotus.main.base.callback

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.foglotus.core.extention.logInfo
import com.foglotus.main.common.viewholder.LoadingMoreViewHolder

/**
 * @author foglotus
 * @since 2019/2/26
 */
class RecycleItemTouchHelper(private val helperCallback: ItemTouchHelperCallback) : ItemTouchHelper.Callback() {

    /**
     * 设置滑动类型标记
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     * 返回一个整数类型的标识，用于判断Item那种移动行为是允许的
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        logInfo("getMovementFlags: ")

        if(viewHolder is LoadingMoreViewHolder){
            return ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.DOWN or ItemTouchHelper.UP,0)
        }

        //START  右向左 END左向右 LEFT  向左 RIGHT向右  UP向上
        //如果某个值传0，表示不触发该操作，次数设置支持上下拖拽，支持向右滑动
        return ItemTouchHelper.Callback.makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START
        )
    }

    /**
     * Item是否支持长按拖动
     *
     * @return
     * true  支持长按操作
     * false 不支持长按操作
     */
    override fun isLongPressDragEnabled(): Boolean {
        //return super.isLongPressDragEnabled()
        return false
    }

    /**
     * Item是否支持滑动
     *
     * @return
     * true  支持滑动操作
     * false 不支持滑动操作
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        return super.isItemViewSwipeEnabled()
    }

    /**
     * 拖拽切换Item的回调
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     * 如果Item切换了位置，返回true；反之，返回false
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        logInfo( "onMove: ")
        helperCallback.onMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    /**
     * 滑动Item
     *
     * @param viewHolder
     * @param direction
     * Item滑动的方向
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        logInfo( "onSwiped: ")
        helperCallback.onItemDelete(viewHolder.adapterPosition)

    }

    /**
     * Item被选中时候回调
     *
     * @param viewHolder
     * @param actionState
     * 当前Item的状态
     * ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
     * ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
     * ItemTouchHelper#ACTION_STATE_DRAG   拖拽中状态
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    interface ItemTouchHelperCallback {
        fun onItemDelete(positon: Int)
        fun onMove(fromPosition: Int, toPosition: Int)
    }

    companion object {
        private val TAG = "RecycleItemTouchHelper"
    }
}