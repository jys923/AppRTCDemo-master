package org.appspot.apprtc.my;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientSocketService extends Service {
    static MySQLiteOpenHelper dbManager;
    static String user_id;
    MyApplication myApp;
    public ClientSocketService() {
    }


    class ClientReceiver extends Thread {

        Socket socket;
        //BufferedInputStream in;
        DataInputStream in;
        DataOutputStream out;
        String string;
        //MySQLiteOpenHelper dbManager;

        ClientReceiver(Socket socket) {
            this.socket = socket;
            try {
                //in = new BufferedInputStream(socket.getInputStream());
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                myApp.setOut(out);
            } catch (IOException e) {
            }
        }
        public void run() {
            //int i = 0 ;
            byte[] buff= new byte[1024];
            StringBuffer strbuff = new StringBuffer(1024);
            String remake = null;
            //Character cr = new Character(ch);
            int read = 0;
            //while (in != null) {
            try {
//                while ((read = in.read(buff)) !=-1) {
//                while (in != null && in.read() != -1) {
                  while (in != null) {
//                  while (in.read() != -1) {
                    try {
                        //in.read(buff);
                        strbuff.setLength(0);
                        strbuff.append(new String(buff,"UTF-8"));
                        Arrays.fill(buff, (byte) 0);
                        //strbuff.append(new String(buff, 0, read));
                        //System.out.print((char)in.read());
                        //in.read(buff);
                        //string += Character.toString((char)in.read());
                        //string +=((char)in.read()).toString();
                        //remake = new String(buff,"UTF-8");
                        String getmsg = in.readUTF();
                        Log.d("서버에서 받음",getmsg);
                        //Log.d("서버에서 받음",strbuff.toString().trim());
                        //제이슨으로 분기
                        JSONObject jsonObject = new JSONObject(getmsg);
                        String status = jsonObject.getString("status").trim();
                        //status
                        if(status.equals("succ_mk_room")){
                            //방디비삽입
                            int room_id = jsonObject.getInt("data");
                            String sentfrom = jsonObject.getString("sentfrom");

                            JSONArray jsonArray = new JSONArray(jsonObject.getString("members"));
                            List<String> member = new ArrayList<String>();
                            for(int i=0; i < jsonArray.length(); i++){
                                member.add(jsonArray.getString(i).trim());
                            }
                            Collections.sort(member);
                            String[] temp = member.toArray(new String[member.size()]);
                            String members = Arrays.toString(temp);
                            Log.d("정렬 잘되냐?",members);
                            //방이없으면 -1
                            int  branch;
                            //flag = 0 다 넣어줘야함
                            //flag = 0 사용가능 flag = -1 지워짐
                            branch = dbManager.RoomisExist(members,user_id);
                            //방이 없으면 디비 저장
                            //있으면 어떻게 할까???
                            if(branch==-1){
                                dbManager.insert_room(room_id,members,user_id);
                            }
                            //디비 검색추 members가 있으면 방번호를 바꾸자

                            //분기 sentfrom이랑 유저아이디랑 같으면 대화방으로 인텐트
                            //아니면 가만히 있음
                            if (sentfrom.equals(user_id)){
                                Intent intent = new Intent(ClientSocketService.this,ChatRoomActivity.class);
                                intent.putExtra("room_id", room_id);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getBaseContext().startActivity(intent);
                            }
                        }else if(status.equals("send_msg")){
                            int room_id = jsonObject.getInt("room_cnt");
                            String msg = jsonObject.getString("data");
                            String sentfrom = jsonObject.getString("sentfrom");
                            Log.d("댛롸내용",msg);
                            //대화내용
                            dbManager.insert_conversation(room_id,msg,sentfrom,myApp.getId());
                            //방목록에서 보이게 방 conversation 항목에 삽입
                            dbManager.insert_room_conversation_cnt (room_id,msg,myApp.getId());
                            //프래그먼트 새로고침
                            //myApp.getChatRoomBR();
                            Intent sendIntent = new Intent("org.appspot.apprtc.my.GET_ROOMS_LIST");
                            sendIntent.putExtra("sendString", "방목록 갱신");
                            sendBroadcast(sendIntent);
                            //이미지 다운로드
                            if(msg.contains("http://"+myApp.getIphttp())){
//                                downloadFile(msg,Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/Thumbnails/"+new File(msg).getName());
                                new down(msg, Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/Thumbnails/"+msg.substring(msg.lastIndexOf("/"),msg.length())).start();
                                //new downpic(msg, Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/Thumbnails/"+msg.substring(msg.lastIndexOf("/"),msg.length())).start();
////                                String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/Thumbnails/";
////                                System.out.println( "문자(열) " + dirPath + " : " +dirPath);
////                                File directory_ChatRTC = new File(dirPath);
////                                directory_ChatRTC.mkdir();
////                                if(!directory_ChatRTC.exists()){ // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
////                                    directory_ChatRTC.mkdir();
////                                }
//                                System.out.println( "문자(열):ㄹ미들다: "+msg);
//                                final InputStream inputStream = new URL(msg).openStream();
//                                StringTokenizer values = new StringTokenizer(msg, "/" );
//                                String[] arr=new String[values.countTokens()];
//                                for( int x = 0; values.hasMoreElements(); x++ ){
//                                    arr[x]=values.nextToken();
//                                    System.out.println( "문자(열) " + x + " : " + arr[x]);
//                                }
//                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/Thumbnails/"+arr[4]);
//                                final OutputStream out = new FileOutputStream(file);
//                                writeFile(inputStream, out);
////                                new Thread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        try {
////                                            writeFile(inputStream, out);
////                                            if(myApp.getChatRoomBR()){
////                                                Intent sendIntent = new Intent("org.appspot.apprtc.my.SEND_BROAD_CAST");
////                                                sendIntent.putExtra("isBoolean", true);
////                                                sendIntent.putExtra("sendInteger", 123);
////                                                sendIntent.putExtra("sendString", "Intent String");
////                                                sendBroadcast(sendIntent);
////                                            } else{
////                                                Log.d("브로드 케스트 없다 ","노티피케이션하자");
////                                            }
////                                        } catch (IOException e) {
////                                            e.printStackTrace();
////                                        }
////                                    }
////                                }).start();
//                                out.close();
                            }
                            if(myApp.getChatRoomBR()){
                                sendIntent = new Intent("org.appspot.apprtc.my.SEND_BROAD_CAST");
                                sendIntent.putExtra("isBoolean", true);
                                sendIntent.putExtra("sendInteger", 123);
                                sendIntent.putExtra("sendString", "Intent String");
                                sendBroadcast(sendIntent);
                            } else{
                                Log.d("브로드 케스트 없다 ","노티피케이션하자");
                            }
                            //Intent intent = new Intent("org.appspot.apprtc.my.SEND_BROAD_CAST");
//                            final PackageManager packageManager = getBaseContext().getPackageManager();
//                            List<ResolveInfo> list = packageManager.queryBroadcastReceivers(intent,
//                                            PackageManager.GET_META_DATA|PackageManager.GET_RESOLVED_FILTER);
                            //Log.d("브로드캐스트리시버캣 수", String.valueOf(list.size()));
//                            Intent screenIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
//                            PendingIntent screenSender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
//                            Log.d("등록??", String.valueOf(screenSender));
//                            if (screenSender == null) {
//                                Log.d("아직 등록 안됨","아직 등록 안됨");
//                            }

//                            PackageManager packageManager = getPackageManager();
//                            List<String> startupApps = new ArrayList<String>();
//                            Intent intent = new Intent("org.appspot.apprtc.my.SEND_BROAD_CAST");
//                            List<ResolveInfo> activities = packageManager.queryBroadcastReceivers(intent, 0);
//                            Log.d("짜증나", String.valueOf(activities.size()));
//                            for (ResolveInfo resolveInfo : activities) {
//                                ActivityInfo activityInfo = resolveInfo.activityInfo;
//                                if (activityInfo != null) {
//                                    Log.d("짜증나","짜증나");
//                                }
//                            }
                            //FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                            //ft.detach(this).attach(this).commit();
                        }else if(status.equals("succ_room_member_out")){
                            int room_id = jsonObject.getInt("room_cnt");
                            String sentfrom = jsonObject.getString("sentfrom");
                            if(sentfrom.equals(myApp.getId())){
                                //방나가는 상황
                                //대화디비에서 지우기 플래그 -1로 원래 0이었음
                                //Log.d("좀되라","room_id:"+room_id+"   my_id:"+myApp.getId());
                                dbManager.remove_conversation(room_id,sentfrom);
                                dbManager.remove_room(room_id,sentfrom);
                                Intent sendIntent = new Intent("org.appspot.apprtc.my.GET_ROOMS_LIST");
                                sendIntent.putExtra("sendString", "방목록 갱신");
                                sendBroadcast(sendIntent);
                                Intent intent = new Intent(ClientSocketService.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getBaseContext().startActivity(intent);
                            }else{
                                //방맴버가 바뀜
                                //방이이디 이름 맴버 방 이름 upsert
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("members"));

                                List<String> member = new ArrayList<String>();
                                for(int i=0; i < jsonArray.length(); i++){
                                    member.add(jsonArray.getString(i).trim());
                                }
                                Collections.sort(member);
                                String[] temp = member.toArray(new String[member.size()]);
                                String members = Arrays.toString(temp);
                                Log.d("정렬 잘되냐?",members);
                                dbManager.insert_room(room_id,members,user_id);
                                Intent sendIntent = new Intent("org.appspot.apprtc.my.GET_ROOMS_LIST");
                                sendIntent.putExtra("sendString", "방목록 갱신");
                                sendBroadcast(sendIntent);
                            }
                        }else if(status.equals("succ_edit_room_members")){
                            //{"status":"succ_edit_room_members","data":9,"sentfrom":"sang","members":["yoon","sang","jo"]}
                            int room_id = jsonObject.getInt("data");
                            String sentfrom = jsonObject.getString("sentfrom");
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("members"));
                            ArrayList<String> member = new ArrayList<String>();
                            for(int i=0; i < jsonArray.length(); i++){
                                member.add(jsonArray.getString(i).trim());
                            }
                            Collections.sort(member);
                            String[] temp = member.toArray(new String[member.size()]);
                            String members = Arrays.toString(temp);
                            Log.d("정렬 잘되냐?1111",members);
                            dbManager.insert_room(room_id,members,user_id);
                            if (sentfrom.equals(user_id)){
                                Intent intent = new Intent(ClientSocketService.this,ChatRoomActivity.class);
                                intent.putExtra("room_id", room_id);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getBaseContext().startActivity(intent);
                            }else {
                                Intent sendIntent = new Intent("org.appspot.apprtc.my.GET_ROOMS_LIST");
                                sendIntent.putExtra("sendString", "방목록 갱신");
                                sendBroadcast(sendIntent);

                                sendIntent = new Intent("org.appspot.apprtc.my.SEND_BROAD_CAST");
                                sendIntent.putExtra("sendString", "방이름");
                                sendBroadcast(sendIntent);
                            }
                        }else if(status.equals("rtc_call_start")){
                            //{ "status": "rtc_call_start","caller": "yoon","callee":"aaaa","room_cnt": 96539148}
                            String caller = jsonObject.getString("caller");
                            String callee = jsonObject.getString("callee");
                            int room_id = jsonObject.getInt("room_cnt");
                            if(!caller.equals(myApp.getId())){
                                //전화건사람이 내아이디랑 다르면 전화 받는 화면 intent
                                Log.d("caller+room_id",caller+callee+room_id);
                                Intent intent = new Intent(ClientSocketService.this,IncomeRTCActivity.class);
                                intent.putExtra("room_id", room_id);
                                intent.putExtra("caller", caller);
                                intent.putExtra("callee", callee);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getBaseContext().startActivity(intent);
                            }
                        }else if(status.equals("rtc_call_end")){
                            String caller = jsonObject.getString("caller");
                            String callee = jsonObject.getString("callee");
                            int room_id = jsonObject.getInt("room_cnt");
                            if(caller.equals(myApp.getId())){
                                Intent sendIntent = new Intent("org.appspot.apprtc.my.rtc_call_end");
                                sendBroadcast(sendIntent);
                            }
                        }else if(status.equals("rtc_caller_end")){
                            String caller = jsonObject.getString("caller");
                            String callee = jsonObject.getString("callee");
                            int room_id = jsonObject.getInt("room_cnt");
                            if(!caller.equals(myApp.getId())){
                                Intent sendIntent = new Intent("org.appspot.apprtc.my.rtc_caller_end");
                                sendBroadcast(sendIntent);
                            }
                        }
                        Log.d("서버에서 받음2",strbuff.toString().trim());
                        //알림
                        strbuff.setLength(0);
                        strbuff.append(new String(buff,"UTF-8"));
                        Arrays.fill(buff, (byte) 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }/*catch (EOFException e) {
                        e.printStackTrace();
                        out.close();
                        in.close();
                        socket.close();
                        receiver.start();
                        thread.start();
                    }*/
                      /*finally {
                        try {
                            // Close the input stream.
                            in.close();
                            out.close();
                        }
                        catch(IOException ex) {
                            System.err.println("An IOException was caught: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }*/
                  }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } // run
    }

    private void writeFile(InputStream is, OutputStream os) throws IOException
    {
        int c = 0;
        while((c = is.read()) != -1)
            os.write(c);
        os.flush();
    }

    Thread thread;
    Thread receiver;
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        //MySQLiteOpenHelper dbManager;
        //Context context = getApplicationContext();
        dbManager = new MySQLiteOpenHelper(this.getBaseContext() , "user.db", null, 1);
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.i("서비스호출", "onStartCommand()실행됨");
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/Thumbnails";
        System.out.println( "문자(열) " + dirPath + " : " +dirPath);
        File directory_ChatRTC = new File(dirPath);
        if(!directory_ChatRTC.isDirectory()/* exists()*/){ // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            directory_ChatRTC.mkdirs();
        }
        myApp = (MyApplication)getApplicationContext();
        user_id = myApp.getId();

//        myApp.setState(5);
//        state = myApp.getState();
        //final Socket socket = null;
        //final Socket[] socket = {null};
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    //socket = new Socket("192.168.0.38", 9876);
                    SocketAddress socketAddress = new InetSocketAddress(myApp.getIp(), myApp.getPort());
                    socket = new Socket();
                    //socket.setSoTimeout(10000); /* InputStream에서 데이터읽을때의 timeout */
                    socket.connect(socketAddress, 0); /* socket연결 자체에대한 timeout */
                    myApp.setSocket(socket);
                    DataOutputStream out;
                    out = new DataOutputStream(socket.getOutputStream());
                    //out.write(("{ \"status\": \"login\", \"data\": \""+myApp.getId()+"\" }").getBytes("UTF-8"));
                    out.writeUTF("{ \"status\": \"login\", \"data\": \""+myApp.getId()+"\" }");
                    //Thread sender = new Thread(new ClientSender(socket));
                    receiver = new Thread(new ClientReceiver(socket));
                    //sender.start();
                    receiver.start();
//
//            DataInputStream din = new DataInputStream(socket.getInputStream());
//            int n = din.read();
//            if(n!=1) {
//                return super.onStartCommand(intent, flags, startId);
//            }
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.e("소켓접속상태", e.getMessage());
                    if(socket !=null ){
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    //return super.onStartCommand(intent, flags, startId);
                }
            }
        });
        thread.start();
        // Send Notification
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
 	public void onDestroy() {
		super.onDestroy();
        try {
            myApp.getSocket().close();
//            receiver.interrupted();
//            thread.interrupted();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    //받기
//    static class ClientReceiver extends Thread {
//        Socket socket;
//        DataInputStream in;
//        ClientReceiver(Socket socket) {
//            this.socket = socket;
//            try {
//                in = new DataInputStream(socket.getInputStream());
//            } catch (IOException e) {
//            }
//        }
//        public void run() {
//            while (in != null) {
//                try {
//                    System.out.println(in.readUTF());
//                } catch (IOException e) {
//                }
//            }
//        } // run
//    }
//
//    //보내기
//    static class ClientSender extends Thread {
//        Socket socket;
//        DataOutputStream out;
//        String name;
//        ClientSender(Socket socket, String name) {
//            this.socket = socket;
//            try {
//                out = new DataOutputStream(socket.getOutputStream());
//                this.name = name;
//            } catch (Exception e) {
//            }
//        }
//        public void run() {
//            Scanner scanner = new Scanner(System.in);
//            try {
////                if (out != null) {
////                    out.writeUTF(name);
////                }
//                while (out != null) {
//                    out.writeUTF("[" + name + "]" + scanner.nextLine());
//                }
//            } catch (IOException e) {
//            }
//        } // run()
//    }

//    public void setSocket(String ip, int port) throws IOException {
//
//        try {
//            socket = new Socket(ip, port);
//            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        } catch (IOException e) {
//            System.out.println(e);
//            e.printStackTrace();
//        }
//
//    }

//    static class ClientSender extends Thread {
//        Socket socket;
//        DataOutputStream out;
//        ClientSender(Socket socket) {
//            this.socket = socket;
//            try {
//                out = new DataOutputStream(socket.getOutputStream());
//            } catch (Exception e) {
//            }
//        }
//        public void run() {
//            Scanner scanner = new Scanner(System.in);
//            try {
////                if (out != null) {
////                    out.writeUTF(name);
////                }
//                while (out != null) {
//                    //scanner.reset();
////                  out.writeUTF("[" + name + "]" + scanner.nextLine());
////                	System.out.println("제이슨 입력:");
//                    //out.writeUTF(scanner.nextLine().getBytes());
//                    out.write(scanner.nextLine().getBytes("UTF-8"));
//                }
//            } catch (IOException e) {
//            }
//            scanner.close();
//        } // run()
//    }
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[128];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
    class down extends Thread {
        String fileURL;
        String saveDir;
        public down(String fileURL, String saveDir) {
            this.fileURL=fileURL;
            this.saveDir=saveDir;
        }

        public void run() {
            URL url = null;
            Bitmap bitmap = null;
            try {
                url = new URL(fileURL);

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                            fileURL.length());
                }

                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);

                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();
                String saveFilePath = saveDir + File.separator + fileName;

                // opens an output stream to save into file
                new File(saveDir);
                File file = new File(saveDir);
                FileOutputStream outputStream = new FileOutputStream(file);

                int bytesRead = -1;
                byte[] buffer = new byte[128];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                inputStream.close();

                System.out.println("File downloaded");
                Intent sendIntent = new Intent("org.appspot.apprtc.my.SEND_BROAD_CAST");
                sendIntent.putExtra("isBoolean", true);
                sendIntent.putExtra("sendInteger", 123);
                sendIntent.putExtra("sendString", "Intent String");
                sendBroadcast(sendIntent);
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class downpic extends Thread{
        String fileURL;
        String saveDir;
        public downpic(String fileURL, String saveDir) {
            this.fileURL=fileURL;
            this.saveDir=saveDir;
        }

        @Override
        public void run() {
            super.run();
            URL url = null;
            try {
                url = new URL(fileURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);//비트맵으로 받아옴

            String temp = saveDir;
            Log.d("서버파일받기", temp);
            File file2 = new File(temp);
            if (!file2.exists()) {
                //파일생성
                FileOutputStream out = new FileOutputStream(saveDir);
                //압축해서 실제 파일만들기
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                conn.disconnect();
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
