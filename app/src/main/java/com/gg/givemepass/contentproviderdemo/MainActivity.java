package com.gg.givemepass.contentproviderdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private Map<String, List<FolderData>> contentMap;
    private ExecutorService service;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        contentMap = new HashMap<>();
        service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                getAlbumData();
                if(contentMap.size() > 0){
                    Set<String> set = contentMap.keySet();
                    int i = 0;
                    for(String key : set) {
                        if(i == 0) {
                            List<FolderData> list = contentMap.get(key);
                            adapter.setData(list);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                        i++;
                    }
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                return;
            }
        }
    }

    private void getAlbumData(){
        try {
            final String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED;

            Cursor imagecursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy + " DESC");
            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int bucketColumn = imagecursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    String picPath = imagecursor.getString(dataColumnIndex);
                    String bucket = imagecursor.getString(bucketColumn);

                    List<FolderData> mFolderDataList;
                    FolderData data = new FolderData();
                    data.setPath(picPath);
                    data.setBucketName(bucket);
                    mFolderDataList = contentMap.get(bucket);

                    //不同資料夾
                    if(!contentMap.containsKey(bucket)){
                        mFolderDataList = new ArrayList<>();
                        mFolderDataList.add(data);
                        contentMap.put(bucket, mFolderDataList);
                    } else{ //同一資料夾
                        mFolderDataList.add(data);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private List<FolderData> mData;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public ViewHolder(View v) {
                super(v);
                imageView = (ImageView) v.findViewById(R.id.recyclerview_item_img);
            }
        }

        public RecyclerViewAdapter() {
            mData = new ArrayList<>();
        }

        public void setData(List<FolderData> data){
            mData = data;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Glide.with(MainActivity.this)
                    .load(mData.get(position).getPath())
                    .centerCrop()//中心切圖, 會填滿
                    .fitCenter()//中心fit, 以原本圖片的長寬為主
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
