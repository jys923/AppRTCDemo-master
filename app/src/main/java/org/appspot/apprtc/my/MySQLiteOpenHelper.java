package org.appspot.apprtc.my;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Administrator on 2016-12-16.
 * <p>
 *
 create table IF NOT EXISTS user(id integer primary key autoincrement,user_id text not null ,friend_id text not null,flag INTEGER DEFAULT 0 ,creat_date datetime DEFAULT (DATETIME('now')),modify_date datetime,UNIQUE(user_id,friend_id));
 * <p>
 * INSERT INTO user(user_id,friend_id) VALUES("yoon","Android");
 * INSERT INTO user(user_id,friend_id) VALUES("yoon","Android1");
 * INSERT INTO user(user_id,friend_id) VALUES("yoon","Android2");
 * <p>
 * <p>
 * SELECT * FROM user
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    public MySQLiteOpenHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 최초에 데이터베이스가 없을경우, 데이터베이스 생성을 위해 호출됨
        // 테이블 생성하는 코드를 작성한다
        String sql =
                "create table IF NOT EXISTS user(id integer primary key autoincrement," +
                "user_id text not null ,friend_id text not null,flag INTEGER DEFAULT 0 ," +
                "creat_date datetime DEFAULT (DATETIME('now')),modify_date datetime," +
                "UNIQUE(user_id,friend_id ) );";//중요 둘이 한번에 만족할때 만 걸림
        String sql2 =//create table IF NOT EXISTS room(id integer primary key autoincrement,room_id INTEGER not null ,room_name text ,members text not null,user_id text not null,show_cnt INTEGER not null DEFAULT 0 , last_sentence text,flag INTEGER DEFAULT 0 ,creat_date datetime DEFAULT(DATETIME('now')),modify_date datetime,UNIQUE(room_id,user_id));
                "create table IF NOT EXISTS room(id integer primary key autoincrement," +
                "room_id INTEGER not null ,room_name text ,members text not null,user_id text not null," +
                "show_cnt INTEGER not null DEFAULT 0 , last_sentence text,flag INTEGER DEFAULT 0 ," +
                "creat_date datetime DEFAULT(DATETIME('now')),modify_date datetime,UNIQUE(room_id,user_id));";//중요 둘이 한번에 만족할때 만 걸림
        String sql3 =//create table IF NOT EXISTS conversation(id integer primary key autoincrement,room_id INTEGER not null ,msg text not null,sent_from text not null,flag INTEGER DEFAULT 0 ,creat_date datetime DEFAULT (DATETIME('now')));
                "create table IF NOT EXISTS conversation(id integer primary key autoincrement," +
                "room_id INTEGER not null ,msg text not null,sent_from text not null,user_id text not null," +
                "flag INTEGER DEFAULT 0 ,creat_date datetime DEFAULT (DATETIME('now')));";
        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스의 버전이 바뀌었을 때 호출되는 콜백 메서드
        // 버전 바뀌었을 때 기존데이터베이스를 어떻게 변경할 것인지 작성한다
        // 각 버전의 변경 내용들을 버전마다 작성해야함
        String sql = "drop table user;"; // 테이블 드랍
        db.execSQL(sql);
        onCreate(db); // 다시 테이블 생성
    }

    public void insert_id(String user_id,String friend_id) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        //db.execSQL("INSERT INTO mytable VALUES(null, '" + item + "', " + price + ", '" + create_at + "');");
        db.execSQL("INSERT INTO user(user_id,friend_id) VALUES('" + user_id + "','" + friend_id + "');");
        db.close();
    }

    public void insert_room(int room_id,String members,String user_id) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT or replace INTO room(room_id,room_name,members,user_id,flag) VALUES(" + room_id + ",'" + members + "','" + members + "','" + user_id + "',0);");
        db.close();
    }
//INSERT or replace INTO room(room_id,room_name,members,user_id) VALUES("123",'"123"','"123"','"1"');
    public void insert_room_conversation_cnt(int room_id,String conversation,String user_id) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        //"update " + tableName + " set voca = '" + voca +"' where id = "+index +";";
        //db.execSQL("INSERT or replace INTO room(room_id,room_name,members,user_id,show_cnt,last_sentence) VALUES(" + room_id + ",'" + members + "','" + members + "','" + user_id + "');");
        //SET filedA='456', fieldB='ABC'
        //db.execSQL("update room set (room_id,room_name,members,user_id,show_cnt,last_sentence) VALUES(" + room_id + ",'" + members + "','" + members + "','" + user_id + "');");
        String msq ="update room set show_cnt = show_cnt+1,last_sentence = '"+conversation+"' where room_id=" + room_id + " and user_id='"+user_id+"' ;";
        db.execSQL(msq);
        db.close();
    }

    public void insert_conversation(int room_id,String msg,String sent_from,String user_id) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        //db.execSQL("INSERT INTO mytable VALUES(null, '" + item + "', " + price + ", '" + create_at + "');");
        Log.d("대하ㅘ1",msg);
        db.execSQL("INSERT INTO conversation(room_id,msg,sent_from,user_id) VALUES(" + room_id + ",'" + msg + "','" + sent_from +  "','" + user_id + "');");
        Log.d("대하ㅘ2",msg);
        db.close();
    }

    public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from user", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : user_id = "
                    + cursor.getString(1)
                    + ", friend_id = "
                    + cursor.getString(2)
                    //+ ", friend_id = "
                    //+ cursor.getString(4)
                    + "\n";
        }
        db.close();
        return str;
    }
    public int RoomisExist(String members,String user_id) {
        int result;
        SQLiteDatabase db = getReadableDatabase();
        //Cursor cursor = db.rawQuery("select * from room where members='" + members + "';", null);
        //Log.d("방찾기 쿼리 맞나?","select * from room where members='" + members + "' and user_id = '"+user_id+"' ;");
        //Cursor cursor = db.rawQuery("select * from room where members='" + members + "' and user_id = '"+user_id+"' ;", null);
        Cursor cursor = db.rawQuery("select * from room where members='" + members + "' and user_id = '"+user_id+"' and flag = 0 ;", null);
        Log.d("방이없으면 0", String.valueOf(cursor.getCount()));
        //방이없으면 -1
        if(cursor.getCount()==0){
            result=-1;
        }else {
            cursor.moveToFirst();
            result=cursor.getInt(cursor.getColumnIndex("room_id"));
        }
        db.close();
        return result;
    }
    public int FriendisExist(String user_id,String friend_id) {
        int result;
        SQLiteDatabase db = getReadableDatabase();
        //Cursor cursor = db.rawQuery("select * from room where members='" + members + "';", null);
        Log.d("친구 찾기 쿼리 맞나?","select * from user where user_id='" + user_id + "' and friend_id = '"+friend_id+"' ;");
        Cursor cursor = db.rawQuery("select * from user where user_id='" + user_id + "' and friend_id = '"+friend_id+"' ;", null);
        Log.d("친구 없으면 0", String.valueOf(cursor.getCount()));
        //친구 없으면 -1
        if(cursor.getCount()==0){
            result=-1;
        //친구 있으면 1
        }else {
            result=1;
        }
        db.close();
        return result;
    }
    public ArrayList<Friends> FriendListFormUserID(String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        //ArrayList<Friends> friendsArrayList = new ArrayList<Friends>();
        //Friends friends = new Friends();
        Log.d("11111111111",user_id);
        String aa = "select * from user where '" + user_id + "';";
        Log.d("selectr==",aa);
        Cursor cursor = db.rawQuery("select * from user where user_id='" + user_id + "';", null);
//        //친구 없으면 -1
//        if(cursor.getCount()==0){
//           result=-1;
//        }
        ArrayList<Friends> friendsArrayList = new ArrayList<Friends>();
        while(cursor.moveToNext()) {
            Log.d("들어가냐?2",cursor.getString(2));
            Friends friends = new Friends();
            friends.setTitle(cursor.getString(cursor.getColumnIndex("friend_id")));
            friendsArrayList.add(friends);
        }
        //Log.d("arrarylist:",friendsArrayList.get(0).getTitle());
//        for (int i = 0; i <= 8; i++) {
//            Log.d("좀되라1",friendsArrayList.get(i).getTitle());
//        }
        db.close();
        return friendsArrayList;
    }

    public ArrayList<String> FriendListFormRoomIDUserID(int room_id,String user_id) {
        JSONArray jsonArray = null;
        ArrayList<String> member = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select members from room where user_id='" + user_id + "' and room_id = " + room_id + ";", null);
        cursor.moveToFirst();
        try {
            jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex("members")));
            member = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                member.add(jsonArray.getString(i).trim());
            }
            Collections.sort(member);
            String[] temp = member.toArray(new String[member.size()]);
            String members = Arrays.toString(temp);
            Log.d("정렬 잘되냐?", members);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.close();
        return member;
    }

    public ArrayList<Friends> RoomListFormUserID(String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        //ArrayList<Friends> friendsArrayList = new ArrayList<Friends>();
        //Friends friends = new Friends();
        Log.d("11111111111",user_id);
        //String aa = "select * from user where '" + user_id + "';";
        //Log.d("selectr==",aa);
        Cursor cursor = db.rawQuery("select * from room where user_id='" + user_id+"' and flag = 0 ;", null);
        ArrayList<Friends> friendsArrayList = new ArrayList<Friends>();

        while(cursor.moveToNext()) {
            Log.d("들어가냐?2",cursor.getString(2));
            Friends friends = new Friends();
            friends.setTitle(cursor.getString(cursor.getColumnIndex("room_id")));
            friends.setDesc(cursor.getString(cursor.getColumnIndex("room_name")));
            friends.setLstStr(cursor.getString(cursor.getColumnIndex("last_sentence")));
            friendsArrayList.add(friends);
        }
        //Log.d("arrarylist:",friendsArrayList.get(0).getTitle());
//        for (int i = 0; i <= 8; i++) {
//            Log.d("좀되라1",friendsArrayList.get(i).getTitle());
//        }
        db.close();
        return friendsArrayList;
    }
//플래그 0인 것만
    public ArrayList<Sentence> ChatListFormRoomID(int room_id,String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from conversation where room_id=" + room_id + " and user_id='"+user_id+"' and flag = 0 ;", null);

        ArrayList<Sentence> sentenceArrayList = new ArrayList<Sentence>();

        while(cursor.moveToNext()) {
            Sentence sentence = new Sentence();
            sentence.setDetail(cursor.getString(cursor.getColumnIndex("msg")));
            sentence.setDate(cursor.getString(cursor.getColumnIndex("creat_date")));
            sentence.setSent_from (cursor.getString(cursor.getColumnIndex("sent_from")));
            sentenceArrayList.add(sentence);
        }
        db.close();
        return sentenceArrayList;
    }
    //대화디비에서 지우기 플래그 -1로 원래 0이었음
    public void remove_conversation(int room_id ,String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        Log.d("좀되라1","room_id:"+room_id+"   my_id:"+user_id);
        String msq ="update conversation set flag = -1 where room_id=" + room_id + " and user_id='"+user_id+"' ;";
        Log.d("좀되라2","msq:"+msq);
        db.execSQL(msq);
        db.close();
    }

    //방 디비에서 지우기 플래그 -1로 원래 0이었음
    public void remove_room(int room_id,String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("update room set flag = -1 where room_id=" + room_id + " and user_id='"+user_id+"' ;");
        db.close();
    }

    public Cursor SelectFormUserID2(String user_id) {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";
        //ArrayList<Friends> friendsArrayList = new ArrayList<Friends>();
        //Friends friends = new Friends();
        Log.d("11111111111",user_id);
        String aa = "select * from user where '" + user_id + "';";
        Log.d("selectr==",aa);
        Cursor cursor = db.rawQuery("select * from user where user_id='" + user_id + "';", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : user_id = "
                    + cursor.getString(1)
                    + ", friend_id = "
                    + cursor.getString(2)
                    //+ ", friend_id = "
                    //+ cursor.getString(4)
                    + "\n";
        }
        Log.d("커서되냐?==",str);
        return cursor;
    }
}
