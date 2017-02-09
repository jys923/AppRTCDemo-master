/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.appspot.apprtc.my;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.appspot.apprtc.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CheeseListFragment extends Fragment {
    MySQLiteOpenHelper dbManager;
    MyApplication myApplication;// = (MyApplication)getApplicationContext();
    String user_id;
    String friend_id;
    RecyclerView rv;
    ArrayList<String> member = new ArrayList<String>();
    Handler handler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view); 비슷한 문장
        View view = inflater.inflate(
                R.layout.fragment_cheese_list, container, false);
        myApplication = (MyApplication)getActivity().getApplicationContext();
        handler = new Handler();
        user_id = myApplication.getId();

        rv = (RecyclerView) view.findViewById(R.id.recyclerview);
        dbManager = new MySQLiteOpenHelper(getActivity().getApplicationContext(), "user.db", null, 1);
        final EditText insert_id = (EditText) view.findViewById(R.id.insert_id);
        Button insert_id_btn = (Button) view.findViewById(R.id.insert_id_btn);
        insert_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // insert into 테이블명 values (값, 값, 값...);
                user_id = myApplication.getId();
                friend_id = insert_id.getText().toString().trim();
                if(user_id.equals(friend_id)){
                    Toast.makeText(getActivity(), "자기자신은 추가 불가", Toast.LENGTH_SHORT).show();
                }
                //삽입전에 같은 값있는지 로컬 체크??//아이디 중복 검사
                else if(dbManager.FriendisExist(user_id,friend_id)==-1){
                    //친구 없으면 -1
                    //삽입전에 맞는 값인지 레디스 체크??
                    String url = "exist_id.php?id=";
                    Log.d("넘기는값", url+friend_id);
                    //new HttpUtil().execute(url+friend_id);
                    //쓰레드 종료까지 대기
                    Httpthreads httpthreads=new Httpthreads(url+friend_id);
                    httpthreads.start();
                    try {
                        httpthreads.join();
                        String temp;
                        ArrayList<Friends> friendsArrayList = dbManager.FriendListFormUserID(user_id);
                        if(friendsArrayList.size()!=0) {
                            for (int i = 0; i < friendsArrayList.size(); i++) {
                                temp = friendsArrayList.get(i).getTitle();
                                member.add(temp);
                            }
                        }
                        //내아이디
                        //member.add(friend_id);
                        //member.add(myApplication.getId());
                        Collections.sort(member);
                        String[] temps = member.toArray(new String[member.size()]);
                        String members = Arrays.toString(temps);
                        Log.d("정렬 잘되냐?",members);
                        //friendsArrayListforRecyclerView.clear();
                        member.clear();
                        friendsArrayList.clear();
                        //레디스 친구리스트에 삽입
                        //localhost:47271/chatrtc/add_friend.php?id=sang&Friend_List=[aa,jo,sang,yoon]
                        url =  "add_friend.php?id="+user_id+"&Friend_List="+members;
                        //new HttpUtil().execute(url);
                        new Httpthreads(url).start();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else{//1이면
                    Toast.makeText(getActivity(), "이미 있는 친구", Toast.LENGTH_SHORT).show();
                }
                //같은 값들어가면 강종
                //dbManager.insert_id(user_id,friend_id);
                //Log.d("디비삽입확인",dbManager.PrintData());
                insert_id.setText(null);
                //rv.notifyDataSetChanged();
                //notifyItemRangeInserted(0, list.size());
                rv.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),dbManager.FriendListFormUserID(myApplication.getId())));
            }
        });
        setupRecyclerView(rv);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        //GridLayoutManager : 여러분의 사진첩 같은 격자형 리스트를 만들 수 있습니다.
        //LinearLayoutManager : 리사이클러 뷰에서 가장 많이 쓰이는 레이아웃으로 수평, 수직 스크롤을 제공하는 리스트를 만들 수 있습니다.
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
/*        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                getRandomSublist(Cheeses.sCheeseStrings, 30)));*/
        //디비갱신
        dbManager = new MySQLiteOpenHelper(getActivity().getApplicationContext(), "user.db", null, 1);
        //Cursor cursor = dbManager.SelectFormUserID("yoon");
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),dbManager.FriendListFormUserID(myApplication.getId())));

        //Log.d("디비전체",helper.PrintData());
        //adapter.notifyDataSetChanged();
    }

    private ArrayList<Friends> getFriendsList(Cursor cursor) {
        ArrayList<Friends> friendsArrayList = new ArrayList<Friends>();
        Friends friends = new Friends();
        while(cursor.moveToNext()) {
            friends.setTitle(cursor.getString(2));
            friendsArrayList.add(friends);
        }
        //Log.d("arrarylist:",friendsArrayList.get(0).getTitle());
        return friendsArrayList;
    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private ArrayList<Friends> mValues;
        //뷰홀더
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public Friends getValueAt(int position) {
            return mValues.get(position);
        }
        //생성자
        public SimpleStringRecyclerViewAdapter(Context context, ArrayList<Friends> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = (ArrayList<Friends>) items.clone();
        }
        //뷰 홀더를 생성하고 뷰를 붙여주는 부분입니다.
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }
        //재활용 되는 뷰가 호출하여 실행되는 메소드,
        //뷰 홀더를 전달하고 어댑터는 position 의 데이터를 결합시킵니다.
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mValues.get(position).getTitle();
            holder.mTextView.setText(mValues.get(position).getTitle());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    //Intent intent = new Intent(context, CheeseDetailActivity.class);
                    //intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
                    Intent intent = new Intent(context, FriendDetailActivity.class);
                    intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
                    intent.putExtra("friend_id", holder.mBoundString);

                    context.startActivity(intent);
                }
            });

            Glide.with(holder.mImageView.getContext())
                    .load(Cheeses.getRandomCheeseDrawable())
                    .fitCenter()
                    .into(holder.mImageView);
        }
        //데이터의 개수 반환
        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

    public class HttpUtil extends AsyncTask<String, Void, Void> {
        StringBuilder jsonHtml = new StringBuilder();

        @Override
        public Void doInBackground(String... params) {
            try {
                //http://localhost:47271/chatrtc/exist_id.php?id=yoon
                //http://192.168.0.38:47271/chatrtc/exist_id.php?id=yoon
                //Log.d("넘기는값", String.valueOf(params[0]));
                String url = "http://" + myApplication.getIp() + ":" + myApplication.getPorthttp() + "/chatrtc/" + String.valueOf(params[0]);
                Log.d("넘기는값", url);
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if (conn != null) {
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    //conn.setRequestProperty("Content-Type","application/json");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    //conn.disconnect();
                    Log.d("회원가입", jsonHtml.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * 보내고
             * -------------
             * 받고
             * **/
            //show(jsonHtml.toString());
            //아이디 있으면 맞는 아니디 로컬 디비 삽입
            if (jsonHtml.toString().trim().equals("true")) {
                dbManager.insert_id(user_id,friend_id);

                //Toast.makeText(getActivity(), "맞는 아이디 디비 삽입", Toast.LENGTH_SHORT).show();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "아이디없음", Toast.LENGTH_SHORT).show();
//                    }
//                });
            } else {
                //Toast.makeText(getActivity(), "없는 아이디 디비 삽입", Toast.LENGTH_SHORT).show();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "아이디중복", Toast.LENGTH_SHORT).show();
//                        join_id.setText("");
//                    }
//                });
            }
            if (jsonHtml.toString().trim().equals("1")) {
                Log.d("레디스 친구 리스트 삽입 성공",jsonHtml.toString().trim());
                //Toast.makeText(getActivity(), "레디스 친구 리스트 삽입 성공", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public class Httpthreads extends Thread {
        StringBuilder jsonHtml = new StringBuilder();
        String value;
        public Httpthreads(String getValue) {
            this.value = getValue;
            }
        @Override
        public void run() {
            try {
                //http://localhost:47271/chatrtc/exist_id.php?id=yoon
                //http://192.168.0.38:47271/chatrtc/exist_id.php?id=yoon
                //Log.d("넘기는값", String.valueOf(params[0]));
                String url = "http://" + myApplication.getIphttp() + ":" + myApplication.getPorthttp() + "/chatrtc/"+value;
                Log.d("넘기는값", url);
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if (conn != null) {
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    //conn.setRequestProperty("Content-Type","application/json");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                    Log.d("회원가입", jsonHtml.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * 보내고
             * -------------
             * 받고
             * **/
            //show(jsonHtml.toString());
            //아이디 있으면 맞는 아니디 로컬 디비 삽입
            if (jsonHtml.toString().trim().equals("true")) {
                dbManager.insert_id(user_id,friend_id);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "있는 아이디", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(getActivity(), "맞는 아이디 디비 삽입", Toast.LENGTH_SHORT).show();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "아이디없음", Toast.LENGTH_SHORT).show();
//                    }
//                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "없는 아이디", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(getActivity(), "없는 아이디 디비 삽입", Toast.LENGTH_SHORT).show();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "아이디중복", Toast.LENGTH_SHORT).show();
//                        join_id.setText("");
//                    }
//                });
            }
            if (jsonHtml.toString().trim().equals("1")) {
                Log.d("레디스 친구 리스트 삽입 성공",jsonHtml.toString().trim());
                //Toast.makeText(getActivity(), "레디스 친구 리스트 삽입 성공", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
