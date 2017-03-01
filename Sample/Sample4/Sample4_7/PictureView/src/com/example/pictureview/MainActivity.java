package com.example.pictureview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity {
	ImageView iv;
	Button btnnext;
	Button btnprevious;
	Button btnplus;
	Button btnminus;
	int currImgId=0;
	int alpha=255;
	int [] imgId={
		R.drawable.p1,
		R.drawable.p2,
		R.drawable.p3,
		R.drawable.p4,
		R.drawable.p5,
		R.drawable.p6,
		R.drawable.p7,
	};
	private View.OnClickListener myListener=new View.OnClickListener() {	
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			if (v==btnnext){
				currImgId=(currImgId+1)%imgId.length;
				iv.setImageResource(imgId[currImgId]);
			}
			else if(v==btnprevious){
				currImgId=(currImgId-1+imgId.length)%imgId.length;
				iv.setImageResource(imgId[currImgId]);
			}
			else if(v==btnplus){
				alpha+=50;
				if(alpha>255)alpha=255;

				iv.setAlpha(alpha);
			}
			else if(v==btnminus){
				alpha-=50;
				if(alpha<0)alpha=0;
				iv.setAlpha(alpha);
			}
		}
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv=(ImageView)findViewById(R.id.imageView1);
        btnnext=(Button)findViewById(R.id.next);
        btnprevious=(Button)findViewById(R.id.previous);
        btnplus=(Button)findViewById(R.id.alpha_plus);
        btnminus=(Button)findViewById(R.id.alpha_minus);
        btnnext.setOnClickListener(myListener);
        btnprevious.setOnClickListener(myListener);
        btnplus.setOnClickListener(myListener);
        btnminus.setOnClickListener(myListener);
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
