package com.example.yaacoov.utracker.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.model.ActionListItemViewModel;
import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.models.ActionList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yaacoov on 19/03/17.
 *UTracker.
 */


public final class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionAdapterViewHolder> {



    /**
     * The interface that receives onClick messages.
     */
    public interface ActionAdapterOnClickListener {
        /**
         * @param selectedAction Selected habit.
         * @param position      Index of the selected item.
         */
        void onClick(Action selectedAction, int position);
    }

    private ActionList mActionList;
    private ActionAdapterOnClickListener mClickListener;

    /**
     * Creates a ActionAdapter.
     *
     * @param actionList     The data source.
     * @param clickListener The on-click handler for this adapter. This single handler is called
     *                      when an item is clicked.
     */
    public ActionAdapter(@NonNull ActionList actionList,
                         @Nullable ActionAdapterOnClickListener clickListener) {
        this.mActionList = actionList;
        this.mClickListener = clickListener;
    }

    public ActionAdapter(@NonNull ActionList actionList) {
        this(actionList, null);
    }

    public void setActions(List<Action> actions) {
        if (actions == null) {
            this.mActionList.clear();
        } else {
            this.mActionList.setHabits(actions);
        }
        notifyDataSetChanged();
    }

    public List<Action> getActions() {
        return mActionList.getHabits();
    }

    public void setSortOrder(ActionList.SortOrder sortOrder) {
        if (mActionList.getSortOrder() != sortOrder) {
            mActionList.setSortOrder(sortOrder);
            notifyDataSetChanged();
        }
    }

    public void setClickListener(ActionAdapterOnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }

    public void clear() {
        mActionList.clear();
        notifyDataSetChanged();
    }

    public void add(Action action) {
        mActionList.add(action);
        notifyDataSetChanged();
    }

    @Override
    public ActionAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listView = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_list_item,
                parent, false);
        listView.setFocusable(true);
        return new ActionAdapterViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(ActionAdapterViewHolder viewHolder, int position) {
        viewHolder.bindAtPosition(position);
    }

    @Override
    public int getItemCount() {
        return mActionList.getHabits().size();
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    class ActionAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_list_item_Action_title)
        TextView nameTextView;

        @BindView(R.id.tv_list_item_reset_period)
        TextView resetPeriodTextView;

        @BindView(R.id.tv_list_item_count)
        TextView countTextView;

        private ActionListItemViewModel mViewModel;

        ActionAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mViewModel = new ActionListItemViewModel(itemView.getContext());
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                int position = getAdapterPosition();
                Action selectedAction = mActionList.getHabits().get(position);
                mClickListener.onClick(selectedAction, position);
            }
        }

        private void bindAtPosition(int position) {
            mViewModel.setAction(mActionList.getHabits().get(position));

            if (itemView instanceof CardView) {
                ((CardView) itemView).setCardBackgroundColor(mViewModel.getBackgroundColor());
            } else {
                itemView.setBackgroundColor(mViewModel.getBackgroundColor());
            }

            nameTextView.setText(mViewModel.getHabitName());
            nameTextView.setTextColor(mViewModel.getHabitNameTextColor());
            resetPeriodTextView.setText(mViewModel.getResetFreq());
            countTextView.setText(mViewModel.getScore());
        }

    }

}
