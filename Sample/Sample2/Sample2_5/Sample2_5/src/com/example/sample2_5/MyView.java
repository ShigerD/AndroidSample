package com.example.sample2_5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MyView extends View{
	Paint paint;
	
	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		paint=new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
	}

    protected void onDraw(Canvas canvas ){
    	super.onDraw(canvas);
    	canvas.drawColor(Color.GRAY);
    	canvas.drawRect(10,10,110,110,paint);
    	canvas.drawText("×Ô¶¨VIEW", 60, 70, paint);
    }
		
	

}
