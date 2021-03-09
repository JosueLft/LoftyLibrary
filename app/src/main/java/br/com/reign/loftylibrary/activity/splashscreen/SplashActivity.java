package br.com.reign.loftylibrary.activity.splashscreen;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.manga.MangaActivity;
import br.com.reign.loftylibrary.adapter.HomePostAdapter;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.model.Post;
import br.com.reign.loftylibrary.model.User;
import br.com.reign.loftylibrary.utils.CompareChapterByDate;
import br.com.reign.loftylibrary.utils.MenuSelect;

public class SplashActivity extends AppCompatActivity {
    //initialize variable
    ImageView ivTop, ivLogo, ivWave, ivBottom;
    TextView appName;
    CharSequence charSequence;
    int index;
    long delay = 200;
    Handler handler = new Handler();

    private HomePostAdapter adapter;
    private ArrayList<Post> postItems = new ArrayList<>();
    private DatabaseReference dbProject;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listenerMangas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Assign variable
        ivTop = findViewById(R.id.iv_top);
        ivLogo = findViewById(R.id.iv_logo);
        ivWave = findViewById(R.id.iv_wave);
        ivBottom = findViewById(R.id.iv_bottom);
        appName = findViewById(R.id.app_name);
        initializeComponentes();
        loadContent();

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
                startActivity(new Intent(SplashActivity.this,
                        MangaActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                //Finish activity
                finish();
            }
        }, 5000);
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

    private void initializeComponentes() {
        // Adapters
        adapter = new HomePostAdapter(postItems, getApplicationContext(), "Card");
    }

    private void loadContent() {
        dbProject = dbReference.child("chapters").child("mangas");
        listenerMangas = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postItems.clear();
                CompareChapterByDate compare = new CompareChapterByDate();
                for(DataSnapshot mangas : dataSnapshot.getChildren()) {
                    MangaChapter manga = mangas.getValue(MangaChapter.class);
                    manga.setWorkTitle(mangas.getKey());
                    manga.setCover(String.valueOf(mangas.child("cover").getValue()));
                    for(DataSnapshot chapter : mangas.getChildren()) {
                        if(!chapter.getKey().equalsIgnoreCase("cover") && !chapter.getKey().equalsIgnoreCase("currentDate")) {
                            manga.setChapterTitle(chapter.getKey());
                            if(!(chapter.child("currentDate").getValue() == null) && !(chapter.child("currentDate").getValue().equals(""))) {
                                manga.setDate(Long.parseLong(String.valueOf(chapter.child("currentDate").getValue())));
                            }
                            postItems.add(new Post(manga.getWorkTitle(), manga.getChapterTitle(), manga.getCover(), manga.getDate()));
                            postItems.sort(compare);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        dbProject.addValueEventListener(listenerMangas);
    }
}