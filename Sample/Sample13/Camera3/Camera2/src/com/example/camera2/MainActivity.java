package com.example.camera2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

   private Camera cameraObject;
   private ShowCamera showCamera;
   private ImageView pic;
   public static Camera isCameraAvailiable(){
      Camera object = null;
      try {
         object = Camera.open(); 
        
      }
      catch (Exception e){
      }
      return object; 
   }

   private PictureCallback capturedIt = new PictureCallback() {

      @Override
      public void onPictureTaken(byte[] data, Camera camera) {

      Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length);
      
      if(bitmap==null){
         Toast.makeText(getApplicationContext(), "not taken", Toast.LENGTH_SHORT).show();
      }
      else
      {
        	
         pic.setImageBitmap(bitmap);
         saveBitmap(bitmap);
         Toast.makeText(getApplicationContext(), strImgPath, Toast.LENGTH_SHORT).show(); 
      }
     //
     // cameraObject.release();
      cameraObject.startPreview();
     
      
   }
};

String  strImgPath;

public void saveBitmap(Bitmap bm) {
		//Log.e(TAG, "保存图片");
		String picName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";//照片命名
		//图片存储文件夹
		strImgPath = Environment.getExternalStorageDirectory().toString() + "/1_camera/";   
		File f = new File(strImgPath);
		if (!f.exists()) {
			f.mkdirs();
			Toast.makeText(getApplicationContext(), "创建文件夹"+strImgPath, Toast.LENGTH_SHORT).show();
		}
		f = new File(strImgPath, picName);

	   try {
	    FileOutputStream out = new FileOutputStream(f);
	    bm.compress(Bitmap.CompressFormat.PNG, 90, out);
	    out.flush();
	    out.close();
	   // Log.i(TAG, "已经保存");
	   } catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	   } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	   }

	 }


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      pic = (ImageView)findViewById(R.id.imageView1);
      cameraObject = isCameraAvailiable();
      showCamera = new ShowCamera(this, cameraObject);
      FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
      preview.addView(showCamera);
   }
   public void snap(View view){
      cameraObject.takePicture(null, null, capturedIt);
  /**   
      cameraObject = isCameraAvailiable();
      showCamera = new ShowCamera(this, cameraObject);
      FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
      preview.addView(showCamera);
      **/ 

   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }
}



