package org.appspot.apprtc.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.appspot.apprtc.R;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoActivity extends AppCompatActivity {
    int room_id;
    String picName;
    ImageView bigPhoto;
    ImageView bigPhoto1;
    ImageView bigPhoto2;
    ImageView bigPhoto3;
    MyApplication myApplication;
    Handler handler = null;
    TextView imagePath;
    Bitmap bmp;
    Bitmap bmp1;
    Bitmap bmp2;
    Bitmap bmp3;

    @Override
    protected void onPause() {
        super.onPause();
        bmp.recycle();
        bmp1.recycle();
        bmp2.recycle();
        bmp3.recycle();
    }

    private void updateImageView(String picPath) {
        //Mat img = imread(Environment.getExternalStorageDirectory().getAbsolutePath() +"/panoTmpImage/im" + id + ".jpeg");
//        Mat img = imread(picName);
//        bigPhoto.getImageMatrix();
//        Mat mat = Mat.zeros(bigPhoto.getHeight(), bigPhoto.getWidth(), CvType.CV_8UC3);
        //Mat bgr = new Mat (bigPhoto.getHeight(),bigPhoto.getWidth() , CvType.CV_8UC4);
        //'CV_8UC1' (gray-scale), 'CV_8UC3' (RGB) or 'CV_8UC4' (RGBA).
//        mat = org.opencv.imgproc.Imgproc.cvtColor(RGB2BGR);
//        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2BGRA,0);
        Mat bgr = Imgcodecs.imread(picPath,Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat mat = new Mat();
        Imgproc.cvtColor(bgr, mat, Imgproc.COLOR_RGB2BGR,0);
//        Mat mat = imread(picPath);
        //fillMatrixColor(mat.getNativeObjAddr(), 200, 1, 80);
        bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat,bmp,true);
        bigPhoto.setImageBitmap(bmp);
        //bmp.recycle();

        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY,0);
        bmp1 = Bitmap.createBitmap(gray.cols(), gray.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(gray,bmp1,true);
        bigPhoto1.setImageBitmap(bmp1);
        //bmp1.recycle();

        bmp2 = Bitmap.createBitmap(bgr.cols(), bgr.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(bgr,bmp2,true);
        bigPhoto2.setImageBitmap(bmp2);
        //bmp2.recycle();

        Mat dst = new Mat();
        Core.flip(mat, dst, -1);
        bmp3 = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst,bmp3,true);
        bigPhoto3.setImageBitmap(bmp3);
        //bmp3.recycle();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    // OpenCV 초기화 이후 함수 호출
                    //updateImageView(picName);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private static final String TAG = "OCVSample::Activity";
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            //사진이 없으면 받기
            File file = new File(picName);
            if (!file.exists()) {
                new down("http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/"+picName.substring(picName.lastIndexOf("/"),picName.length()),picName).start();
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("picName",picName);
                        //bigPhoto.setImageURI(Uri.fromFile(new File(picName)));
                        updateImageView(picName);

                    }
                });
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        myApplication = (MyApplication) getApplicationContext();
        bigPhoto = (ImageView)findViewById(R.id.bigPhoto);
        bigPhoto1 = (ImageView)findViewById(R.id.bigPhoto1);
        bigPhoto2 = (ImageView)findViewById(R.id.bigPhoto2);
        bigPhoto3 = (ImageView)findViewById(R.id.bigPhoto3);

        imagePath = (TextView)findViewById(R.id.imagePath);
        Intent intent = getIntent();
        room_id = intent.getExtras().getInt("room_id");
        picName = intent.getExtras().getString("picName");
        imagePath.setText(picName);
//        //사진이 없으면 받기
//        File file = new File(picName);
//        if (!file.exists()) {
//            new down("http://"+myApplication.getIphttp()+":"+myApplication.getPorthttp()+"/chatrtc/uploads/"+picName.substring(picName.lastIndexOf("/"),picName.length()),picName).start();
//        }else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d("picName",picName);
//                    //bigPhoto.setImageURI(Uri.fromFile(new File(picName)));
//                    updateImageView(picName);
//
//                }
//            });
//        }
        //Uri imgUri=Uri.parse("/data/data/MYFOLDER/myimage.png");
        //bigPhoto.setImageURI(new File(picName));
//        bigPhoto.setImageURI(Uri.fromFile(new File(picName)));
//        File imgFile = new  File(picName);
//        if(imgFile.exists()){
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            bigPhoto.setImageBitmap(myBitmap);
//        }
    }
    class down extends Thread {
        String fileURL;
        String saveDir;
        public down(String fileURL, String saveDir) {
            this.fileURL=fileURL;
            this.saveDir=saveDir;
        }

        public void run() {
            Log.d("다운스레드 시작","다운스레드 시작");
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
//                    bitmap = BitmapFactory.decodeStream(inputStream);//비트맵으로 받아옴
                    String saveFilePath = saveDir + File.separator + fileName;

                    // opens an output stream to save into file
                    //new File(saveDir);
//                    File file = new File(saveDir);
//                    FileOutputStream outputStream = new FileOutputStream(file);
                    FileOutputStream outputStream = new FileOutputStream(saveDir);

                    int bytesRead = -1;
                    byte[] buffer = new byte[128];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    inputStream.close();

                    System.out.println("File downloaded");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("picName",picName);
//                            bigPhoto.setImageURI(Uri.fromFile(new File(picName)));
                            updateImageView(picName);
                        }
                    });
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("picName",picName);
//                            bigPhoto.setImageURI(Uri.fromFile(new File(picName)));
//                        }
//                    });
                } else {
                    System.out.println("No file to download. Server replied HTTP code: " + responseCode);
                }
                httpConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

