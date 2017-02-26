package com.yhhd.zyl.moveview.mapping;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yhhd.zyl.moveview.R;

import java.util.List;

/**
 * Author:Liang
 * Email:zhaoyongliangxny@qq.com
 * Created 2017年02月25日 11:49
 * Description:
 */

public class MappingAdapter  extends RecyclerView.Adapter<MappingAdapter.ViewHolder>{

    private List<String> mList ;
    private Context context;
    public  void  setDataList(List<String> mDataList ){
        mList = mDataList;
        notifyDataSetChanged();

    }

    public MappingAdapter(Context context) {
        this.context =context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.item_mapping, null);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String mappingPath = mList.get(position);
        holder.mappingImage.setImageResource(R.mipmap.dd);
//        Glide.with(context).load("http://b.hiphotos.baidu.com/baike/w%3D268%3Bg%3D0/sign=92e00c9b8f5494ee8722081f15ce87c3/29381f30e924b899c83ff41c6d061d950a7bf697.jpg").into(holder.mappingImage);
    }

    @Override
    public int getItemCount() {
        return mList!=null?mList.size():0;
    }


    public class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private  ImageView mappingImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mappingImage = (ImageView) itemView.findViewById(R.id.mapping_item);
            mappingImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mapItemClickListener.setOnItemclick(mappingImage,getPosition());

        }
    }


    public   interface onMapItemClickListener{
        void  setOnItemclick(View view,int postion);

    }
    private onMapItemClickListener mapItemClickListener;
    public void setOnitemClickListener(onMapItemClickListener itemClickListener){
      mapItemClickListener= itemClickListener;

    }
}
