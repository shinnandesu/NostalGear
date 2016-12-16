package com.example.nostalgear;

import android.R.string;
import android.os.AsyncTask;
import android.text.StaticLayout;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.io.File;
import java.io.IOException;

public class PostMessageTask2 extends AsyncTask<String, String, String> {
    private CallBackTask callbacktask;
    @Override
    protected String doInBackground(String... contents) {
    	
    	String imagePath = "";  
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://life-cloud.ht.sfc.keio.ac.jp/~shinsan/NostalGear/upload.php");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        try {
            System.out.println("doInBackgroundで作業中...");
            File file = new File(contents[0]);
            System.out.println("いまからこいつをPOSTする->" + file.getAbsolutePath());
//            FileBody fileBody = new FileBody(file);
    		builder.addPart("file", new FileBody(file));
//            builder.addBinaryBody("learn_this_face", file, ContentType.create("image/jpeg"), file.getName());
//            FileEntity entity = new FileEntity(file, "image/jpeg"); 
//            httpPost.setEntity(entity);
            System.out.println(1);
            httpPost.setEntity(builder.build());
            System.out.println(2);
            HttpResponse response = httpClient.execute(httpPost);
//            System.out.println(EntityUtils.toString(response.getEntity()));
            HttpEntity entity = response.getEntity();
            imagePath = EntityUtils.toString(entity);
            System.out.println(3);
            
        } catch (ClientProtocolException e) {
            Log.v("ERR", "msg:" + e.getMessage());
        } catch(IOException e) {
            Log.v("ERR", "msg:" + e.getMessage());
        }
		
		return imagePath;
    }
    @Override
    protected void onPostExecute(String imagePath) {
        super.onPostExecute(imagePath);
        callbacktask.CallBack(imagePath);
    }

    public void setOnCallBack(CallBackTask _cbj) {
        callbacktask = _cbj;
    }

    public static class CallBackTask {
        public void CallBack(String imagePath) {
        }
    }
    
    
}
