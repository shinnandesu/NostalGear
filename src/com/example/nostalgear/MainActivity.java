package com.example.nostalgear;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.nostalgear.PostMessageTask2.CallBackTask;
import com.google.android.glass.app.Card;
import com.google.android.glass.content.Intents;

import java.io.File;

public class MainActivity extends Activity {
	
	private SurfaceView mySurfaceView;
    private Camera myCamera;


    // 写真撮影時の通知用の値
    private static final int TAKE_PICTURE_REQUEST = 1;
    String message = "";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // 画面に文字を表示するためCardを使用する
        Card card = new Card(this);
        card.setText("Object Recognition");
        setContentView(card.getView());
       
        
    }
    
    

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // タッチパッドがタップされた場合
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            takePicture();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 写真撮影を開始する
     */
    private void takePicture() {
        Log.d(MainActivity.class.getName(), "call takePicture");

        // Intent経由で撮影して、写真のデータをもらう
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // 第二引数のTAKE_PICTURE_REQUESTで呼び元を判定する
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

     
        
        Log.d(MainActivity.class.getName(), "call onActivityResult");

        // 結果がOKで、呼び元がTAKE_PICTURE_REQUESTの場合
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {

            Log.d(MainActivity.class.getName(), "TAKE_PICTURE_REQUEST & RESULT_OK");

            // 撮影結果の保存場所を取得する
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
            //String picturePath = data.getStringExtra(Intents.EXTRA_VIDEO_FILE_PATH);
            Log.d("Taken Picture Path: " + picturePath, "TAKE_PICTURE_REQUEST & RESULT_OK");

            // 保存されているか確認する
            processPictureWhenReady(picturePath);
            
            

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 写真が保存されるまで待機するメソッド
     * @param picturePath 写真のファイルパス
     */
    private void processPictureWhenReady(final String picturePath) {

        Log.d(MainActivity.class.getName(), "call processPictureWhenReady");

        final File pictureFile = new File(picturePath);

        if (pictureFile.exists()) {
            // 動画が存在する場合
            Log.d(MainActivity.class.getName(), "pictureFile がありました");

            
            // ここで動画を使用した処理を書く
            postMedia(picturePath);
           

               
          

            // また、進捗状況インジゲータや待ち画像を表示している場合は消す処理を書く

        } else {
            // 動画が存在しない場合
            Log.d(MainActivity.class.getName(), "pictureFileはまだありません");

            // ここで進捗状況インジゲータや待ち画像などを表示する処理を書く
            
            Card card = new Card(this);
            card.setText("Now Recognizing.....");
            setContentView(card.getView());

            // ファイルパスを取得する
            final File parentDirectory = pictureFile.getParentFile();

            // FileObserverでディレクトリを監視する
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                // MOVED_TOやCLOSE_WRITE後に保留中のイベントから保護するためのフラグ
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {

                    Log.d(MainActivity.class.getName(), "call event");

                    // 既に書き込まれている場合は何もしない
                    if (!isFileWritten) {

                        File affectedFile = new File(parentDirectory, path);
                        // 動画ファイルが書き込まれたチェックする
                        isFileWritten = affectedFile.equals(pictureFile);

                        Log.d(MainActivity.class.getName(), "affectedFile = " + affectedFile.getAbsolutePath());

                        if (isFileWritten) {

                            Log.d(MainActivity.class.getName(), "file is written");

                            // ファイルが書き込まれている場合は、FileObserverを停止する
                            stopWatching();

                            // ファイルが書き込まれている場合は、UI ThreadでprocessPictureWhenReadyを呼ぶ
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(MainActivity.class.getName(), "runOnUiThread");
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    } 

    private void postMedia(String pictureFile) {
        PostMessageTask2 post = new PostMessageTask2();
        post.execute(pictureFile);
        post.setOnCallBack(new CallBackTask(){

            @Override
            public void CallBack(String imagePath) {
                super.CallBack(imagePath);
                webView(imagePath);
                // ※１
                // resultにはdoInBackgroundの返り値が入ります。
                // ここからAsyncTask処理後の処理を記述します。
                Log.i("AsyncTaskCallback", "非同期処理が終了しました。");
            }

        });
    }
    
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
        }
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
         
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        }
    };
    
    private void webView(String imagePath) {
        System.out.println("open webview");
    	String url = imagePath;
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setData(Uri.parse(url));
    	startActivity(intent);
    	
     }
    
    
    
}