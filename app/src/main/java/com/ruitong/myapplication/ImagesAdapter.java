package com.ruitong.myapplication;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/10/3.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private List<Bitmap> datas;

//    private ClickIntface clickIntface;
//    public void setClickIntface(ClickIntface clickIntface){
//        this.clickIntface=clickIntface;
//    }

    public ImagesAdapter(List<Bitmap> datas) {
        this.datas = datas;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bitmaps_item,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        viewHolder.image.setImageBitmap(datas.get(position));
       ;
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
      class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;


        private ViewHolder(View view){
            super(view);
            image = (ImageView) view.findViewById(R.id.images);


        }
    }


}
