/*
 * Copyright (c) 2018 Beijing Chinaway Technologies Co., Ltd. All rights reserved.
 */

package com.baisc.selection.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 分组类型的grid recycler adapter.
 *
 * @param <H>  header viewHolder
 * @param <VH> item viewHolder.
 * @param <F>  footer viewHoler.
 */
public abstract class BaseSelectionRecyclerAdapter<H extends ViewHolder, VH extends ViewHolder, F extends ViewHolder>
        extends Adapter<ViewHolder> {

    private static final int TYPE_HEADER = -1;
    private static final int TYPE_FOOTER = -2;
    private static final int TYPE_VIEW_ITEM = 0;
    private List<ViewState> mViewStates = new ArrayList<>();
    private Context mContext;
    private AdapterDataObserver mDataObserver;

    /**
     * 构造函数.
     *
     * @param context 上下文
     */
    public BaseSelectionRecyclerAdapter(Context context) {
        mContext = context;
        registerAdapterDataObserver(new DataChangedObserve());
    }

    /**
     * 设置适配器数据变化观察者.
     *
     * @param observer {@link AdapterDataObserver}
     */
    public void setAdapterDataObserver(AdapterDataObserver observer) {
        mDataObserver = observer;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        analysisPosition();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mViewStates.clear();
    }

    @Override
    public final int getItemViewType(int position) {
        int selection = findSelectionInPosition(position);
        int index = findChildIndexInPosition(position);

        if (isSelectionHeaderForPosition(position)) {
            return getSelectionHeaderType(selection);
        } else if (isSelectionFooterForPosition(position)) {
            return getSelectionFooterType(selection);
        } else {
            return getSelectionItemType(selection, index);
        }
    }

    private void analysisPosition() {
        int totalCount = getItemCount();
        int selection = getSelection();
        int position = -1;
        mViewStates.clear();
        for (int group = 0; group < selection; group++) {
            position++;
            setUpViewState(true, false, group, ViewState.INDEX_HEADER);
            int childCount = getItemCountInSelection(group);
            for (int child = 0; child < childCount; child++) {
                position++;
                setUpViewState(false, false, group, child);
            }
            if (hasFooterInSelection(group)) {
                position++;
                setUpViewState(false, true, group, ViewState.INDEX_FOOTER);
            }
        }
        if (totalCount > 0 && position != totalCount - 1) {
            throw new UnsupportedOperationException("you many check the adapter list data, the "
                    + "position must == getItemCount()-1, the position is " + position + " but "
                    + "getItemCount is " + (getItemCount() - 1));
        }
    }

    private void setUpViewState(boolean header, boolean footer, int selection, int childIndex) {
        ViewState state = new ViewState();
        state.mIsFooter = footer;
        state.mIsHeader = header;
        state.mSelection = selection;
        state.mChildIndex = childIndex;
        mViewStates.add(state);
    }


    /**
     * 获取当前selection 的header viewType.
     *
     * @param selection selection id
     * @return header viewType
     * @see #isSelectionHeaderType(int)
     */
    protected int getSelectionHeaderType(int selection) {
        return TYPE_HEADER;
    }

    /**
     * 获取当前selection的footer viewType.
     *
     * @param selection selection selection id
     * @return footer viewType
     * @see #isSelectionFooterType(int)
     */
    protected int getSelectionFooterType(int selection) {
        return TYPE_FOOTER;
    }

    /**
     * 获取当前selection 的child 的viewType.
     *
     * @param selection  selection id
     * @param childIndex child index
     * @return child index
     */
    protected int getSelectionItemType(int selection, int childIndex) {
        return TYPE_VIEW_ITEM;
    }

    /**
     * 当前position是否是header.
     *
     * @param position stable position
     * @return true is header
     */
    public boolean isSelectionHeaderForPosition(int position) {
        ViewState state = mViewStates.get(position);
        return state.mIsHeader;
    }

    /**
     * 当前position是否是footer.
     *
     * @param position stable position
     * @return true is footer
     */
    public boolean isSelectionFooterForPosition(int position) {
        ViewState state = mViewStates.get(position);
        return state.mIsFooter;
    }

    /**
     * 如果你重写了getSelectionHeaderType方法，则需要保证该方法也被重写，以用来判断当前viewType是否是header类型.
     *
     * @param viewType viewType
     * @return true or false
     * @see #getSelectionFooterType(int)
     */
    protected boolean isSelectionHeaderType(int viewType) {
        return viewType == TYPE_HEADER;
    }

    /**
     * 如果你重写了getSelectionFooterType方法，则需要保证该方法也被重写，以用来判断当前viewType是否是footer类型.
     *
     * @param viewType viewType
     * @return true or false
     * @see #getSelectionFooterType
     */
    protected boolean isSelectionFooterType(int viewType) {
        return viewType == TYPE_FOOTER;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isSelectionFooterType(viewType)) {
            return onCreateSelectionFooterHolder(parent, viewType);
        } else if (isSelectionHeaderType(viewType)) {
            return onCreateSelectionHeaderHolder(parent, viewType);
        } else {
            return onCreateSelectionItemHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int selection = findSelectionInPosition(position);
        int index = findChildIndexInPosition(position);
        int viewType = getItemViewType(position);
        if (isSelectionHeaderType(viewType)) {
            onBindSelectionHeaderHolder((H) holder, viewType, selection);
        } else if (isSelectionFooterType(viewType)) {
            onBindSelectionFooterHolder((F) holder, viewType, selection);
        } else {
            onBindSelectionItemHolder((VH) holder, viewType, position, selection,
                    index);
        }
    }

    private int findSelectionInPosition(int position) {
        ViewState state = mViewStates.get(position);
        return state.mSelection;
    }

    private int findChildIndexInPosition(int position) {
        ViewState state = mViewStates.get(position);
        return state.mChildIndex;
    }

    @Override
    public int getItemCount() {
        return getTotalCount();
    }

    private int getTotalCount() {
        int totalCount = 0;
        int selection = getSelection();
        for (int index = 0; index < selection; index++) {
            totalCount += getItemCountInSelection(index) + (hasFooterInSelection(selection) ? 1 : 0);
        }
        return totalCount + selection;
    }

    protected boolean hasFooterInSelection(int selection) {
        return false;
    }


    protected abstract int getSelection();

    protected abstract int getItemCountInSelection(int selection);

    protected abstract H onCreateSelectionHeaderHolder(ViewGroup parent, int viewType);

    protected abstract VH onCreateSelectionItemHolder(ViewGroup parent, int viewType);

    protected F onCreateSelectionFooterHolder(ViewGroup parent, int viewType) {
        return null;
    }

    protected abstract void onBindSelectionHeaderHolder(H holder, int viewType, int selection);

    protected abstract void onBindSelectionItemHolder(VH holder, int viewType, int  position,
            int selection, int childIndex);

    protected void onBindSelectionFooterHolder(F holder, int viewType, int selection) {
        // do nothing
    }

    private class DataChangedObserve extends AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mDataObserver != null) {
                mDataObserver.onChanged();
            }
            analysisPosition();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            if (mDataObserver != null) {
                mDataObserver.onItemRangeChanged(positionStart, itemCount);
            }
            analysisPosition();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            if (mDataObserver != null) {
                mDataObserver.onItemRangeInserted(positionStart, itemCount);
            }
            analysisPosition();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            if (mDataObserver != null) {
                mDataObserver.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
            analysisPosition();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            if (mDataObserver != null) {
                mDataObserver.onItemRangeRemoved(positionStart, itemCount);
            }
            analysisPosition();
        }
    }

    /**
     * item view state.
     */
    public static class ViewState {

        /**
         * header在selection中的index.
         */
        public static final int INDEX_HEADER = -1;
        /**
         * footer在selection中的index.
         */
        public static final int INDEX_FOOTER = -2;
        /**
         * 是否是header.
         */
        boolean mIsHeader;
        /**
         * 是否是footer.
         */
        boolean mIsFooter;
        /**
         * 当前selectionId.
         */
        int mSelection;
        /**
         * 组里面的item index.
         * 如果是header或者footer，则index 为 INDEX_HEADER or INDEX_FOOTER
         */
        int mChildIndex;
    }
}
