package com.example.sd_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn=(Button)findViewById(R.id.button1);
        tv=(TextView)findViewById(R.id.textView1);
        String sdpath=getSdCardPath();
        tv.setText(sdpath);
      //  InputStream inputStream = null;
        try{
        	File myfile=new File(sdpath,"mysdtext.text");
        	FileInputStream is=new FileInputStream(myfile);
        	byte[] b=new byte[is.available()];
        	is.read(b);
        	String result=new String(b);
        	System.out.println("读取成功"+result);
        	tv.setText(sdpath+"读取成功"+result);
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    /*
     * 
     */
    public static String getSdCardPath(){
    	String sdpath="";
    	sdpath=Environment.getExternalStorageDirectory()
    			.getAbsolutePath();
    	return sdpath;
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
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    public static String getDefaultFilePath(){
    	String filepath="";
    	File file=new File(Environment.getExternalStorageDirectory(),
    			"abc.txt");
    	if(file.exists()){
    		filepath=file.getAbsolutePath();
    	}else{
    		filepath="no exist";
    	}
    	return filepath;
    }
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    
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
