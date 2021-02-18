package br.com.reign.loftylibrary.activity.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.MainActivity;

public class SplashsActivity extends AppCompatActivity {
    //initialize variable
    ImageView ivTop, ivLogo, ivWave, ivBottom;
    TextView appName;
    CharSequence charSequence;
    int index;
    long delay = 200;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashs);

        //Assign variable
        ivTop = findViewById(R.id.iv_top);
        ivLogo = findViewById(R.id.iv_logo);
        ivWave = findViewById(R.id.iv_wave);
        ivBottom = findViewById(R.id.iv_bottom);
        appName = findViewById(R.id.app_name);

        // Set full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize top animation
        Animation animationTop = AnimationUtils.loadAnimation(this,
                R.anim.top_wave);

        //Start top animation
        ivTop.setAnimation(animationTop);

        // Initialize object animator
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                ivLogo,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        );
        //Set duration
        objectAnimator.setDuration(500);
        //Set Repeat count
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //Set repeat mode
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        //Start animator
        objectAnimator.start();

        // set animate text
        animatText("Lofty Library");

        // Initialize bottom animation
        Animation animationBottom = AnimationUtils.loadAnimation(this,
                R.anim.bottom_wave);
        //Start bottom animation
        ivBottom.setAnimation(animationBottom);

        // Initialize Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Redirect to main activity
                startActivity(new Intent(SplashsActivity.this,
                        MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                //Finish activity
                finish();
            }
        }, 4000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // When runnable is run
            // Set Text
            appName.setText(charSequence.subSequence(0, index++));
            // Check condition
            if(index <= charSequence.length()) {
                //When index is equal to text length
                //Run handler
                handler.postDelayed(runnable,delay);
            }
        }
    };

    // Create animated text method
    public void animatText(CharSequence cs) {
        // Set text
        charSequence = cs;
        //Clear index
        index = 0;
        // Clear text
        appName.setText("");
        //Remove call back
        handler.removeCallbacks(runnable);
        //Run Handler
        handler.postDelayed(runnable, delay);
    }
}