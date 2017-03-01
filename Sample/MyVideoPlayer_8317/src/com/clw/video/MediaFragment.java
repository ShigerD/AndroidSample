
package com.clw.video;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MediaFragment extends Fragment {
    
    private static final String TAG = MediaFragment.class.getSimpleName();
    
    private GroupListAdapter mArrayAdapter;
    private Store mStore;
    private ListView mListView;
    private View mView;
    
    private LinearLayout mAllVideos;

    public MediaFragment(Store store) {
        mStore = store;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaModel.getInstance().registerDataChangedListener(mStore, mDataListener);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaModel.getInstance().unregisterDataChangedListener(mStore, mDataListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "=onCreateView=");
        mView = inflater.inflate(R.layout.video_group_list_layout, container, false);
        mListView = (ListView)mView.findViewById(R.id.main_list);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mArrayAdapter = new GroupListAdapter(getActivity());
        mListView.setAdapter(mArrayAdapter);
        mAllVideos = (LinearLayout)mView.findViewById(R.id.all_Videos);
        mAllVideos.setOnClickListener(mAllViewClick);
        ArrayList<VideoEntry> list = MediaModel.getInstance().startAllVideoLoader(mStore, mListener);
        if (list == null) {
            mView.findViewById(R.id.all_list).setVisibility(View.GONE);
            mView.findViewById(R.id.no_videos).setVisibility(View.GONE);
            mView.findViewById(R.id.list_loading).setVisibility(View.VISIBLE);
        } else if (list.size() == 0) {
            mView.findViewById(R.id.all_list).setVisibility(View.GONE);
            mView.findViewById(R.id.no_videos).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.list_loading).setVisibility(View.GONE);
        } else {
            mArrayAdapter.setDatalist(list);
            mArrayAdapter.notifyDataSetChanged();
        }
        return mView;
    }
    
    private void updateDataList(Store store, ArrayList<VideoEntry> dataList) {
        if (mArrayAdapter != null) {
            mArrayAdapter.setDatalist(dataList);
            mArrayAdapter.notifyDataSetChanged();
            Log.i(TAG, "dataList= " + dataList.size());
            if (dataList.size() == 0) {
                mView.findViewById(R.id.all_list).setVisibility(View.GONE);
                mView.findViewById(R.id.no_videos).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.list_loading).setVisibility(View.GONE);
            } else {
                mView.findViewById(R.id.all_list).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.no_videos).setVisibility(View.GONE);
                mView.findViewById(R.id.list_loading).setVisibility(View.GONE);
            }
        }
    }
    
    private MediaModel.IDataChangedListener mDataListener = new MediaModel.IDataChangedListener() {
        @Override
        public void onDataChanaged(Store store, ArrayList<VideoEntry> dataList) {
            updateDataList(store, dataList);
        }
    };
    
    private MediaModel.OnAllVideoLoadListener mListener = new MediaModel.OnAllVideoLoadListener() {
        @Override
        public void onLoadFinished(Store store, ArrayList<VideoEntry> dataList) {
            updateDataList(store, dataList);
        }
    };
    
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            VideoTabActivity activity = (VideoTabActivity)getActivity();
            activity.switchToPlay(mStore, mArrayAdapter.getDatalist().get(arg2).mVideoList, false);
        }
    };
    
    private View.OnClickListener mAllViewClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            ArrayList<Uri> fileUri = new ArrayList<Uri>();
            for(int i = 0; i < mArrayAdapter.getDatalist().size(); i++) {
                for(int j = 0; j < mArrayAdapter.getDatalist().get(i).mVideoList.size(); j++) {
                    fileUri.add(mArrayAdapter.getDatalist().get(i).mVideoList.get(j));
                }
            }
            VideoTabActivity activity = (VideoTabActivity)getActivity();
            activity.switchToPlay(mStore, fileUri, true);
        }
    };

    private class GroupListAdapter extends ListBaseAdapter<VideoEntry, GroupListAdapter.ViewHolder> {
        
        public GroupListAdapter(Context context) {
            super(context, R.layout.video_group_list_item);
        }

        @Override
        public ViewHolder getViewHolder() {
            return new ViewHolder();
        }

        @Override
        public void bindView(ViewHolder viewHolder, View convertView) {
            viewHolder.dir = (TextView)convertView.findViewById(R.id.dir_name);
            viewHolder.path = (TextView)convertView.findViewById(R.id.path);
        }

        @Override
        public void setViewContent(ViewHolder viewHolder,
                VideoEntry entry, View convertView, int position) {
            viewHolder.dir.setText(entry.mDir.getName());
            String info = mContext.getString(R.string.video_group_info, 
                    entry.mVideoList.size(), 
                    entry.mDir.getParentFile().getPath());
            viewHolder.path.setText(info);
        }
        
        public final class ViewHolder {
            TextView dir;
            TextView path;
        }
    }
}
