package org.appspot.apprtc.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.appspot.apprtc.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AddFriendsForEditActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText editText;
    Button button;
    ListView listView;
    HorizontalRecyclerAdapter horizontalRecyclerAdapter;
    ArrayList<Friends> friendsArrayList = new ArrayList<Friends>();
    ArrayList<Friends> friendsArrayListforRecyclerView = new ArrayList<Friends>();
    ArrayList<String> member = new ArrayList<String>();
    ArrayList<String> member_from_room = new ArrayList<String>();
    Friends friends  = new Friends();
    MySQLiteOpenHelper mySQLiteOpenHelper;
    MyApplication myApplication;
    ListViewAdapter listViewAdapter;
    Socket socket;
    DataOutputStream out;
    Intent intent;
    int room_id;

    @Override
    protected void onResume() {
        super.onResume();
        socket=myApplication.getSocket();
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext(), "user.db", null, 1);
        friendsArrayList = mySQLiteOpenHelper.FriendListFormUserID(myApplication.getId());
        //디비 맴버 조회
        intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");
        member_from_room = mySQLiteOpenHelper.FriendListFormRoomIDUserID(room_id,myApplication.getId());
        //디비멤버 체크 표시
//        mySQLiteOpenHelper.close();
        listViewAdapter  = new ListViewAdapter(friendsArrayList,R.layout.friend_list_item);
        listView.setAdapter(listViewAdapter);
        for(int i=0;i<member_from_room.size();i++){
            Friends friends= new Friends();
            friends.setTitle(member_from_room.get(i));
            if(!member_from_room.get(i).equals(myApplication.getId())){
                friendsArrayListforRecyclerView.add(friends);
            }
        }
        horizontalRecyclerAdapter = new HorizontalRecyclerAdapter(friendsArrayListforRecyclerView, R.layout.friend_list_item_hor);
        recyclerView.setAdapter(horizontalRecyclerAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySQLiteOpenHelper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview2);
        editText = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button2);
        listView = (ListView) findViewById(R.id.listView2);
        myApplication = (MyApplication)getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //friendsArrayListforRecyclerView 요걸 정렬 해서 mk_room
                FriendsComparator comp = new FriendsComparator();
                //Collections.sort(friendsArrayListforRecyclerView, comp);
                String temp;
                for(int i=0;i<friendsArrayListforRecyclerView.size();i++){
                    temp=friendsArrayListforRecyclerView.get(i).getTitle();
                    member.add(temp);
                }
                //내아이디
                member.add(myApplication.getId());
                Collections.sort(member);
                String[] temps = member.toArray(new String[member.size()]);
                String members = Arrays.toString(temps);
                Log.d("정렬 잘되냐?",members);
                //friendsArrayListforRecyclerView.clear();
                member.clear();
                //분기 디비 검사 방이있으면 인텐드
                //방이없으면 -1
                int  branch;
                branch = mySQLiteOpenHelper.RoomisExist(members,myApplication.getId());
                if(branch!=-1){
                    Intent intent = new Intent(AddFriendsForEditActivity.this, ChatRoomActivity.class);
                    intent.putExtra("room_id",branch);
                    startActivity(intent);
                }//없으면 소켓 보냄 서비스에서 인텐트
                else {
                    //제이슨 생성
                    //{ "status": "edit_room_members","sentfrom": "sang","room_cnt": 5,"data": [ "yoon", "sang", "jo"]}
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("status","edit_room_members");
                        jsonObject.put("sentfrom",myApplication.getId());
                        jsonObject.put("room_cnt",room_id);
                        JSONArray jsonArray= new JSONArray(temps);
                        //jsonArray.put(myApplication.getId());
                        jsonObject.put("data",jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("제이슨으로 변환",jsonObject.toString());
                    final String msg = jsonObject.toString();
//                    String msg = "{ \"data\": [ \""+user_id+"\", \""+friend_id+"\" ], \"status\": \"mk_room\", \"sentfrom\": \""+user_id+"\" }";
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
                }

                //제이슨 만들어서 보내기

            }
        });
    }
    //listView 아답타
    //friend_list_item.xml
    public class ListViewAdapter extends BaseAdapter {
        // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
        private ArrayList<Friends> listViewItemList = new ArrayList<Friends>() ;
        private int itemLayout;
        // ListViewAdapter의 생성자
        public ListViewAdapter(ArrayList<Friends> items , int itemLayout){
            this.listViewItemList = items;
            this.itemLayout = itemLayout;
        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return listViewItemList.size() ;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(itemLayout, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            //ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.friend_id) ;
            //TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox) ;
            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            final Friends listViewItem = listViewItemList.get(position);
            for(int i=0;i<member_from_room.size();i++){
                if(listViewItem.getTitle().equals(member_from_room.get(i))) {
                    checkBox.setChecked(true);
                }
            }

            // 아이템 내 각 위젯에 데이터 반영
            //iconImageView.setImageDrawable(listViewItem.getIcon());
            titleTextView.setText(listViewItem.getTitle());
            //descTextView.setText(listViewItem.getDesc());
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context,"aaaas",Toast.LENGTH_SHORT).show();
                    if(checkBox.isChecked()==true){
                        //어레이에 넣기
                        //friends.setTitle(listViewItem.getTitle());
                        friendsArrayListforRecyclerView.add(listViewItem);
                        Log.d("사이즈", String.valueOf(friendsArrayList.size()));
                    }else{
                        //어레이에 빼기
                        for(int i=0;i<friendsArrayListforRecyclerView.size();i++){
                            if (listViewItem.getTitle().equals(friendsArrayListforRecyclerView.get(i).getTitle())){
                                friendsArrayListforRecyclerView.remove(i);
                            }
                        }
                        //friendsArrayListforRecyclerView.remove(friendsArrayListforRecyclerView.size()-1);
                    }
                    //리사이클뷰 실행
                    horizontalRecyclerAdapter = new HorizontalRecyclerAdapter(friendsArrayListforRecyclerView, R.layout.friend_list_item_hor);
                    recyclerView.setAdapter(horizontalRecyclerAdapter);
                }
            });

            return convertView;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position) ;
        }

        // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
//        public void addItem(Drawable icon, String title, String desc) {
//            ListViewItem item = new ListViewItem();
//
//            item.setIcon(icon);
//            item.setTitle(title);
//            item.setDesc(desc);
//
//            listViewItemList.add(item);
//        }
    }



    //클릭할떄마다
    //friends.setTitle("아이디나 이름");
    //friendsArrayList.add(friends);
//    horizontalRecyclerAdapter = new HorizontalRecyclerAdapter(friendsArrayList,R.layout.friend_list_item_hor);
//    recyclerView.setAdapter(horizontalRecyclerAdapter);
    //
    public class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.ViewHolder> {

        private ArrayList<Friends> friendsArrayList;
        private int itemLayout;
        /**
         * 생성자
         * @param items
         * @param itemLayout
         */
        public HorizontalRecyclerAdapter(ArrayList<Friends> items , int itemLayout){
            this.friendsArrayList = items;
            this.itemLayout = itemLayout;
        }
        /**
         * 레이아웃을 만들어서 Holer에 저장
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public HorizontalRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent,false);
            View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout,parent,false);
            return new ViewHolder(view);
        }
        /**
         * listView getView 를 대체
         * 넘겨 받은 데이터를 화면에 출력하는 역할
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(HorizontalRecyclerAdapter.ViewHolder holder, int position) {
            Friends item = friendsArrayList.get(position);
            holder.friend_id.setText(item.getTitle());
//            holder.text1.setText(item.getDetail());
//            holder.text2.setText(item.getDate());
            //holder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            //return 0;
            return friendsArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            public TextView friend_id/*,text1,text2*/;

            public ViewHolder(View itemView) {
                super(itemView);
                friend_id = (TextView) itemView.findViewById(R.id.friend_id);
//                text1 = (TextView) itemView.findViewById(R.id.text1);
//                text2 = (TextView) itemView.findViewById(R.id.text2);
            }
        }
    }
}
