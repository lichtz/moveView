package com.yhhd.zyl.moveview;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private MoveLayout moveLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.ll);
        moveLayout = (MoveLayout) findViewById(R.id.mov);


        videoView.setVideoURI(Uri.parse("http://weizitest-10076841.video.myqcloud.com/ppp.mp4.f0.mp4"));
        videoView.start();

    }
int i = 0;
    public void on(View view) {
        final TextView textView = new TextView(this);
        textView.setBackgroundColor(Color.RED);
      textView.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        textView.setTag(i);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(100, 150);
        textView.setLayoutParams(layoutParams);

        moveLayout.setView(textView,false,i);
        moveLayout.addView(textView);
        i++;
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int tag = (int) textView.getTag();

                moveLayout.removeView(tag);
                moveLayout.removeView(textView);

                return true;
            }
        });


    }
}
