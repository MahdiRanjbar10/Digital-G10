package com.esnplus.digitalG10;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomSpinner extends LinearLayout {
    private TextView spinnerSelectedTv;
    private ImageView spinnerSelectedIv;
    private View arrowView;
    private RecyclerView spinnerRecyclerView;
    private Context context;
    private int selectedIndex;
    private SpinnerAdapter adapter;
    private LinearLayout headLayout;
    private OnItemClickListener onItemClickListener;

    public CustomSpinner(Context context) {
        super(context);
        init(context);
    }
    public CustomSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public CustomSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.custom_spinner_layout, this, true);

        spinnerSelectedTv = rootView.findViewById(R.id.selected_item_text);
        spinnerSelectedIv = rootView.findViewById(R.id.selected_item_icon);
        arrowView = rootView.findViewById(R.id.arrow_view);
        spinnerRecyclerView = rootView.findViewById(R.id.spinner_rv);
        headLayout = rootView.findViewById(R.id.head);
    }
    private void openSpinner() {
        spinnerRecyclerView.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_non_clockwise);
        arrowView.startAnimation(anim);
        arrowView.setBackgroundResource(R.drawable.ic_arrow_up);
    }
    private void closeSpinner() {
        spinnerRecyclerView.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_clockwise);
        arrowView.startAnimation(anim);
        arrowView.setBackgroundResource(R.drawable.ic_arrow_down);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setUpAdapter(List<AlarmDevice> list) {
        adapter = new SpinnerAdapter(list);
        spinnerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        spinnerRecyclerView.setAdapter(adapter);
        if (adapter.getItemCount() == 0){
            spinnerSelectedTv.setText("هیچ دستگاهی ثبت نام نشده است");
            spinnerSelectedIv.setImageDrawable(null);
            headLayout.setOnClickListener(null);
        }else {
            selectedIndex = 0;
            spinnerSelectedTv.setText(adapter.list.get(selectedIndex).getName());
            spinnerSelectedIv.setImageResource(adapter.list.get(selectedIndex).getIcon());
            headLayout.setOnClickListener(v -> {
                if (spinnerRecyclerView.getVisibility() == View.GONE) {
                    openSpinner();
                } else {
                    closeSpinner();
                }
            });
        }
    }
    public AlarmDevice getSelectedItem() {
        if (adapter != null) {
           if(adapter.list != null) {
               return adapter.list.isEmpty() ? new AlarmDevice() : adapter.list.get(selectedIndex);
           }
        }
        return new AlarmDevice();
    }
    public void setSelectedItem(int position){
        selectedIndex = position;
        AlarmDevice alarmDevice = adapter.list.get(position);
        spinnerSelectedTv.setText(alarmDevice.getName());
        spinnerSelectedIv.setImageResource(alarmDevice.getIcon());
    }
    private class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.ViewHolder> {
        final List<AlarmDevice> list;
        public SpinnerAdapter(List<AlarmDevice> list) {
            this.list = list;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AlarmDevice alarmDevice = list.get(position);
            holder.itemName.setText(alarmDevice.getName());
            holder.itemPhoneNumber.setText(alarmDevice.getPhoneNumber());
            holder.itemIcon.setImageResource(alarmDevice.getIcon());
            holder.itemView.setOnClickListener(view -> {
                setSelectedItem(holder.getAdapterPosition());
                onItemClickListener.onClick(selectedIndex);
                closeSpinner();
            });
        }
        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView itemName,itemPhoneNumber;
            ImageView itemIcon;
            public ViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.itemName);
                itemPhoneNumber = itemView.findViewById(R.id.itemPhoneNumber);
                itemIcon = itemView.findViewById(R.id.itemIcon);
            }
        }
    }
    public interface OnItemClickListener{
        void onClick(int position);
    }
}