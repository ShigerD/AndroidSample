package com.example.getdate;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
      
        
      static  SimpleDateFormat formatter=new SimpleDateFormat ("hh_mm_ss");
      static   Date curDate=new Date(System.currentTimeMillis());
      static   String strdate=formatter.format(curDate);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       final TextView tv=(TextView)findViewById(R.id.textView1); 
       Button btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				curDate=new Date(System.currentTimeMillis());
				strdate=formatter.format(curDate);		
				tv.setText(strdate);
			}
        	
        });
    }

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
