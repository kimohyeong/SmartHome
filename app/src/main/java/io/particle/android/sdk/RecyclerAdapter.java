package io.particle.android.sdk;

import android.animation.ValueAnimator;
import android.content.Context;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.cloudDB.DBhelper;
import io.particle.android.sdk.utils.Async;
import io.particle.sdk.app.R;

import static io.particle.android.sdk.utils.Py.list;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>  {
    public ArrayList<Device> listData = new ArrayList<>();
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;
    private int roomNum;
    CloudLink cloudLink = new CloudLink();
    private DBhelper helper;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_node, parent, false);
        helper = new DBhelper(context);
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

    void setRoomNum(int roomNum){this.roomNum  = roomNum;}

    class ItemViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

        private TextView deviceNameTxt, deviceStateTxt, deviceInfoTxt;
        private LinearLayout fanDetailLayout, blindDetailLayout, ledDetailLayout, rgbledDetailLayout;
        private ImageView deviceImg;
        private Device data;
        private Switch deviceSwitch;
        private int position;

        ItemViewHolder(View itemView) {
            super(itemView);

            deviceNameTxt = itemView.findViewById(R.id.deviceText);
            deviceStateTxt = itemView.findViewById(R.id.deviceStateText);
            deviceInfoTxt = itemView.findViewById(R.id.deviceInfoText);
            deviceImg = itemView.findViewById(R.id.deviceImg);
            deviceSwitch = itemView.findViewById(R.id.deviceSwitch);
            // LED layout listener 세팅

            fanDetailLayout = itemView.findViewById(R.id.fanDetailLayout);
            blindDetailLayout = itemView.findViewById(R.id.blindDetailLayout);
            ledDetailLayout = itemView.findViewById(R.id.ledDetailLayout);
            rgbledDetailLayout = itemView.findViewById(R.id.rgbledDetailLayout);
        }


        void onBind(Device data, int position) {
            this.data = data;
            this.position = position;

            deviceNameTxt.setText(data.getDeviceName());
            deviceStateTxt.setText(data.getDeviceState());
            deviceImg.setImageDrawable(data.getDeviceImgDrawable());

            // switch on off
            deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String s = data.getDeviceState();
                    if(s.equals("1") && isChecked)
                        return;
                    else if(s.equals("0") && !isChecked)
                        return;;

                    String state, detailState;
                    int x =Integer.parseInt(RoomActivity.actNum.getText().toString());
                    int y =Integer.parseInt(RoomActivity.inactNum.getText().toString());
                    if(isChecked) {
                        state = "1";
                        x++;
                        y--;
                    }
                    else {
                        state = "0";
                        x--;
                        y++;
                    }

                    //int resultCode = cloudLink.setDevice(roomNum,position, command);
                    int resultCode = 1;
                    if(resultCode==1)
                    {
                        RoomActivity.actNum.setText(x+"");
                        RoomActivity.inactNum.setText(y+"");
                        data.setDeviceState(state);
                        notifyDataSetChanged();

                        helper.updateDevice(data);
                    }
                }
            });
            if(data.getDeviceState().equals("1"))
                deviceSwitch.setChecked(true);
            else
                deviceSwitch.setChecked(false);


            changeVisibility(selectedItems.get(position));

            deviceInfoTxt.setOnClickListener(this);
            deviceStateTxt.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deviceInfoText:
                case R.id.deviceStateText:
                    if (selectedItems.get(position)) {
                        selectedItems.delete(position);
                    } else {
                        selectedItems.delete(prePosition);
                        selectedItems.put(position, true);
                    }
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    prePosition = position;
                    break;
            }
            return;
        }

        private void changeVisibility(final boolean isExpanded) {
            final LinearLayout curLayout = visibleLayout(this.data.getDeviceType());
            if(curLayout == null) return;

            //int dpValue = curLayout.getHeight();
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (50 * d);

            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();

                    curLayout.getLayoutParams().height = value;
                    curLayout.requestLayout();
                    curLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            va.start();
        }

        private LinearLayout visibleLayout(int deviceNum) {
            // 0 - led, 1 - blind, 2 - fan
            switch (deviceNum){
                case 0:
                    SetLayoutListener(ledDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.VISIBLE);
                    rgbledDetailLayout.setVisibility(View.GONE);
                    return ledDetailLayout;
                case 1:
                    SetLayoutListener(rgbledDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.GONE);
                    rgbledDetailLayout.setVisibility(View.VISIBLE);
                    return rgbledDetailLayout;
                case 2:
                    SetLayoutListener(blindDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.VISIBLE);
                    ledDetailLayout.setVisibility(View.GONE);
                    rgbledDetailLayout.setVisibility(View.GONE);
                    return blindDetailLayout;
                case 3:
                    SetLayoutListener(fanDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.VISIBLE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.GONE);
                    rgbledDetailLayout.setVisibility(View.GONE);
                    return fanDetailLayout;
                case 4:
                    SetLayoutListener(fanDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.GONE);
                    rgbledDetailLayout.setVisibility(View.GONE);
                    return null;
            }
            return null;
        }

        // 각 컴포넌트 리스너 등록
        private void SetLayoutListener(LinearLayout curLayout, int deviceNum) {
            switch (deviceNum){
                case 0:
                    Button led25Btn = curLayout.getChildAt(0).findViewById(R.id.led_25);
                    Button led50Btn = curLayout.getChildAt(0).findViewById(R.id.led_50);
                    Button led75Btn = curLayout.getChildAt(0).findViewById(R.id.led_75);
                    Button led100Btn = curLayout.getChildAt(0).findViewById(R.id.led_100);

                    led25Btn.setOnClickListener(this::onClickLed);  led75Btn.setOnClickListener(this::onClickLed);
                    led50Btn.setOnClickListener(this::onClickLed);  led100Btn.setOnClickListener(this::onClickLed);
                    return;
                case 1:
                    Button redBtn = curLayout.getChildAt(0).findViewById(R.id.rgb_red);
                    Button yellowBtn = curLayout.getChildAt(0).findViewById(R.id.rgb_yellow);
                    Button greenBtn = curLayout.getChildAt(0).findViewById(R.id.rgb_green);
                    Button blueBtn = curLayout.getChildAt(0).findViewById(R.id.rgb_blue);
                    Button purpleBtn = curLayout.getChildAt(0).findViewById(R.id.rgb_purple);

                    redBtn.setOnClickListener(this::onClickRgbLed);  yellowBtn.setOnClickListener(this::onClickRgbLed);
                    greenBtn.setOnClickListener(this::onClickRgbLed);  blueBtn.setOnClickListener(this::onClickRgbLed);
                    purpleBtn.setOnClickListener(this::onClickRgbLed);
                    return;
                case 2:
                    // BLIND layout listener 세팅
                    Button blind0Btn = curLayout.getChildAt(0).findViewById(R.id.blind_0);
                    Button blind25Btn = curLayout.getChildAt(0).findViewById(R.id.blind_25);
                    Button blind50Btn = curLayout.getChildAt(0).findViewById(R.id.blind_50);
                    Button blind75Btn = curLayout.getChildAt(0).findViewById(R.id.blind_75);
                    Button blind100Btn = curLayout.getChildAt(0).findViewById(R.id.blind_100);

                    blind0Btn.setOnClickListener(this::onClickBlind);
                    blind25Btn.setOnClickListener(this::onClickBlind);
                    blind50Btn.setOnClickListener(this::onClickBlind);
                    blind75Btn.setOnClickListener(this::onClickBlind);
                    blind100Btn.setOnClickListener(this::onClickBlind);
                    return;
                case 3:
                    // FAN layout listener 세팅
                    Button fanMinBtn = curLayout.getChildAt(0).findViewById(R.id.fanMinBtn);
                    Button fanMidBtn = curLayout.getChildAt(0).findViewById(R.id.fanMidBtn);
                    Button fanMaxBtn = curLayout.getChildAt(0).findViewById(R.id.fanMaxBtn);
                    fanMinBtn.setOnClickListener(this::onClickFan);
                    fanMidBtn.setOnClickListener(this::onClickFan);
                    fanMaxBtn.setOnClickListener(this::onClickFan);
                    return;
            }
        }

        /*Definition of Listener */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.e("log1-switch", buttonView.getId()+""+isChecked+"");
        }


        void onClickLed(View v) {
//            if(SmartHomeMainActivity.meshGateway == null) {
//                Log.e("log1-blindBtn", "Gateway is null");
//                return;
//            }

            String detail_state="";
            switch (v.getId()) {
                case R.id.led_25:
                    detail_state="25";
                    break;
                case R.id.led_50:
                    detail_state="50";
                    break;
                case R.id.led_75:
                    detail_state="75";
                    break;
                case R.id.led_100:
                    detail_state="100";
                    break;
            }

            String commandStr = this.data.getDeviceRoomNum() + "/" + this.data.getDeviceName() +"/" +
                    this.data.getDeviceState() + "/" + this.data.getDeviceDetailState();
            //int resultCode = cloudLink.setDevice(commandStr);
            int resultCode = 1;
            if(resultCode==1)
            {
                deviceInfoTxt.setText(detail_state);
                data.setDeviceDetailState(detail_state);

                helper.updateDevice(data);

                notifyDataSetChanged();
            }


            Log.e("smart onclick: ", commandStr);

        }

        void onClickRgbLed(View v) {
//            if(SmartHomeMainActivity.meshGateway == null) {
//                Log.e("log1-blindBtn", "Gateway is null");
//                return;
//            }

            switch (v.getId()) {
                case R.id.rgb_red:
                    this.data.setDeviceDetailState("255/0/0");
                    break;
                case R.id.rgb_yellow:
                    this.data.setDeviceDetailState("255/255/0");
                    break;
                case R.id.rgb_green:
                    this.data.setDeviceDetailState("0/255/0");
                    break;
                case R.id.rgb_blue:
                    this.data.setDeviceDetailState("0/0/255");
                    break;
                case R.id.rgb_purple:
                    this.data.setDeviceDetailState("100/0/255");
                    break;
            }
            String commandStr = this.data.getDeviceRoomNum() + "/" + this.data.getDeviceName() +"/" +
                    this.data.getDeviceState() + "/" + this.data.getDeviceDetailState();
            Log.e("smart onclick: ", commandStr);
//            sendCloudCommand(commandStr);
        }

        void onClickFan(View v) {
//            if(SmartHomeMainActivity.meshGateway == null) {
//                Log.e("log1-blindBtn", "Gateway is null");
//                return;
//            }
            if(v.getId() == R.id.fanMinBtn) {
                this.data.setDeviceDetailState("min");
            } else if(v.getId() == R.id.fanMidBtn) {
                this.data.setDeviceDetailState("mid");
            } else {
                this.data.setDeviceDetailState("max");
            }
            String commandStr = this.data.getDeviceRoomNum() + "/" + this.data.getDeviceName() +"/" +
                    this.data.getDeviceState() + "/" + this.data.getDeviceDetailState();
            Log.e("smart onclick: ", commandStr);

            //sendCloudCommand(commandStr);
        }

        void onClickBlind(View v) {
//            if(SmartHomeMainActivity.meshGateway == null) {
//                Log.e("log1-blindBtn", "Gateway is null");
//                return;
//            }

            switch (v.getId()) {
                case R.id.blind_0:
                    this.data.setDeviceDetailState("0");
                    break;
                case R.id.blind_25:
                    this.data.setDeviceDetailState("25");
                    break;
                case R.id.blind_50:
                    this.data.setDeviceDetailState("50");
                    break;
                case R.id.blind_75:
                    this.data.setDeviceDetailState("75");
                    break;
                case R.id.blind_100:
                    this.data.setDeviceDetailState("100");
                    break;
            }
            String commandStr = this.data.getDeviceRoomNum() + "/" + this.data.getDeviceName() +"/" +
                    this.data.getDeviceState() + "/" + this.data.getDeviceDetailState();
            Log.e("smart onclick: ", commandStr);

            //sendCloudCommand(commandStr);
        }
    }
}