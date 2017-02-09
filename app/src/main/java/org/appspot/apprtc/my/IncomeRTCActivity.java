package org.appspot.apprtc.my;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import org.appspot.apprtc.ConnectActivity;
import org.appspot.apprtc.R;

import java.io.IOException;

public class IncomeRTCActivity extends AppCompatActivity {//implements View.OnClickListener {
    Vibrator vibrator;
    ImageButton rtc_accept;
    ImageButton rtc_decline;
    Intent intent;
    TextView textView;
    int room_id;
    String caller;
    String callee;
    MyApplication myApplication;
    String msg;
    BroadcastReceiver mReceiver;
    IntentFilter intentfilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_rtc);
        myApplication = (MyApplication) getApplicationContext();
        textView = (TextView)findViewById(R.id.caller);
        rtc_accept = (ImageButton) findViewById(R.id.rtc_accept);
//        rtc_accept.setOnClickListener(this);
        rtc_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("눌려라","눌려라"+room_id);
                vibrator.cancel();
                intent = new Intent(IncomeRTCActivity.this,ConnectActivity.class);
                //tcp 프랜드 아이디로 화상채팅하자고 보냄 맞음
                intent.putExtra("call_room",String.valueOf(room_id));
                //내아이디 프랜드아이디 방정보 tcp 서버에 보냄
                startActivity(intent);
                finish();
            }
        });
        rtc_decline = (ImageButton) findViewById(R.id.rtc_decline);
//        rtc_decline.setOnClickListener(this);
        rtc_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.cancel();
                //tcp로 거절 보냄
                //{ "status": "rtc_call_end","caller": "yoon","callee":"aaaa","room_cnt": 96539148}
                msg = "{ \"status\": \"rtc_call_end\",\"caller\": \""+caller+"\",\"callee\":\""+callee+"\",\"room_cnt\":"+room_id+"}";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            myApplication.getOut().writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                intent = new Intent(IncomeRTCActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        intent = getIntent();
        caller = intent.getExtras().getString("caller");
        callee = intent.getExtras().getString("callee");
        room_id =intent.getExtras().getInt("room_id");

        textView.setText(caller);
        Log.d("눌려라","눌려라");
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
        long[] pattern = { 0, 500, 100, 400, 100 };
        vibrator.vibrate(pattern,0);
        //10초지나면 자동 메인화면 rtc_decline 누른셈
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
    @Override
    public void onResume() {
        super.onResume();
        //상대방이 거부한 경우
        //브로드캐스트의 액션을 등록하기 위한 인텐트 필터
        intentfilter = new IntentFilter();
        intentfilter.addAction("org.appspot.apprtc.my.rtc_caller_end");
        //동적 리시버 구현
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                vibrator.cancel();
                finish();
            }
        };
        //Receiver 등록
        registerReceiver(mReceiver, intentfilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Receiver 등록
        unregisterReceiver(mReceiver);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.rtc_accept:
//                Log.d("눌려라","눌려라"+room_id);
//                intent = new Intent(IncomeRTCActivity.this,ConnectActivity.class);
//                //tcp 프랜드 아이디로 화상채팅하자고 보냄 맞음
//                intent.putExtra("call_room",room_id);
//                //내아이디 프랜드아이디 방정보 tcp 서버에 보냄
//                startActivity(intent);
//                finish();
//            case R.id.rtc_decline:
//                //tcp로 거절 보냄
//                intent = new Intent(IncomeRTCActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//
//        }
//    }
}
