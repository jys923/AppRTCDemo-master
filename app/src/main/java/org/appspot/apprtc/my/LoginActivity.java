package org.appspot.apprtc.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.appspot.apprtc.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    MySQLiteOpenHelper dbManager;
    EditText ip;
    EditText port;
    EditText ipHttp;
    EditText portHttp;
    EditText id;
    EditText password;
    Button login;
    Button join;
    MyApplication myApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Hello jni = new Hello();
//        int num = jni.GetInt();
//        Log.d("엔디케이","엔디케이:"+num);
        ip = (EditText) findViewById(R.id.ip);
        port = (EditText) findViewById(R.id.port);
        ipHttp =  (EditText) findViewById(R.id.ipHttp);
        portHttp = (EditText) findViewById(R.id.portHttp);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        join = (Button) findViewById(R.id.join);

        login.setOnClickListener(this);
        join.setOnClickListener(this);
        myApplication = (MyApplication)getApplicationContext();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                myApplication.setIp(ip.getText().toString().trim());
                myApplication.setPort(Integer.parseInt(port.getText().toString().trim()));
                myApplication.setIphttp(ipHttp.getText().toString().trim());
                myApplication.setPorthttp(Integer.parseInt(portHttp.getText().toString().trim()));
                myApplication.setId(id.getText().toString().trim());
                //String msg = myApplication.getIp()+myApplication.getPort()+myApplication.getId();
                //Log.d("입력확인",msg);
                //myApplication.setIp();
//                Intent intent2 = new Intent(this, ClientSocketService.class);
//                startService(intent2);
//                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                intent.putExtra("user_id",myApplication.getId());
//                startActivity(intent);
                dbManager = new MySQLiteOpenHelper(this.getBaseContext() , "user.db", null, 1);
                SharedPreferences test = getSharedPreferences("test", MODE_PRIVATE);
                if (test.getInt(myApplication.getId(), -1)!=1){
                    SharedPreferences.Editor editor = test.edit();
                    editor.putInt(myApplication.getId(), 0); //First라는 key값으로 infoFirst 데이터를 저장한다.
                    editor.apply();//완료한다.
                }

                String temp_pw=password.getText().toString().trim();
                String url = "login.php?id=";
                String temp_id = myApplication.getId();
                Log.d("넘기는값", url+temp_id);
                new HttpUtil().execute(url+temp_id,temp_pw);
                break;
            case R.id.join:
                Intent intent2 = new Intent(LoginActivity.this,JoinActivity.class);
                //intent2.putExtra("user_id",myApplication.getId());
                startActivity(intent2);
                break;
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
                String url = "http://" + myApplication.getIphttp() + ":" + myApplication.getPorthttp() + "/chatrtc/" + String.valueOf(params[0]);
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
                    Log.d("받은값", jsonHtml.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * 보내고
             * -------------
             * 받고
             * **/
            try {
                JSONObject jsonObject = new JSONObject(jsonHtml.toString().trim());
                if(jsonObject.getString("pw").equals(String.valueOf(params[1]))){
                    Log.d("아이디:pw맞음",String.valueOf(params[1]));
                    //쉐어드프리퍼런스 플래그 검사해서 한번만 디비에 넣자
                    SharedPreferences test = getSharedPreferences("test", MODE_PRIVATE);
                    int flag = test.getInt(myApplication.getId(), -1);
                    if (flag==0){
                        //제이슨 풀기
                        Log.d("디비삽입 한번만",String.valueOf(params[1]));
                        if (!jsonObject.getString("Friend_List").equals("")){
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("Friend_List"));

                            for(int i=0; i < jsonArray.length(); i++){
                                dbManager.insert_id(myApplication.getId(),jsonArray.getString(i).trim());
                            }
                            //디비삽입
                            //갱신하고 플래그 바꿔줌
                            SharedPreferences.Editor editor = test.edit();
                            editor.putInt(myApplication.getId(), 1);
                            editor.apply();//완료한다.
                        }
                    }
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("user_id",myApplication.getId());
                    startActivity(intent);
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "아이디:비번안맞음", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "아이디 안맞음", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }
}
