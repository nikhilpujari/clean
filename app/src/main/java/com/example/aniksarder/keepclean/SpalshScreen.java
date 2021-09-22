package com.example.aniksarder.keepclean;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SpalshScreen extends AppCompatActivity {

    private ImageView SimageView;
    private Animation animation;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_spalsh_screen);
        SimageView = findViewById(R.id.spalash_screen);
        animation = AnimationUtils.loadAnimation(context,R.anim.mytransition);
        SimageView.startAnimation(animation);
        final Intent intent = new Intent(context,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }

        };
        timer.start();
    }
}
