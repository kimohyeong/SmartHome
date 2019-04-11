package com.example.a502.smarthome;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<Device> listData = new ArrayList<>();
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_node, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(Device data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView deviceNameTxt, deviceStateTxt, deviceInfoTxt;
        private LinearLayout fanDetailLayout, blindDetailLayout, ledDetailLayout;
        private ImageView deviceImg;
        private Device data;
        private int position;

        ItemViewHolder(View itemView) {
            super(itemView);

            deviceNameTxt = itemView.findViewById(R.id.deviceText);
            deviceStateTxt = itemView.findViewById(R.id.deviceStateText);
            deviceInfoTxt = itemView.findViewById(R.id.deviceInfoText);
            deviceImg = itemView.findViewById(R.id.deviceImg);

            fanDetailLayout = (LinearLayout)itemView.findViewById(R.id.fanDetailLayout);
            blindDetailLayout = (LinearLayout)itemView.findViewById(R.id.blindDetailLayout);
            ledDetailLayout = (LinearLayout)itemView.findViewById(R.id.ledDetailLayout);
        }

        void onBind(Device data, int position) {
            this.data = data;
            this.position = position;

            deviceNameTxt.setText(data.getDeviceName());
            deviceStateTxt.setText(data.getDeviceState());
            deviceImg.setImageDrawable(data.getDeviceImgDrawable());

            changeVisibility(selectedItems.get(position));

            deviceInfoTxt.setOnClickListener(this);
            deviceStateTxt.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (selectedItems.get(position)) {
                // 펼쳐진 Item을 클릭 시
                selectedItems.delete(position);
            } else {
                // 직전의 클릭됐던 Item의 클릭상태를 지움
                selectedItems.delete(prePosition);
                // 클릭한 Item의 position을 저장
                selectedItems.put(position, true);
            }
            // 해당 포지션의 변화를 알림
            if (prePosition != -1) notifyItemChanged(prePosition);
            notifyItemChanged(position);
            // 클릭된 position 저장
            prePosition = position;
        }

        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 0;
            if (this.data.getDeviceType() == 0) {
                dpValue = 200;
            } else {
                dpValue = 150;
            }

            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            final LinearLayout curLayout = visibleLayout(2);

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초

            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // value는 height 값
                    int value = (int) animation.getAnimatedValue();
                    //imageView의 높이 변경
                    curLayout.getLayoutParams().height = value;
                    curLayout.requestLayout();
                    // imageView가 실제로 사라지게하는 부분
                    curLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }

        private LinearLayout visibleLayout(int deviceNum) {
            // 0 - fan, 1 - blind, 2 - led
            switch (deviceNum){
                case 0:
                    fanDetailLayout.setVisibility(View.VISIBLE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.GONE);
                    return fanDetailLayout;
                case 1:
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.VISIBLE);
                    ledDetailLayout.setVisibility(View.GONE);
                    return blindDetailLayout;
                case 2:
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.VISIBLE);
                    return ledDetailLayout;
            }
            return null;
        }
    }
}