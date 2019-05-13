package io.particle.android.sdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import io.particle.android.sdk.cloudDB.CloudLink;
import io.particle.sdk.app.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class RoomActivity extends AppCompatActivity {
    private RecyclerAdapter adapter;
    private int roomNum = 0;
    private String roomName;
    private int roomImg;
    public static TextView actNum ;
    public static TextView inactNum ;
    CloudLink cloudLink = new CloudLink();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ActionBar bar = getSupportActionBar();
        bar.hide();

        Intent intent = getIntent();

        roomNum = intent.getIntExtra("ROOM_NUM",0);
        roomName = intent.getStringExtra("NAME");
        roomImg = intent.getIntExtra("IMG",0);
        init();
        getData();
    }
    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        adapter.setRoomNum(roomNum);
        recyclerView.setAdapter(adapter);

        //위layout
        TextView txtRoom = findViewById(R.id.txt_room);
        ImageView imgRoom = findViewById(R.id.img_room);
        actNum = findViewById(R.id.actNum);
        inactNum = findViewById(R.id.inactNum);

        txtRoom.setText(roomName);
        imgRoom.setImageResource(roomImg);

        int an=0,ian=0;
        for(int i=0; i<SmartHomeMainActivity.devices[roomNum].size(); i++){
            if(SmartHomeMainActivity.devices[roomNum].get(i).getDeviceState().substring(0,1).equals("1"))
                an++;
            else
                ian++;
        }
        actNum.setText(String.valueOf(an));
        inactNum.setText(String.valueOf(ian));

        //delete touch
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(findViewById(R.id.recyclerView));
    }

    private void getData() {
        if (SmartHomeMainActivity.devices[roomNum] == null) {
            Log.e("getData","devices is null");
            return;
        }

        // 0 - led, 1 - blind, 2 - fan
        List<Drawable> drawableList = Arrays.asList(getResources().getDrawable(R.drawable.light),getResources().getDrawable(R.drawable.rgbled),getResources().getDrawable(R.drawable.blind),getResources().getDrawable(R.drawable.fan),getResources().getDrawable(R.drawable.temperature));
        for(int i = 0; i< SmartHomeMainActivity.devices[roomNum].size(); i++) {
            Device data = SmartHomeMainActivity.devices[roomNum].get(i);
            data.setDeviceImgDrawable(drawableList.get(data.getDeviceType()));
            adapter.addItem(data);
        }
        adapter.notifyDataSetChanged();
    }


    public void onClickAllOff(View v){
        for(int i=0; i<SmartHomeMainActivity.devices[roomNum].size(); i++)
        {
            String s = SmartHomeMainActivity.devices[roomNum].get(i).getDeviceState();
            String offStr = "0"+s.substring(1,s.length());
            SmartHomeMainActivity.devices[roomNum].get(i).setDeviceState(offStr);
        }
        adapter.notifyDataSetChanged();
        actNum.setText("0");
        inactNum.setText(SmartHomeMainActivity.devices[roomNum].size()+"");

        String commandStr = roomNum + "/" + "ALL" +"/" + "0" + "/" + "0";

        cloudLink.setDevice(commandStr);
    }

    ///////////////////////////////////
    // delete btn //

    enum ButtonsState{
        GONE, RIGHT_VISIBLE
    }

    boolean swipeBack = false;
    ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final float buttonWidth = 200;

    //왼쪽오른쪽 터시이벤트
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d("super","horray");
        }

        //swip후 위치돌려놓기
        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if(swipeBack){
                swipeBack = false;
                return  0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        //swip했을경우 버튼
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if(actionState == ACTION_STATE_SWIPE) {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                //버튼visible상태라면 view위치 -300
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    dX = dX - buttonWidth;
                    //버튼그리기
                    drawButtons(c, viewHolder);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        private void setTouchListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            recyclerView.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                    if (swipeBack) {
                        //view위치 dx 가 버튼위치보다 왼쪽으로갔으면 RIGHT_VISIBLE
                        if(buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                            ///삭제
                            if(buttonInstance.contains(event.getX(),event.getY())){Log.d("super","Remobe");
                                adapter.listData.remove(viewHolder.getAdapterPosition());
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                adapter.notifyItemRangeChanged(viewHolder.getAdapterPosition(),adapter.getItemCount());
                            }
                            buttonShowedState = ButtonsState.GONE;
                        }
                        else if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                        else buttonShowedState = ButtonsState.GONE;

                        //RIGHT_VISIBEL 경우 기존의 click이벤트 false
                        if (buttonShowedState != ButtonsState.GONE) {
                            setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            setItemsClickable(recyclerView, false);
                        }
                        else{
                            onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                        }
                    }
                    return false;
                }
            });
        };
        private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
            recyclerView.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                    return false;
                }
            });
        }
        private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
            recyclerView.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                        recyclerView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                        setItemsClickable(recyclerView, true);
                        swipeBack = false;
                        buttonShowedState = ButtonsState.GONE;
                    }
                    return false;
                }
            });
        }
        private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
            for (int i = 0; i < recyclerView.getChildCount(); ++i) {
                recyclerView.getChildAt(i).setClickable(isClickable);
            }
        }
        RectF buttonInstance;
        private  void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
            float buttonWidthWithoutPadding = buttonWidth-5 ;
            float corners = 0;//16

           // buttonInstance = null;
            if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                View itemView = viewHolder.itemView;
                Paint p = new Paint();
                RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop()+14, itemView.getRight(), itemView.getTop()+255);
                p.setColor(Color.rgb(251,72,80));
                c.drawRoundRect(rightButton, corners, corners, p);
                drawImgBtn( c, rightButton, p);

                buttonInstance = rightButton;
            }
        }
        private void drawImgBtn( Canvas c, RectF button, Paint p) {
            float textSize = 60;
            p.setColor(Color.WHITE);
            p.setAntiAlias(true);

            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.trashcan,null);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Bitmap bitmap1 = bitmap.createScaledBitmap(bitmap, 55, 70,true);
            c.drawBitmap(bitmap1,button.centerX()-bitmap1.getWidth()/2,button.centerY()-bitmap1.getHeight()/2, p);
        }

    };

    public void onClickBack(View v){
        finish();
    }
}
