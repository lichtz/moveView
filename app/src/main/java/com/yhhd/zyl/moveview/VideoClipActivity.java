package com.yhhd.zyl.moveview;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.yhhd.zyl.moveview.mapping.MappingAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoClipActivity extends VideoBaseActivity {
    /**
     * 返回
     */
    private FrameLayout img_back;
    /**
     * 确认
     */
    private TextView txt_enter;
    /**
     * 视频bean
     */

    /**
     * 横向listview
     */
    private MyRecyclerView recyclerView;
    /**
     * 封面图按钮
     */
    private ImageView img_bg;
    /**
     * 阴影色块left
     */
    private ImageView img_left;
    /**
     * 阴影色块right
     */
    private ImageView img_right;
    /**
     * 显示时间
     */
    private TextView txt_time;
    /**
     * 封面容器
     */
    private RelativeLayout relative;
    /**
     * 进度条
     */
    private RelativeLayout relative1;
    /**
     * 视频播放
     */
    private VideoView videoView;
    /**
     * 数据集
     */
    private ArrayList<String> list;
    /**
     * 列表适配器
     */
    private VideoNewCutAdapter adapter;
    /**
     * 屏幕宽度
     */
    private int width;
    /**
     * 临时保存文件路径
     */
    private String savePath;

    /**
     * 最少多少秒
     */
    public static final int MIN_TIME = 5000;
    /**
     * 最大多少秒
     */
    public static final int MAX_TIME = 15000;
    /**
     * 屏幕中1像素占有多少毫秒
     */
    private float picture = 0;
    /**
     * 多少秒一帧
     */
    private float second_Z;

    /**
     * 是否中断线性
     */
    private boolean isThread = false;

    /**
     * 左边拖动按钮
     */
    private Button txt_left;
    /**
     * 右边拖动按钮
     */
    private Button txt_right;

    /**
     * 按下时X抽坐标
     */
    private float DownX;

    /**
     * 拖动条容器
     */
    private RelativeLayout.LayoutParams layoutParams_progress;
    /**
     * 阴影背景容器
     */
    private RelativeLayout.LayoutParams layoutParams_yin;
    /**
     * 拖动条的宽度
     */
    private int width_progress = 0;
    /**
     * 拖动条的间距
     */
    private int Margin_progress = 0;
    /**
     * 阴影框的宽度
     */
    private int width1_progress = 0;

    /**
     * 不能超过右边多少
     */
    private int right_margin = 0;
    /**
     * 所有图片长度
     */
    private int img_widthAll = 0;
    /**
     * 最少保留的多少秒长度
     */
    private int last_length = 0;
    /**
     * 左边拉了多少
     */
    private int left_lenth = 0;
    /**
     * 滚动的长度
     */
    private int Scroll_lenth = 0;

    //截图
    private File file;
    private Dialog dialog;
    private int cutLineWidth;
   private boolean isFristNext = true;
    private FrameLayout imageViewDan;
    private RelativeLayout.LayoutParams imageDanLayoutParams;
    float vValue = 0;
    private RecyclerView mappRecycleView;
    private ImageView mappingHideButton;
    private MappingAdapter mappingAdapter;
    private MoveLayout moveLayout;
    //测试
    private  int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clip);
        initView();
        initData();
        initListener();
        init();


    }

    //点击事件
    private void initListener() {
        adapter.setOnClickListener(new VideoNewCutAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isFristNext) {
                    sendVideo((position + 1) * view.getWidth());
                }
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Util.RecursionDeleteFile(file);
                    }
                }).start();

                finish();
            }
        });

        //下一步处理
        txt_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFristNext){
                    txt_left.setClickable(false);
                    txt_right.setClickable(false);

                    isFristNext=false;
                    imageViewDan.setVisibility(View.VISIBLE);
                    imageViewDan.setClickable(true);
                    imageDanLayoutParams = (RelativeLayout.LayoutParams) imageViewDan.getLayoutParams();
                    imageDanLayoutParams.width = (int) (1000/picture);
                    imageViewDan.setLayoutParams(imageDanLayoutParams);


                }else {
                    layoutParams_progress = (RelativeLayout.LayoutParams) relative1.getLayoutParams();
                    String address = CONSTANTS.FINAL_VIDEO_PATH;

                    File file = new File(address);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    savePath = CONSTANTS.FINAL_VIDEO_PATH + System.currentTimeMillis() + ".mp4";
                    cutLineWidth = layoutParams_progress.width;

                    VideoClip videoClip = new VideoClip();
                    videoClip.execute();
                }
            }
        });
        //播放
        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    img_bg.setVisibility(View.VISIBLE);
                    videoView.pause();
                    handler.removeCallbacks(runnable);
                } else {
                    videoView.setVisibility(View.VISIBLE);
                    img_bg.setVisibility(View.GONE);
                    videoView.start();
                    layoutParams_progress = (RelativeLayout.LayoutParams) relative1.getLayoutParams();
                    handler.postDelayed(runnable, (long) (layoutParams_progress.width * picture) + 500);
                }
            }
        });


        //弹幕 添加指示器
        imageViewDan.setOnTouchListener(new View.OnTouchListener() {

            private float v;
            private float danDownX;


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) relative1.getLayoutParams();
                int width = layoutParams1.width;
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        danDownX = motionEvent.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        v = vValue+motionEvent.getRawX() - danDownX;


                        if (v <=0){
                            imageDanLayoutParams.setMarginStart(0);
                        }else if (v >=width-imageDanLayoutParams.width){
                            imageDanLayoutParams.setMarginStart(width-imageDanLayoutParams.width);
                        }else {
                            imageDanLayoutParams.setMarginStart((int) v);
                        }

                        imageViewDan.setLayoutParams(imageDanLayoutParams);


                        break;
                    case MotionEvent.ACTION_UP:
                        vValue = v;
                        break;
                }
                return false;
            }
        });

        //左拖动
        txt_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DownX = motionEvent.getRawX();

                        layoutParams_progress = (RelativeLayout.LayoutParams) relative1.getLayoutParams();
                        layoutParams_yin = (RelativeLayout.LayoutParams) img_left.getLayoutParams();

                        width_progress = layoutParams_progress.width;
                        Margin_progress = layoutParams_progress.leftMargin;
                        width1_progress = layoutParams_yin.width;

                        break;
                    case MotionEvent.ACTION_MOVE:

                        LeftMoveLayout(motionEvent.getRawX() - DownX, motionEvent.getRawX());

                        break;
                    case MotionEvent.ACTION_UP:

                        sendVideo();

                        layoutParams_progress = null;
                        layoutParams_yin = null;

                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        /** 右边拖动按钮 */
        txt_right.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DownX = event.getRawX();

                        layoutParams_progress = (RelativeLayout.LayoutParams) relative1.getLayoutParams();
                        layoutParams_yin = (RelativeLayout.LayoutParams) img_right.getLayoutParams();

                        width_progress = layoutParams_progress.width;
                        Margin_progress = layoutParams_progress.rightMargin;
                        width1_progress = layoutParams_yin.width;

                        break;
                    case MotionEvent.ACTION_MOVE:

                        RightMoveLayout(DownX - event.getRawX());

                        break;
                    case MotionEvent.ACTION_UP:
                        layoutParams_progress = null;
                        layoutParams_yin = null;
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        /** 视频播放完回调 */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                img_bg.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
            }
        });

        /**
         * 滚动监听
         */
        recyclerView.setOnItemScrollChangeListener(new MyRecyclerView.OnItemScrollChangeListener() {
            @Override
            public void onChange(View view, int position) {
                Scroll_lenth = position * view.getWidth() - view.getLeft();
                if (Scroll_lenth <= 0) {
                    Scroll_lenth = 0;
                }
                //变滑动边更新
                sendVideo();
            }

            @Override
            public void onChangeState(int state) {
                if (state == 0) {
                    sendVideo();
                }
            }
        });
        //测试

        mappingAdapter.setOnitemClickListener(new MappingAdapter.onMapItemClickListener() {
            @Override
            public void setOnItemclick(View view, int postion) {
                final ImageView imageView = new ImageView(VideoClipActivity.this);
                imageView.setTag(i);
                imageView.setImageResource(R.mipmap.dd);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(250, 250);
                imageView.setLeft((width-250)/2);
                imageView.setTop(width/2);
                imageView.setLayoutParams(layoutParams);

                moveLayout.setView(imageView,false,i);
              moveLayout.addView(imageView);

                i++;
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int tag = (int) imageView.getTag();

                        moveLayout.removeView(tag);
                        moveLayout.removeView(imageView);

                        return true;
                    }
                });

            }
        });
    }

    /**
     * 向右边
     */
    private void LeftMoveLayout(float MoveX, float X) {
        if (layoutParams_progress != null && layoutParams_yin != null) {
            if (Margin_progress + (int) MoveX > 0 && width_progress - (int) MoveX > last_length) {
                layoutParams_progress.width = width_progress - (int) MoveX;
                layoutParams_progress.leftMargin = Margin_progress + (int) MoveX;
                layoutParams_yin.width = width1_progress + (int) MoveX;

                relative1.setLayoutParams(layoutParams_progress);
                img_left.setLayoutParams(layoutParams_yin);

                txt_time.setText((float) (Math.round((layoutParams_progress.width * picture / 1000) * 10)) / 10 + " s");

                left_lenth = layoutParams_yin.width;
            }
        }
    }

    /**
     * 向左边拉
     *
     * @param MoveX
     * @version 1.0
     * @createTime 2015年6月18日, 上午9:45:16
     * @updateTime 2015年6月18日, 上午9:45:16
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    private void RightMoveLayout(float MoveX) {
        if (layoutParams_progress != null && layoutParams_yin != null) {
            if (Margin_progress + (int) MoveX > right_margin && width_progress - (int) MoveX > last_length) {
                layoutParams_progress.width = width_progress - (int) MoveX;
                layoutParams_progress.rightMargin = Margin_progress + (int) MoveX;
                layoutParams_yin.width = width1_progress + (int) MoveX;

                txt_time.setText((float) (Math.round((layoutParams_progress.width * picture / 1000) * 10)) / 10 + " s");

                relative1.setLayoutParams(layoutParams_progress);
                img_right.setLayoutParams(layoutParams_yin);
            }
        }

    }


    private void init() {
        //创建文件夹
        file = new File(CONSTANTS.TEMP_PIC_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        recyclerView.setAdapter(adapter);
        String path = CONSTANTS.FINAL_VIDEO_PATH+"p.mp4";
        videoView.setVideoPath(path);
//        videoView.setVideoURI(Uri.parse("http://weizitest-10076841.video.myqcloud.com/ppp.mp4.f20.mp4"));
        videoView.requestFocus();
        //1像素多少毫秒
        picture = (float) MAX_TIME / (float) width;
        second_Z = (float) MAX_TIME / 1000f / ((float) width / (float) Util.dip2px(VideoClipActivity.this, 20));
        ;
        getBitmapsFromVideo(path);
    }

    private void getBitmapsFromVideo(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(path);
                String sDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int duration = Integer.parseInt(sDuration);

                //视频长度
                int seconds = duration / 1000;
                Message message = handler.obtainMessage();
                message.what = 2;
                message.arg1 = seconds;
                handler.sendMessage(message);
                Bitmap bitmap;
                //得到每一刻的bitmap
                int index = 0;
                for (float f = second_Z; f <= (float) seconds; f += second_Z) {
                    if (isThread) {
                        return;
                    }
                    try {
                        bitmap = mediaMetadataRetriever.getFrameAtTime((long) (f * 1000 * 1000), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        String path = CONSTANTS.TEMP_PIC_PATH + System.currentTimeMillis() + ".jpg";

                        FileOutputStream fos = null;
                        fos = new FileOutputStream(path);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        list.add(path);
                        Message message1 = handler.obtainMessage();
                        message1.what = 1;
                        message1.arg1 = index;
                        handler.sendMessage(message1);
                        index++;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    private void initData() {
//        bean = (VideoSelectBean) getIntent().getSerializableExtra("videoBean");
    }

    private void initView() {
        img_back = (FrameLayout) findViewById(R.id.view_title);
        txt_enter = (TextView) findViewById(R.id.video_new_txt_enter);
        recyclerView = (MyRecyclerView) findViewById(R.id.recyclerview_horizontal);
        videoView = (VideoView) findViewById(R.id.video_new_cut_videoview);
        img_bg = (ImageView) findViewById(R.id.video_new_cut_img_bg);
        img_left = (ImageView) findViewById(R.id.video_new_cut_img_left);
        img_right = (ImageView) findViewById(R.id.video_new_cut_img_right);
        relative = (RelativeLayout) findViewById(R.id.video_new_cut_relative);
        txt_time = (TextView) findViewById(R.id.video_new_cut_txt_time);
        relative1 = (RelativeLayout) findViewById(R.id.video_new_cut_relative1);
        imageViewDan = (FrameLayout) findViewById(R.id.sss);
        imageViewDan.setVisibility(View.GONE);
        imageViewDan.setClickable(false);
        mappRecycleView = (RecyclerView) findViewById(R.id.mapping_recycleview);
        mappingHideButton = (ImageView) findViewById(R.id.mappingHideButton);
        moveLayout = (MoveLayout) findViewById(R.id.move_group);

        txt_left = (Button) findViewById(R.id.video_new_cut_txt_left);
        txt_right = (Button) findViewById(R.id.video_new_cut_txt_right);
        width = getWindowManager().getDefaultDisplay().getWidth();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relative.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width*4/3;
        relative.setLayoutParams(layoutParams);
        width = getWindowManager().getDefaultDisplay().getWidth() - Util.dip2px(VideoClipActivity.this, 20);
        //创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        mappRecycleView.setLayoutManager(gridLayoutManager);
        mappingAdapter = new MappingAdapter(VideoClipActivity.this);
        mappRecycleView.setAdapter(mappingAdapter);
        list = new ArrayList<>();
        adapter = new VideoNewCutAdapter(list);

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                adapter.notifyItemInserted(msg.arg1);
                adapter.notifyDataSetChanged();
                if (msg.arg1 == 0) {
//                    sendVideo(Util.dip2px(VideoClipActivity.this, 0));
                }
            } else if (msg.what == 2) {
                img_widthAll = (int) (msg.arg1 * 1000 / picture);
                last_length = (int) (MIN_TIME / picture);
                if (img_widthAll < width) {
                    right_margin = width - img_widthAll;
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) img_right.getLayoutParams();
                    layoutParams.width = width - img_widthAll;
                    img_right.setLayoutParams(layoutParams);
                    //分割线
                    layoutParams_progress = (RelativeLayout.LayoutParams) relative1.getLayoutParams();
                    layoutParams_progress.width = img_widthAll;
                    layoutParams_progress.rightMargin = width - img_widthAll;
                    relative1.setLayoutParams(layoutParams_progress);
                    txt_time.setText(msg.arg1 + ".0 s");
                } else {
                    img_widthAll = width;
                    layoutParams_progress = (RelativeLayout.LayoutParams) relative1.getLayoutParams();
                    layoutParams_progress.width = width;
                    relative1.setLayoutParams(layoutParams_progress);
                    txt_time.setText((MAX_TIME / 1000) + ".0 s");
                }
            }
        }


    };

    /**
     * 移动起始播放位置
     *
     * @param i 长度
     */
    private void sendVideo(int i) {

        if (!videoView.isShown()) {
            videoView.setVisibility(View.VISIBLE);
        }
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        if (!img_bg.isShown()) {
            img_bg.setVisibility(View.VISIBLE);
        }
        handler.removeCallbacks(runnable);
        videoView.seekTo((int) (i * picture));

    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (!img_bg.isShown()) {
                img_bg.setVisibility(View.VISIBLE);
            }
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        }
    };

    /**
     * 移动起始播放位置
     */
    private void sendVideo() {
        if (!videoView.isShown()) {
            videoView.setVisibility(View.VISIBLE);
        }
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        if (!img_bg.isShown()) {
            img_bg.setVisibility(View.VISIBLE);
        }

        handler.removeCallbacks(runnable);

        videoView.seekTo((int) ((Scroll_lenth + left_lenth) * picture));
    }




    private class VideoClip extends AsyncTask<Void, Integer, Void> {
        private ProgressBar bar;
        private TextView progress;

        @Override
        protected void onPreExecute() {
            //
            //创建进度条
//            dialog = new Dialog(VideoClipActivity.this, R.style.Dialog_loading_noDim);
//            Window window = dialog.getWindow();
//            WindowManager.LayoutParams attributes = window.getAttributes();
//            attributes.width = (int) (getResources().getDisplayMetrics().density * 240);
//            attributes.height = (int) (getResources().getDisplayMetrics().density * 80);
//            attributes.gravity = Gravity.CENTER;
//            window.setAttributes(attributes);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setContentView(R.layout.activity_recorder_progress);
//            progress = (TextView) dialog.findViewById(R.id.recorder_progress_progresstext);
//            bar = (ProgressBar) dialog.findViewById(R.id.recorder_progress_progressbar);
//            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setText(values[0] + "%");
            bar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress(70);
//            try {
////                Util.startTrim(new File(bean.getPath()), new File(savePath), (long) ((Scroll_lenth + left_lenth) * picture), (long) ((Scroll_lenth
////                        + left_lenth + cutLineWidth) * picture));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            publishProgress(100);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            Toast.makeText(VideoClipActivity.this, "wancheng" + savePath, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isThread = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Util.RecursionDeleteFile(file);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
//        Util.showDialog(VideoClipActivity.this, "提示", "确定要放弃本视频吗？" , 2, new Handler(){
//            @Override
//            public void dispatchMessage(Message msg) {
//                if(msg.what == 1){
//                    handler.removeCallbacksAndMessages(null);
//                    String path = bean.getPath();
//                    File file = new File(path);
//
//                    if(file != null && file.exists() ) {
//                        file.delete();
//                    }
//                   finish();
//                }
//
//
//            }
//        });
        if (!isFristNext){
            isFristNext =true;
            txt_right.setClickable(true);
            txt_left.setClickable(true);
            imageDanLayoutParams.setMarginStart(0);
            imageViewDan.setLayoutParams(imageDanLayoutParams);
            vValue = 0;
            imageViewDan.setVisibility(View.GONE);


        }
    }






    /**
     * 处理视频编辑
     */
    private List<String> imgList = new ArrayList<>();


    public void onDanImg(View view) {
        DanDao();
        NetDao();

setRecycleDisplay();

        }

    private void NetDao() {
        imgList.add("http://img06.tooopen.com/images/20160815/tooopen_sy_175638814153.jpg");
        imgList.add("http://img06.tooopen.com/images/20161120/tooopen_sy_187242348957.jpg");
        imgList.add("http://img06.tooopen.com/images/20170120/tooopen_sy_197334647747.jpg");
        mappingAdapter.setDataList(imgList);

    }

    public void onDanText(View view) {

        DanDao();
        setRecycleDisplay();
    }
    public void mappingHide(View view) {
        setRecycleHide();


    }



private void DanDao(){
    mappingHideButton.setClickable(true);
    txt_left.setClickable(false);
    txt_right.setClickable(false);

    isFristNext=false;
    imageViewDan.setVisibility(View.VISIBLE);
    imageViewDan.setClickable(true);
    imageDanLayoutParams = (RelativeLayout.LayoutParams) imageViewDan.getLayoutParams();
    imageDanLayoutParams.width = (int) (1000/picture);
    imageViewDan.setLayoutParams(imageDanLayoutParams);
}

private void  setRecycleDisplay(){
    mappRecycleView.setVisibility(View.VISIBLE);
    TranslateAnimation myAnimation_Translate = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 1,
            Animation.RELATIVE_TO_PARENT, 0,
            Animation.RELATIVE_TO_PARENT, 0,
            Animation.RELATIVE_TO_PARENT, 0);

    myAnimation_Translate.setDuration(500);
    myAnimation_Translate.setInterpolator(AnimationUtils
            .loadInterpolator(VideoClipActivity.this,
                    android.R.anim.accelerate_decelerate_interpolator));
    mappRecycleView.startAnimation(myAnimation_Translate);
}

    private void setRecycleHide(){
        TranslateAnimation myAnimation_Translate = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0);

        myAnimation_Translate.setDuration(500);
        myAnimation_Translate.setInterpolator(AnimationUtils
                .loadInterpolator(VideoClipActivity.this,
                        android.R.anim.accelerate_decelerate_interpolator));
        mappRecycleView.startAnimation(myAnimation_Translate);
        mappRecycleView.setVisibility(View.GONE);
        mappingHideButton.setClickable(false);
    }


}
