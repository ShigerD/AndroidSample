package com.example.filemanage;



import java.io.File;
import java.util.List;

//import com.hoperun.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {
	
	private List<String> items;
	private List<String> paths;
	private LayoutInflater inflater;
	private Bitmap rootIcon;
	private Bitmap prevIcon;
	private Bitmap docIcon;
	private Bitmap folderIcon;
	
	public FileListAdapter() {}
	public FileListAdapter(Context context, List<String> items,
			List<String> paths) {
		// 从context获取一个布局加载器
		inflater = LayoutInflater.from(context);
		this.items = items;
		this.paths = paths;
		// 初始化关联图标
		rootIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.back_root);
		prevIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.back_prev);
		docIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.doc);
		folderIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.folder);
		
	}

	// 返回数据的记录条数
	public int getCount() {
		return items.size();
	}

	// 返回列表中的数据
	public Object getItem(int position) {
		return items.get(position);
	}

	// 返回item的id值
	public long getItemId(int position) {
		return position;
	}

	// 获取ListView中的item组件容器
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null ;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_row, null);
			// 创建子组件容器
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.fileNameText);
			holder.imageIcon = (ImageView) convertView.findViewById(R.id.imageIcon);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// 根据文件不同类型，显示不同图标及文件名称
		if (items.get(position).equals("back to /")) {
			holder.imageIcon.setImageBitmap(rootIcon);
		} else if (items.get(position).equals("back previous")) {
			holder.imageIcon.setImageBitmap(prevIcon);
		} else {
			File f = new File(paths.get(position));
			if (f.isDirectory()) {
				holder.imageIcon.setImageBitmap(folderIcon);
			} else {
				holder.imageIcon.setImageBitmap(docIcon);
			}
		}
		holder.text.setText(items.get(position));
		return convertView;
	}
	
	// 内部类，容器类
	private class ViewHolder {
		TextView text;
		ImageView imageIcon;
	}

}