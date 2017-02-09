 package org.appspot.apprtc.my;

 import android.content.Intent;
 import android.os.Bundle;
 import android.support.v7.app.AppCompatActivity;
 import android.util.Log;
 import android.view.View;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.TextView;

 import org.appspot.apprtc.ConnectActivity;
 import org.appspot.apprtc.R;
 import org.json.JSONArray;
 import org.json.JSONObject;

 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.net.Socket;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 import java.util.Random;

 public class  FriendDetailActivity extends AppCompatActivity {
     int room_id;
     String msg;
    EditText call_room;
    ImageView call,chat;
    MyApplication myApplication ;
    Socket socket;
    DataOutputStream out;
    Intent intent;
    String user_id;
    String friend_id;
     TextView friendId;
     MySQLiteOpenHelper dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        friendId = (TextView)findViewById(R.id.friendId);
        dbManager = new MySQLiteOpenHelper(getApplicationContext(), "user.db", null, 1);
        intent = getIntent();
        //int room_id = Integer.parseInt(intent.getStringExtra("room_id"));
        friend_id = intent.getExtras().getString("friend_id");
        Log.d("받은 값", friend_id);
        friendId.setText(friend_id);
        myApplication = (MyApplication)getApplicationContext();
        user_id = myApplication.getId();
        socket=myApplication.getSocket();
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        call_room = (EditText) findViewById(R.id.call_room);
        Random random = new Random();
        room_id=random.nextInt(1000000000);
        Log.d("왜이래", String.valueOf(room_id));
        call_room.setText(String.valueOf(room_id));
        call = (ImageView) findViewById(R.id.call);
        chat = (ImageView) findViewById(R.id.chat);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //{ "status": "rtc_call_start","caller": "yoon","callee":"aaaa","room_cnt": 96539148}
                msg = "{ \"status\": \"rtc_call_start\",\"caller\": \""+myApplication.getId()+"\",\"callee\":\""+friend_id+"\",\"room_cnt\": "+room_id+"}";
                String msg2 = "{ \"status\": \"rtc_caller_end\",\"caller\": \""+myApplication.getId()+"\",\"callee\":\""+friend_id+"\",\"room_cnt\": "+room_id+"}";
                myApplication.setRTC_msg(msg2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            myApplication.getOut().writeUTF(msg);
                            //Intent intent = new Intent(ChatRoomActivity.this,MainActivity.class);
                            //startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                //Intent intent = new Intent(FriendDetailActivity.this, FriendDetailActivity.class);
                Intent intent = new Intent(FriendDetailActivity.this, ConnectActivity.class);
                //tcp 프랜드 아이디로 화상채팅하자고 보냄 맞음
                String room_cnt = call_room.getText().toString();
                intent.putExtra("call_room",room_cnt);
                //내아이디 프랜드아이디 방정보 tcp 서버에 보냄
                startActivity(intent);
                finish();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //맴버 정렬
                List<String> member = new ArrayList<String>();
                member.add(friend_id);
                member.add(user_id);
                Collections.sort(member);
                String[] temp = member.toArray(new String[member.size()]);
                String members = Arrays.toString(temp);
                Log.d("정렬 잘되냐?",members);
                //분기 디비 검사 방이있으면 인텐드
                //방이없으면 -1
                int  branch;
                branch = dbManager.RoomisExist(members,myApplication.getId());
                if(branch!=-1){
                    Intent intent = new Intent(FriendDetailActivity.this, ChatRoomActivity.class);
                    intent.putExtra("room_id",branch);
                    startActivity(intent);
                }//없으면 소켓 보냄 서비스에서 인텐트
                else {
                    //String msg = "{ \"data\": [ \""+user_id+"\", \""+friend_id+"\" ], \"status\": \"mk_room\", \"sentfrom\": \""+user_id+"\" }";
                    //{ "status": "send_msg", "room_cnt": 0, "sentfrom": "yoon" , "data": "가나다아" }

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("status", "mk_room");
                        jsonObject.put("sentfrom", myApplication.getId());
                        JSONArray jsonArray= new JSONArray(temp);
                        //jsonArray.put(myApplication.getId());
                        jsonObject.put("data",jsonArray);
                        Log.d("제이슨으로 변환", jsonObject.toString());
                        final String msg = jsonObject.toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //out.write(msg.getBytes("UTF-8"));
                                    myApplication.getOut().writeUTF(msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    } /*catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            }
        });
    }
}
