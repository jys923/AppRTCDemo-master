package org.appspot.apprtc.my;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.appspot.apprtc.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {
    EditText join_id;
    String id;
    EditText password;
    EditText phone;
    EditText email;
    Button overlap;
    Button cancle;
    Button join;
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        myApplication = (MyApplication) getApplicationContext();
        join_id = (EditText) findViewById(R.id.join_id);
        password = (EditText) findViewById(R.id.join_password);
        phone = (EditText) findViewById(R.id.join_phone);
        email = (EditText) findViewById(R.id.join_Email);
        overlap = (Button) findViewById(R.id.join_overlap);
        cancle = (Button) findViewById(R.id.join_cancle);
        join = (Button) findViewById(R.id.join_join);
        overlap.setOnClickListener(this);
        cancle.setOnClickListener(this);
        join.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join_overlap:
                //아이디 중복 검사
                String url = "exist_id.php?id=";
                id = join_id.getText().toString().trim();
                Log.d("넘기는값", url+id);
                new HttpUtil().execute(url+id);
                break;
            case R.id.join_cancle:
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.join_join:
                //join.php?id=jo&pw=1234&email=1234@naver.com&phone=010123456789&Friend_List=[jo,sang,yoon]
                Intent intent2 = new Intent(JoinActivity.this, LoginActivity.class);
                intent2.putExtra("user_id", myApplication.getId());
                String last = null;
                String url2 = "join.php?id=";
                id = join_id.getText().toString().trim();
                last = url2+id+"&pw="+password.getText().toString().trim()+"&email="+email.getText().toString().trim()+"&phone="+phone.getText().toString().trim()+"&Friend_List=";
                Log.d("넘긴다...",last);
                new HttpUtil2().execute(last);
                startActivity(intent2);
                finish();
                break;
        }
    }

    public class HttpUtil2 extends AsyncTask<String, Void, Void> {
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
            if (jsonHtml.toString().trim().equals("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "가입성공", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "아이디중복", Toast.LENGTH_SHORT).show();
                        join_id.setText("");
                    }
                });
            }
            return null;
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
            if (jsonHtml.toString().trim().equals("false")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "아이디없음", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "아이디중복", Toast.LENGTH_SHORT).show();
                        join_id.setText("");
                    }
                });
            }
            return null;
        }
    }
}
