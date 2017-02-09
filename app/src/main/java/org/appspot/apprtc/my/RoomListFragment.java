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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;

import org.appspot.apprtc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomListFragment extends Fragment {
    MySQLiteOpenHelper dbManager;
    BroadcastReceiver mReceiver;
    MyApplication myApplication;

//    @Override
//    public void onResume() {
//        super.onResume();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();
//    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view); 비슷한 문장
        View view = inflater.inflate(
                R.layout.fragment_room_list, container, false);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();

        final RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerview);
        myApplication = (MyApplication)getActivity().getApplicationContext();

        dbManager = new MySQLiteOpenHelper(getActivity().getApplicationContext(), "user.db", null, 1);
        final EditText insert_id = (EditText) view.findViewById(R.id.insert_id);
        Button insert_id_btn = (Button) view.findViewById(R.id.insert_id_btn);
        insert_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // insert into 테이블명 values (값, 값, 값...);
                String user_id = "sang";
                String friend_id = insert_id.getText().toString();
                //입력전에 같은 값있는지 체크??
                //같은 값들어가면 강종
                dbManager.insert_id(user_id,friend_id);
                Log.d("디비삽입확인",dbManager.PrintData());
                insert_id.setText(null);
                //rv.notifyDataSetChanged();
                //notifyItemRangeInserted(0, list.size());
                rv.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),dbManager.RoomListFormUserID(myApplication.getId())));
            }
        });
        setupRecyclerView(rv);
        //브로드캐스트의 액션을 등록하기 위한 인텐트 필터
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("org.appspot.apprtc.my.GET_ROOMS_LIST");
        //동적 리시버 구현
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sendString = intent.getExtras().getString("sendString");
                Log.d("방목록 브로드리시버", sendString);
                setupRecyclerView(rv);
            }
        };
        //Receiver 등록
        //registerReceiver(mReceiver, intentfilter);
        getActivity().registerReceiver(mReceiver, intentfilter);
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
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),dbManager.RoomListFormUserID(myApplication.getId())));

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
            public final TextView lastSentence;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                lastSentence = (TextView) view.findViewById(R.id.lastSentence);
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
            mValues = items;
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
            holder.lastSentence.setText(mValues.get(position).getLstStr());
            holder.mBoundString = mValues.get(position).getTitle();
            holder.mTextView.setText(mValues.get(position).getDesc());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    //Intent intent = new Intent(context, CheeseDetailActivity.class);
                    //intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
                    Intent intent = new Intent(context, ChatRoomActivity.class);
                    //intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
                    Log.d("넘기는 값",holder.mBoundString);
                    intent.putExtra("room_id", Integer.valueOf(holder.mBoundString));

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
}
