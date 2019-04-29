package com.example.a502.smarthome;

import android.animation.ValueAnimator;
import android.content.Context;

import android.text.Layout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

import static io.particle.android.sdk.utils.Py.list;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>  {
    private ArrayList<Device> listData = new ArrayList<>();
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

    class ItemViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener{

        private TextView deviceNameTxt, deviceStateTxt, deviceInfoTxt;
        private LinearLayout fanDetailLayout, blindDetailLayout, ledDetailLayout;
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
            deviceSwitch.setOnCheckedChangeListener(this);

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
            switch (v.getId()) {
                case R.id.deviceInfoText:
                case R.id.deviceStateText:
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
                    break;
                case R.id.blindMinBtn:
                case R.id.blindMaxBtn:
                    Log.e("log1-blindBtn",v.getId()+"");
                    break;
                case R.id.fanMinBtn:
                case R.id.fanMidBtn:
                case R.id.fanMaxBtn:
                    if(MainActivity.meshGateway == null) {
                        Log.e("log1-blindBtn", "Gateway is null");
                        return;
                    }
                    if(v.getId() == R.id.fanMinBtn) {
                        this.data.setDeviceState("MIN");
                    } else if(v.getId() == R.id.fanMidBtn) {
                        this.data.setDeviceState("MID");
                    } else {
                        this.data.setDeviceState("MAX");
                    }
                    String commandStr = this.data.getDeviceRoomNum() + "/" + this.data.getDeviceName() +"/"+ this.data.getDeviceState();
                    sendCloudCommand(commandStr);
                    break;
            }
            return;
        }

        private void changeVisibility(final boolean isExpanded) {
            int dpValue = 150;

            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);
            final LinearLayout curLayout = visibleLayout(this.data.getDeviceType());

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
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
            // 0 - led, 1 - blind, 2 - fan
            switch (deviceNum){
                case 0:
                    SetLayoutListener(ledDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.VISIBLE);
                    return ledDetailLayout;
                case 1:
                    SetLayoutListener(blindDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.GONE);
                    blindDetailLayout.setVisibility(View.VISIBLE);
                    ledDetailLayout.setVisibility(View.GONE);
                    return blindDetailLayout;
                case 2:
                    SetLayoutListener(fanDetailLayout, deviceNum);
                    fanDetailLayout.setVisibility(View.VISIBLE);
                    blindDetailLayout.setVisibility(View.GONE);
                    ledDetailLayout.setVisibility(View.GONE);
                    return fanDetailLayout;
            }
            return null;
        }

        // 각 컴포넌트 리스너 등록
        private void SetLayoutListener(LinearLayout curLayout, int deviceNum) {
            switch (deviceNum){
                case 0:
                    ColorSeekBar cs = curLayout.getChildAt(0).findViewById(R.id.colorpicker);
                    cs.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
                        @Override
                        public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                            Log.e("log1-colorSeekBar", color+"");
                        }
                    });
                    return;
                case 1:
                    // BLIND layout listener 세팅
                    Button blindMinBtn = curLayout.getChildAt(0).findViewById(R.id.blindMinBtn);
                    Button blindMaxBtn = curLayout.getChildAt(0).findViewById(R.id.blindMaxBtn);
                    blindMaxBtn.setOnClickListener(this);
                    blindMinBtn.setOnClickListener(this);

                    SeekBar blindSeekBar = curLayout.getChildAt(1).findViewById(R.id.blindSeekBar);
                    blindSeekBar.setOnSeekBarChangeListener(this);
                    return;
                case 2:
                    // FAN layout listener 세팅
                    Switch fanSwitch = curLayout.getChildAt(0).findViewById(R.id.fanSwitch);
                    fanSwitch.setOnCheckedChangeListener(this);

                    Button fanMinBtn = curLayout.getChildAt(1).findViewById(R.id.fanMinBtn);
                    Button fanMidBtn = curLayout.getChildAt(1).findViewById(R.id.fanMidBtn);
                    Button fanMaxBtn = curLayout.getChildAt(1).findViewById(R.id.fanMaxBtn);
                    fanMinBtn.setOnClickListener(this);
                    fanMidBtn.setOnClickListener(this);
                    fanMaxBtn.setOnClickListener(this);
                    return;
            }
        }

        /*Definition of Listener */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.e("log1-switch", buttonView.getId()+""+isChecked+"");
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.e("log1-seekBar", progress+"");
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        public void sendCloudCommand(final String command) {
            Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
                @Override
                public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                    try {
                        String cmd = command;

                        List<String> msg = new ArrayList<String>();
                        msg.add(cmd);
                        int resultCode = MainActivity.meshGateway.callFunction("publishMsg", msg);
                        Log.e("log1-sendCC",command + resultCode);

                    }  catch (ParticleDevice.FunctionDoesNotExistException e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
                @Override
                public void onSuccess(@NonNull Object value) {
                }
                @Override
                public void onFailure(@NonNull ParticleCloudException e) {
                }
            });
        }
    }
}