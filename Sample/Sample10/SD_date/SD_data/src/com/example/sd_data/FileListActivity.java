package com.example.sd_data;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileListActivity extends Activity {
	String myfilePath;
	private ArrayAdapter<String> mFileArrayAdapter;
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.file_list);
	        ListView pairedListView = (ListView) findViewById(R.id.file_available);
	        
	     //   myfilePath=Environment.getExternalStorageDirectory()
	    //			.getAbsolutePath()+"/A_MySdData1";
	        myfilePath=MainActivity.myFilePath;
	        mFileArrayAdapter = new ArrayAdapter<String>(this, R.layout.file_name);
	        getFileDir(myfilePath) ;
	        pairedListView.setAdapter(mFileArrayAdapter);
	        pairedListView.setOnItemClickListener(mFileClickListener);
	 }
	 /*
	  * 
	  */
	 private void getFileDir(String path) {

	 		//items = new ArrayList<String>();
	 		//paths = new ArrayList<String>();
	 		// 获取当前路径下的文件
	 		File presentFile = new  File(path);
	 		File[] files = presentFile.listFiles();
	 	/*	
	 		if (! path.equals(rootPath)) {
	 			// 返回根目录
	 			items.add("back to /");
	 			paths.add(rootPath);
	 			// 返回上一级目录
	 			items.add("back previous");
	 			paths.add(presentFile.getParent());
	 		}
	 	*/

	 		// 添加当前路径下的所有的文件名和路径
	 		for (File f : files) {
	 			//tvcontent+=(f.getName());
	 			mFileArrayAdapter.add(f.getName());
	 			//paths.add(f.getPath());
	 		}
	 		
	 		// 设置列表适配器
	 		//setListAdapter(new FileListAdapter(FileManagerActivity.this, items, paths));
	 	}
	 /*
	  * 
	  */
	    private OnItemClickListener mFileClickListener = new OnItemClickListener() {
	        @Override
			public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            
	            String info = ((TextView) v).getText().toString();
	            
	            MainActivity.myPictureSourse=MainActivity.myFilePath+"/"+info;
	            MainActivity.tv.setText(MainActivity.myPictureSourse);
	            // Create the result Intent and include the MAC address
	            Intent intent = new Intent();	           
	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
	        }
	    };

}
