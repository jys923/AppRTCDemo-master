package org.appspot.apprtc.my;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.appspot.apprtc.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private RecyclerView recyclerView;
    ArrayList<Sentence> sentenceArrayList = new ArrayList<Sentence>();
    MySQLiteOpenHelper dbManager;
    MyRecyclerAdapter myRecyclerAdapter;
    Intent intent;
    int room_id;
    String user_id;
    MyApplication myApplication;
    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    BroadcastReceiver mReceiver;
    Button button;
    Button exit2;
    Button edit2;
    Button photo;
    EditText editText;
    Socket socket;
    DataOutputStream out;
    ListView roomMemberListView;
    ArrayList<String> list;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView imageView;
    //private int id_view;
    private String absoultePath;

    @Override
    protected void onStop() {
        super.onStop();
        //등록된 Receiver는 반드시 해제 해주어야 한다.
        //unregisterReceiver(mReceiver);
    }

    //화면보고있을때만 갱신
    @Override
    protected void onResume() {
        super.onResume();
        socket = myApplication.getSocket();
        try {
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //브로드캐스트의 액션을 등록하기 위한 인텐트 필터
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("org.appspot.apprtc.my.SEND_BROAD_CAST");
        //동적 리시버 구현
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sendString = intent.getExtras().getString("sendString");
                Log.d(TAG, sendString);
                initData();
                //recyclerView.smoothScrollToPosition(sentenceArrayList.size());
            }
        };
        //Receiver 등록
        registerReceiver(mReceiver, intentfilter);
        myApplication.setChatRoomBR(true);
        //recyclerView.smoothScrollToPosition(sentenceArrayList.size());
    }

    //안보이면 갱신안함
    @Override
    protected void onPause() {
        super.onPause();
        //등록된 Receiver는 반드시 해제 해주어야 한다.
        unregisterReceiver(mReceiver);
        myApplication.setChatRoomBR(false);
//        try {
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatRoomActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    Toolbar toolbar;
    ActionBar ab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //상단바 셋팅 서랍장있는 바
        setSupportActionBar(toolbar);

        ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//        ab.setTitle(myApplication.getId());
        //좌측 3줄 서랍장 버튼
//        ab.setDisplayHomeAsUpEnabled(true);
        //위로 올릴떄 보임
        imageView = (ImageView) findViewById(R.id.imageView);
        photo = (Button) findViewById(R.id.photo);
        photo.setOnClickListener(this);
        edit2 = (Button) findViewById(R.id.edit2);
        edit2.setOnClickListener(this);
        exit2 = (Button) findViewById(R.id.exit2);
        exit2.setOnClickListener(this);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        myApplication = (MyApplication) getApplicationContext();
        user_id = myApplication.getId();
        //ClientSocketService.set
//        initLayout();
//        initData();
        //intent = new Intent(ChatRoomActivity.this , ChatRoomActivity.class);
        intent = getIntent();
        //int room_id = Integer.parseInt(intent.getStringExtra("room_id"));
        room_id = intent.getExtras().getInt("room_id");
        Log.d("받은 값", String.valueOf(room_id));
        //디비검색
        editText = (EditText) findViewById(R.id.editText);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        dbManager = new MySQLiteOpenHelper(getApplicationContext(), "user.db", null, 1);
        sentenceArrayList = dbManager.ChatListFormRoomID(room_id,myApplication.getId());
        myRecyclerAdapter = new MyRecyclerAdapter(sentenceArrayList, R.layout.chat_list_item);
        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setSelection(myRecyclerAdapter.getItemCount() - 1);
//        recyclerView.getLayoutManager().scrollToPosition(myRecyclerAdapter.getItemCount());
        ((LinearLayoutManager)recyclerView.getLayoutManager()).setStackFromEnd(true);

        list = dbManager.FriendListFormRoomIDUserID(room_id,myApplication.getId());
        String[] title = list.toArray(new String[list.size()]);
        ab.setTitle(Arrays.toString(title));
        //자기자신 삭제
        list.remove(list.indexOf(myApplication.getId()));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        roomMemberListView  = (ListView) findViewById(R.id.friendList);
        // ListView에 각각의 아이템표시를 제어하는 Adapter를 설정
        roomMemberListView.setAdapter(adapter);
        roomMemberListView.setOnItemClickListener(this);
        /*roomMemberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });*/

    }
    /**
     * 레이아웃 초기화
     */
    private void initLayout() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    /**
     * 데이터 초기화
     */
    private void initData() {
        ArrayList<Sentence> sentenceArrayList = new ArrayList<Sentence>();
//        for (int i =0; i<20; i ++){
//
//            Sentence sentence = new Sentence();
//            sentence.setDetail("가나다ㅏㅇ마ㅏㅁ");
//            sentence.setDate("2016-01-01");
//            //sentence.setImage(R.drawable.ic_launcher);
//            sentenceArrayList.add(sentence);
//        }
        sentenceArrayList = dbManager.ChatListFormRoomID(room_id,myApplication.getId());
        myRecyclerAdapter = new MyRecyclerAdapter(sentenceArrayList, R.layout.chat_list_item);
        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.getLayoutManager().scrollToPosition(myRecyclerAdapter.getItemCount());

        ((LinearLayoutManager)recyclerView.getLayoutManager()).setStackFromEnd(true);
        list = dbManager.FriendListFormRoomIDUserID(room_id,myApplication.getId());
        String[] title = list.toArray(new String[list.size()]);
        ab.setTitle(Arrays.toString(title));
        //자기자신 삭제
        list.remove(list.indexOf(myApplication.getId()));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        roomMemberListView  = (ListView) findViewById(R.id.friendList);
        // ListView에 각각의 아이템표시를 제어하는 Adapter를 설정
        roomMemberListView.setAdapter(adapter);
        roomMemberListView.setOnItemClickListener(this);
    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
        private ArrayList<Sentence> sentenceArrayList;
        private int itemLayout;

        /**
         * 생성자
         *
         * @param items
         * @param itemLayout
         */
        public MyRecyclerAdapter(ArrayList<Sentence> items, int itemLayout) {
            this.sentenceArrayList = items;
            this.itemLayout = itemLayout;
        }

        /**
         * 레이아웃을 만들어서 Holer에 저장
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent,false);
            View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
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
        public void onBindViewHolder(MyRecyclerAdapter.ViewHolder holder, int position) {
            final Sentence item = sentenceArrayList.get(position);
            holder.text0.setText(item.getSent_from());
            //if "http://" + myApplication.getIphttp() + ":" + myApplication.getPorthttp() + "/chatrtc/albums/pic" + String.valueOf(params[0]);
            holder.text2.setText(item.getDate());
            //holder.itemView.setTag(item);
            //holder.text1.setText(item.getDetail());
            if (item.getDetail().contains("http://"+myApplication.getIphttp())){
                //이미지 썸네일에 받기
                final String path = item.getDetail();
                StringTokenizer values = new StringTokenizer(path, "/" );
                String[] arr=new String[values.countTokens()];
                for( int x = 0; values.hasMoreElements(); x++ ){
                    arr[x]=values.nextToken();
                    System.out.println( "문자(열) " + x + " : " + arr[x]);
                }
                String path2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/Thumbnails/"+path.substring(path.lastIndexOf("/"),path.length());
                holder.imageView2.setImageURI(Uri.parse(path2));
                holder.imageView2.setMaxHeight(200);
                holder.imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(),/*item.getDetail()*/path,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChatRoomActivity.this,PhotoActivity.class);
                        //방으로 돌아올떄 사용
                        intent.putExtra("room_id",room_id);
                        intent.putExtra("picName",Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads"+path.substring(path.lastIndexOf("/"),path.length()));
                        startActivity(intent);
                    }
                });
            }else{
                holder.text1.setText(item.getDetail());
            }
            //내가 쓴 글이면
            if(item.getSent_from().equals(myApplication.getId())) {
                // 9 패치 이미지로 채팅 버블을 출력
                //holder.chatItem.setBackground(getResources().getDrawable(R.drawable.popup_inline_error_above));
                //holder.chatItem.setBackgroundDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.popup_inline_error_above));
                holder.chatItem.setBackgroundResource(R.drawable.popup_inline_error_above);
                holder.chatItem.setGravity(Gravity.RIGHT);
                //((LinearLayout.LayoutParams) holder.chatItem.getLayoutParams()).gravity = Gravity.RIGHT;
            }else {//남이 쓴글이면
                holder.chatItem.setBackgroundResource(R.drawable.popup_inline_error);
                holder.chatItem.setGravity(Gravity.LEFT);
                //((LinearLayout.LayoutParams) holder.chatItem.getLayoutParams()).gravity = Gravity.LEFT;
            }
        }

        @Override
        public int getItemCount() {
            //return 0;
            return sentenceArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout chatItem;
            public TextView text0, text1, text2;
            public ImageView imageView2;
            public ViewHolder(View itemView) {
                super(itemView);
                chatItem = (LinearLayout) itemView.findViewById(R.id.chatItem);
                text0 = (TextView) itemView.findViewById(R.id.text0);
                text1 = (TextView) itemView.findViewById(R.id.text1);
                text2 = (TextView) itemView.findViewById(R.id.text2);
                imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String friend_id = (String)adapterView.getAdapter().getItem(i);
        //Toast.makeText(getApplicationContext(), tv, Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(ChatRoomActivity.this,FriendDetailActivity.class);
        intent.putExtra("friend_id",friend_id);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        //제이슨 생성
        //JSONObject jsonObject = new JSONObject();
        final String msg;
        Intent intent;
        switch (v.getId()) {
            case R.id.button:
                //{ "status": "send_msg", "room_cnt": 0, "sentfrom": "yoon" , "data": "가나다아" }
                //제이슨 생성
                JSONObject jsonObject = new JSONObject();
                //{ "status": "send_msg", "room_cnt": 0, "sentfrom": "yoon" , "data": "가나다아" }
                try {
                    jsonObject.put("status", "send_msg");
                    jsonObject.put("room_cnt", room_id);
                    jsonObject.put("sentfrom", myApplication.getId());
                    jsonObject.put("data", editText.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("제이슨으로 변환", jsonObject.toString());
                msg = jsonObject.toString();
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
                editText.setText("");
                break;

            case R.id.exit2:
                //{ "status": "room_member_out","sentfrom": "yoon","room_cnt": 11}
                msg = "{ \"status\": \"room_member_out\",\"sentfrom\": \""+myApplication.getId()+"\",\"room_cnt\": "+room_id+"}";
                Log.d("제이슨으로 변환", msg);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                           // out.write(msg.getBytes("UTF-8"));
                            Log.d("제이슨으로 변환aaa", msg);
                            myApplication.getOut().writeUTF(msg);
                            //Intent intent = new Intent(ChatRoomActivity.this,MainActivity.class);
                            //startActivity(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;

            case R.id.edit2:
                //콜백으로 하는 방법도 있을까?
                //친구리스트를 친구들이 이미 클릭되어있게
                intent = new Intent(ChatRoomActivity.this,AddFriendsForEditActivity.class);
                intent.putExtra("room_id",room_id);
                startActivity(intent);
                break;
            case R.id.photo:
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhotoAction();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakeAlbumAction();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("업로드할 이미지 선택")
//                        .setPositiveButton("사진촬영", cameraListener)
                        .setPositiveButton("앨범선택", albumListener)
//                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
        }
    }
    /**
     * 카메라에서 사진 촬영
     */
    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                //Uri로 부터 절대경로 구함
                //Log.d("절대경로2333",getPath(mImageCaptureUri));
                //editText.setText(getPath(mImageCaptureUri));
//                File imgFile = new File(getPath(mImageCaptureUri));
//                if(imgFile.exists()){
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    imageView.setImageBitmap(myBitmap);
//                }
//                String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
//                Log.d("절대경로",dir);
                //절대 경로의 파일을 파일명 새로 바꿔서 보냄 Chat_밀리.jpeg
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile(getPath(mImageCaptureUri));
                    }
                }).start();
                //uploadFile(mImageCaptureUri.getPath());
                break;
            }
            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                // CROP할 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
                intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동
                break;
            }
            case CROP_FROM_iMAGE: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                if (resultCode != RESULT_OK) {
                    return;
                }
                final Bundle extras = data.getExtras();
                // CROP된 이미지를 저장하기 위한 FILE 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/SmartWheel/" + System.currentTimeMillis() + ".jpg";
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
                    imageView.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    storeCropImage(photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    absoultePath = filePath;
                    break;
                }
                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }
    /*
    * Bitmap을 저장하는 부분
    */
    private void storeCropImage(Bitmap bitmap, String filePath) {
        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식이다.
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
        File directory_SmartWheel = new File(dirPath);

        if(!directory_SmartWheel.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            directory_SmartWheel.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {

            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int uploadFile(String sourceFileUri) {
        StringBuilder jsonHtml = null;
        int serverResponseCode = 0;
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {//파일아닐때 예외처리
//            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :"+ fileName + "" + fileName);
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    messageText.setText("Source File not exist :"
//                            + uploadFilePath + "" + uploadFileName);
//                }
//            });

            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                //URL url = new URL(upLoadServerUri);
                //http://192.168.0.38:47271/chatrtc/login.php?id=yoon
                URL url = new URL("http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/UploadToServer.php");
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
//                                    + uploadFileName;
//                            messageText.setText(msg);
//                            Toast.makeText(MainActivity.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    /*
                    *
                    * */
                    jsonHtml = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        jsonHtml.append(line + "\n");
                    }
                    Log.d("결과받기", jsonHtml.toString());
                    br.close();
                }
                conn.disconnect();
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
//                dialog.dismiss();
                //conn.disconnect();
                //
                // contains를 이용한 방법(true, false 반환)
//                if(jsonHtml.toString().contains("success")){
//                    System.out.println("문자열 있음!");
//                }
//                else{
//                    System.out.println("문자열 없음!");
//                }
                //jsonHtml.toString()
//                StringTokenizer st = new StringTokenizer( jsonHtml.toString(), "/" );
//                String[] arr=new String[st.countTokens()];
//                int i=0;
//                while(st.hasMoreTokens()){
//                    arr[i]=st.nextToken();
//                    i++;
//                }
//                for(int j=0; j<st.countTokens(); j++){
//                    System.out.println("문자(열) "+arr[j]);
//                }
                String path = jsonHtml.toString();
                StringTokenizer values = new StringTokenizer( jsonHtml.toString(), "/" );
                String[] arr=new String[values.countTokens()];
                for( int x = 0; values.hasMoreElements(); x++ ){
                    arr[x]=values.nextToken();
                    System.out.println( "문자(열) " + x + " : " + arr[x]);
                }
                //http://127.0.0.1:47271/chatrtc/uploads/20170107130314295600.jpg
                //URL url2 = new URL("http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/"+arr[2]);
                JSONObject jsonObject = new JSONObject();
                //{ "status": "send_msg", "room_cnt": 0, "sentfrom": "yoon" , "data": "가나다아" }
                try {
                    jsonObject.put("status", "send_msg");
                    jsonObject.put("room_cnt", room_id);
                    jsonObject.put("sentfrom", myApplication.getId());
                    jsonObject.put("data", "http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/thumbnails/"+path.substring(path.lastIndexOf("/"),path.length()));
//                    jsonObject.put("data", "http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/thumbnails/"+arr[3]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("제이슨으로 변환", jsonObject.toString());
                String msg2 = jsonObject.toString();
                myApplication.getOut().writeUTF(msg2);

//                Log.d("서버파일받기", "파일 있을때");
//                URL url2 = new URL("http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/"+arr[2]);
//                HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
//                conn2.setDoInput(true);
//                conn2.connect();
//                InputStream is = conn2.getInputStream();
//                Bitmap bitmap = BitmapFactory.decodeStream(is);//비트맵으로 받아옴
//                //conn2.disconnect();
//                String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatrtc/downloads/thumbnails/"+arr[2];
//                Log.d("서버파일받기", temp);
//                File file2 = new File(temp);
//                if (!file2.exists()) {
//                    //파일생성
//                    //file2.mkdirs();//폴더생성 서비스로 빼자
//                    FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/chatrtc/download/thumbnails/"+arr[2]);
//                    //압축해서 실제 파일만들기
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    out.close();
//                    /**로컬디비에 추가**/
//                    //엘범 없음
//                    Log.d("서버파일받기", "잘됨");
//                    //제이슨으로 msg보내기
//                    //"http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/"+arr[2]
//                    JSONObject jsonObject = new JSONObject();
//                    //{ "status": "send_msg", "room_cnt": 0, "sentfrom": "yoon" , "data": "가나다아" }
//                    try {
//                        jsonObject.put("status", "send_msg");
//                        jsonObject.put("room_cnt", room_id);
//                        jsonObject.put("sentfrom", myApplication.getId());
//                        jsonObject.put("data", "http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/"+arr[2]);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("제이슨으로 변환", jsonObject.toString());
//                    String msg2 = jsonObject.toString();
//                    myApplication.getOut().writeUTF(msg2);
//                }
            } catch (MalformedURLException ex) {
//                dialog.dismiss();
                ex.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("MalformedURLException Exception : check script url.");
//                        Toast.makeText(MainActivity.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                //  dialog.dismiss();
                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("Got Exception : see logcat ");
//                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
                Log.e("Upload Exception", "Exception : " + e.getMessage(), e);
                //Log.e("Upload file to server Exception", "Exception : "+ e.getMessage(), e);
            }
//            dialog.dismiss();
            return serverResponseCode;
        } // End else block
    }
}
