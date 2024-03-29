package com.example.filemanage;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//import com.hoperun.adapter.FileListAdapter;

public class FileManagerActivity extends ListActivity {
    /** Called when the activity is first created. */
	private TextView showXPath;// 显示文件文件路径
	private ArrayList<String> items;// 要显示的文件名
	private ArrayList<String> paths;// 显示文件路径
	private String rootPath = "/";// 根目录
	private View renameDialogView;// 重命名对话框视图
	private EditText nameEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        showXPath = (TextView) findViewById(R.id.xPath);
        getFileDir(rootPath);// 获取文件列表
    }

    // 获取文件列表方法
	private void getFileDir(String path) {
		showXPath.setText(path);//显示当前路径
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		// 获取当前路径下的文件
		File presentFile = new  File(path);
		File[] files = presentFile.listFiles();
		
		if (! path.equals(rootPath)) {
			// 返回根目录
			items.add("back to /");
			paths.add(rootPath);
			// 返回上一级目录
			items.add("back previous");
			paths.add(presentFile.getParent());
		}
		
		// 添加当前路径下的所有的文件名和路径
		for (File f : files) {
			items.add(f.getName());
			paths.add(f.getPath());
		}
		
		// 设置列表适配器
		setListAdapter(new FileListAdapter(FileManagerActivity.this, items, paths));
	}
	
	// List中item的点击事件
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File f = new File(paths.get(position));
		if (f.isDirectory()) {
			getFileDir(paths.get(position));
			
		} else {
			fileHandle(f);
		}
	}

	// File对象处理方法
	private void fileHandle(final File f) {
		// 设置监听器操作，在单击列表文件item的时候弹出对话框
		OnClickListener clickListener = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {// 打开文件
					openFile(f);
				} else if (which == 1) {// 修改文件名
					// 创建修改文件名对话框
					LayoutInflater renameInflater = LayoutInflater
						.from(FileManagerActivity.this);
					renameDialogView = renameInflater.inflate(
							R.layout.rename_alert_dialog, null);
					nameEdit = (EditText) renameDialogView.findViewById(R.id.nameEdit);
					// 设置编辑框内容未文件名
					nameEdit.setText(f.getName());
					
					// 在选项对话框中选择要进行的操作，在弹出的重命名对话框中的监听器
					OnClickListener renameDialogListener = new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							String nameEditStr = nameEdit.getText().toString();// 获取重命名编辑框中的文本内容
							final String postRenamedFilePath = f.getParentFile().getPath() 
									+ "/" + nameEditStr;// 重命名后的文件路径
							
							if (new File(postRenamedFilePath).exists()) {// 修改后的文件名与已有的文件名冲突
								// 不包括未修改文件名，直接点击确定的情况
								if (! nameEditStr.equals(f.getName())) {
									new AlertDialog.Builder(FileManagerActivity.this)
										.setTitle("提示").setMessage("文件已经存在，是否覆盖？")
										.setPositiveButton("确定", new DialogInterface.OnClickListener() {
											// 确定覆盖原有文件进行的操作
											public void onClick(DialogInterface dialog,	int which) {
												f.renameTo(new File(postRenamedFilePath));// 重命名覆盖文件
												getFileDir(f.getParent());// 列出重命名后结果
											}
											
										}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
											}
											
										}).show();
								} 
								
							} else {
								f.renameTo(new File(postRenamedFilePath));// 重命名覆盖文件
								getFileDir(f.getParent());// 列出重命名后结果
							}
						}
					};
					
					// 重命名对话框
					AlertDialog renameDialog =  new AlertDialog.Builder(FileManagerActivity.this).create();
					renameDialog.setView(renameDialogView);// 设置重命名对话框的试图
					
					renameDialog.setButton("确定", renameDialogListener);
					renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
						}
						
					});
					renameDialog.show();
					
				} else {
					// 删除选中的文件选项
					new AlertDialog.Builder(FileManagerActivity.this).setTitle("删除")
						.setMessage("确定要删除此文件吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
							// 确定删除文件
							public void onClick(DialogInterface dialog,
									int which) {
								f.delete();// 删除文件
								getFileDir(f.getParent());
							}
							
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
							}
							
						}).show();
				}
				
			}
			
		};
		
		// 创建listDialog,选项对话框
		String[] operas = new String[] {"open", "rename", "delete"};
		new AlertDialog.Builder(FileManagerActivity.this)
			.setTitle("operator dialog").setItems(operas, clickListener)
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				}
				
			}).show();
	}

	// 文件打开方法
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		// 获取文件媒体类型
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	// 获取MIME Type类型
	private String getMIMEType(File f) {
		String type = "";
		String fileName = f.getName();
		String end = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
		// 判断文件类型
		if(end.equals("m4a") || end.equals("mp3") || end.equals("mid") 
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio"; 
	    } else if (end.equals("3gp") || end.equals("mp4")) {
	    	type = "video";
	    } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") 
	    		|| end.equals("jpeg") || end.equals("bmp")) {
	    	type = "image";
	    } else {
	      type="*";
	    }
		// MIME Type格式是"文件类型/文件扩展名"
		type += "/*";
		return type;
	}
}