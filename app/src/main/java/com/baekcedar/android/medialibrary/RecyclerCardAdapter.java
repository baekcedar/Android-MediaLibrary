package com.baekcedar.android.medialibrary;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.baekcedar.android.medialibrary.R.id.imageView;

/**
 * Created by HM on 2016-10-04.
 */
public class RecyclerCardAdapter extends RecyclerView.Adapter<RecyclerCardAdapter.ViewHolder>{

    ArrayList<RecyclerData> datas;
    int itemLayout;
    Context context;

    // 생성자
    public RecyclerCardAdapter(ArrayList<RecyclerData> datas, int itemLayout, Context context){
        this.datas = datas;
        this.itemLayout = itemLayout;
        this.context = context;
    }


    // view 를 만들어서 홀더에 저장하는 역할
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }
    // listView getView 를 대체하는 함수
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RecyclerData data = datas.get(position);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //클릭시

            }
        });

        holder.textName.setText(data.name);
        holder.textTitle.setText(data.title);
        holder.itemView.setTag(data);
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.valueOf(data.albumId));
//        holder.img.setImageBitmap(getAlbumArtImage(context, Integer.parseInt(data.albumId)));
        Glide.with(context)
                .load(uri)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.img);

        setAnimation(holder.cardView, position);
    }
    int lastPosision = -1;
    public void setAnimation(View view,int position){
        if(position > lastPosision) {
            Animation ani = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            view.startAnimation(ani);
            lastPosision = position;
        }
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textTitle;
        CardView cardView;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);

            textName = (TextView) itemView.findViewById(R.id.textName);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            cardView = (CardView) itemView.findViewById(R.id.cardItem);
            img = (ImageView) itemView.findViewById(imageView);


        }
    }
    // 앨범이미지 가져오기
    public static final Bitmap getAlbumArtImage(Context p_Context, long p_AlbumId){
        Bitmap cover = null;
        ByteArrayOutputStream w_OutBuf = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int w_bFirst = 1;
        int w_nZeroCount = 0;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.valueOf(p_AlbumId));
        ContentResolver res = p_Context.getContentResolver();
        InputStream in;
        try {
            in = res.openInputStream(uri);
            while(true) {
                int count = in.read(buffer);
                if(count == -1){
                    break;
                }
                if(w_bFirst == 1){
                    //. 맨 첫 바이트토막을 쓰는 경우 앞에 붙은 0값들은 제외한다.
                    for(int i = 0; i < count; i++){
                        if(buffer[i] == 0){
                            w_nZeroCount++;
                        }
                        else{
                            break;
                        }
                    }
                    w_OutBuf.write(buffer, w_nZeroCount, count - w_nZeroCount);
                    w_bFirst = 0;
                }
                else {
                    w_OutBuf.write(buffer, 0, count);
                }
            }
            cover = BitmapFactory.decodeByteArray(w_OutBuf.toByteArray(), 0, w_OutBuf.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cover;
    }

}
