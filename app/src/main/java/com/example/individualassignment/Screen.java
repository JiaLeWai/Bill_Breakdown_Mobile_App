package com.example.individualassignment;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Screen extends AppCompatActivity {

    ImageView appIcon;
    Animation ani;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        ani = AnimationUtils.loadAnimation(this, R.anim.animation);

        appIcon = findViewById(R.id.AppIcon);

        appIcon.setAnimation(ani);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Screen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}