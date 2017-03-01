package com.example.url;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class MainActivity extends Activity {
	String stringUrl="http://192.168.1.1:8200/MyUrlSample/msg.txt";
	String bitmapURL="http://10.12.42.64:8200/MyUrlSample/pic.jpg";
	EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn=(Button)findViewById(R.id.button1);
        et=(EditText)findViewById(R.id.editText1);
        et.setText("begin");
        btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				et.setText("enter btn");
				getURLpic();
				getStringUrlSourse();
			}
		});
    }
/*
 * (non-Javadoc)
 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
 */
    public void getStringUrlSourse(){
    	try{
    		et.setText("enter txt");
    		URL myUrl=new URL(stringUrl);
    		URLConnection myConn=myUrl.openConnection();
    		InputStream din=myConn.getInputStream();
    		BufferedInputStream bis =new BufferedInputStream (din);
    		ByteArrayBuffer baf=new ByteArrayBuffer(bis.available());
    		int data=0;
    		while((data=bis.read())!=-1){
    			baf.append((byte)data);
    		}
    		String msg=EncodingUtils.getString(baf.toByteArray(), "UTF-8");
    		
    		//et.setText(msg);
    		et.setText("hello");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    public void getURLpic(){
    	try{
    		et.setText("enter pic");
    		URL myURL=new URL(bitmapURL);
    		URLConnection myConn=myURL.openConnection();
    		InputStream din=myConn.getInputStream();
    		Bitmap bmp=BitmapFactory.decodeStream(din);
    		ImageView iv=(ImageView)findViewById(R.id.imageView1);
    		iv.setImageBitmap(bmp);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
