package com.baekcedar.android.medialibrary;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public  ArrayList<RecyclerData> datas = null;
    private final static int REQUEST_CODE =100 ; //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datas = new ArrayList<>();

        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            initData();
        }
        else {
            checkPermissions();
        }
    }
    // 권한이 허용되면 실행
    public void initData(){
        datas = getMusicInfo(); // 뮤직 데이터 셋팅
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerCardAdapter adapter = new RecyclerCardAdapter(datas,R.layout.card_item,this);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }
    // 뮤직 데이터 셋팅
    public ArrayList<RecyclerData> getMusicInfo(){
        ArrayList<RecyclerData> datas = new ArrayList<>();

        String [] projections = {
                MediaStore.Audio.Media._ID,         // 노래아이디
                MediaStore.Audio.Media.ALBUM_ID,    // 앨범아이디
                MediaStore.Audio.Media.TITLE,       // 제목
                MediaStore.Audio.Media.ARTIST,      // 가수
        };
        //getContentResolver().query(주소, 검색해올 컴럼명들, 조건절, 조건절에 매핑되는 값, 정렬)
        Cursor cursor = getContentResolver().query(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            projections,
                            null, null, null
                        );
        /*
            - uri           : content ://스키마 형태로 정해져 있는 곳의 데이터를 가져온다.
            - projection    : 가져올 컬럼 이름들의 배열, null 을 입력하면 모든값을 가져온다.
            - selection     : 조건절(where)에 해당하는 내용
            - selectionArgs : 조건절이 preparedstatement 형태일 때 ? 에 매핑되는 값을 배열
            - sort order    : 정렬 조건
         */
          if( cursor != null ){
             while(cursor.moveToNext()){
                 RecyclerData data = new RecyclerData();
                 int idx;
                 // 데이터의 가수 이름을 입력
                 // 1. 가수 이름 컬럼의 순서 (index)를 가져온다.
                 idx = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                 // 2. 해당 index를 가진 컬럼의 실제값을 가져온다.
                 data.artist = cursor.getString(idx);

                 idx = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                 data.title = cursor.getString(idx);
                 idx = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                 data.albumId = cursor.getString(idx);
                 idx = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                 data.musicId = cursor.getString(idx);
                 datas.add(data);
             }
              cursor.close(); // *반드시 종료*
          }
        return datas;
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // 쓰기 권한이 없으면 로직 처리
            // 중간에 권한 내용에 대한 알림을 처리하는 함수
            // shouldShowRequestPermissionRationale();
            String permissionArray[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissionArray, REQUEST_CODE);
        }else{
            // 쓰기권한이 있으면 파일생성
            initData();
        }
    }
    // 제네레이터 > 오버리아드 메소드 > onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case  REQUEST_CODE :
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initData();
                }
                break;
        }
    }

}
