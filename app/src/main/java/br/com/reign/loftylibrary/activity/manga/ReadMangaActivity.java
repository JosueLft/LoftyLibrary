package br.com.reign.loftylibrary.activity.manga;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xwray.groupie.GroupAdapter;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.MangaChapterAdapter;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.utils.SortPages;

public class ReadMangaActivity extends AppCompatActivity {

    private MangaChapterAdapter mangaChapterAdapter;
    private String chapterTitle;
    private String workTitle;
    private RecyclerView rvMangaContent;
    private ArrayList<MangaChapter> chapterPages = new ArrayList<>();
    private ArrayList<MangaChapter> chapters = new ArrayList<>();

    private Button btnNextChapter;
    private Button btnPreviousChapter;
    private Button btnAddLibrary;
    private TextView txtCurrentChapterTitle;
    private int chapterIndex;

    // Firebase
    private DatabaseReference dbReference;

    // Google AdMob
    private Button btnCloseAds;
    AdView adsPainel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_manga);

        initializeComponents();
        closeAds();
        receivingChapter();
        loadContent(chapterTitle);
        nextChapter();
    }

    private void initializeComponents() {
        txtCurrentChapterTitle = findViewById(R.id.txtCurrentChapterTitle);
        //RecyclerView
        rvMangaContent = findViewById(R.id.rvMangaContent);
        rvMangaContent.setLayoutManager(new LinearLayoutManager(this));
        //Adapter
        mangaChapterAdapter = new MangaChapterAdapter(chapterPages, this);
        rvMangaContent.setAdapter(mangaChapterAdapter);
        // Controllers
        btnNextChapter = findViewById(R.id.btnNextChapter);
        btnPreviousChapter = findViewById(R.id.btnPreviousChapter);
        btnAddLibrary = findViewById(R.id.btnAddLibrary);

        // Google AdMob
        btnCloseAds = findViewById(R.id.btnCloseAds);
        adsPainel = new AdView(this);
        adsPainel.setAdSize(AdSize.BANNER);
        adsPainel.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        initAdMob();
    }

    private void receivingChapter() {
        workTitle = getIntent().getExtras().getString("WorkTitle");
        chapterTitle = getIntent().getExtras().getString("ChapterTitle");
        txtCurrentChapterTitle.setText(chapterTitle);
    }

    private void loadContent(final String chapterTitle) {

        dbReference = FirebaseDatabase.getInstance().getReference()
                .child("chapters")
                .child("mangas")
                .child(workTitle)
                .child(chapterTitle);

        dbReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapterPages.clear();
                SortPages compare = new SortPages();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(!(data.getKey().equalsIgnoreCase("currentDate"))) {
                        chapterPages.add(new MangaChapter(Integer.parseInt(String.valueOf(data.getKey())), String.valueOf(data.getValue())));
                        chapterPages.sort(compare);
                    }
                }

                mangaChapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nextChapter() {
        dbReference = FirebaseDatabase.getInstance().getReference()
                .child("chapters")
                .child("mangas")
                .child(workTitle);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapters.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    if(!(data.getKey().equalsIgnoreCase("cover")) && !(data.getKey().equalsIgnoreCase("currentDate"))) {
                        chapters.add(new MangaChapter(data.getKey()));
                    }
                }

                for(int i = 0; i < chapters.size(); i++) {
                    if(chapters.get(i).getChapterTitle().equals(chapterTitle)) {
                        chapterIndex = i;
                    }
                }
                if(chapterIndex == (chapters.size() - 1)) {
                    btnNextChapter.setVisibility(View.GONE);
                } else if(chapterIndex == 0) {
                    btnPreviousChapter.setVisibility(View.GONE);
                } else if(chapterIndex < (chapters.size() - 1)){
                    btnNextChapter.setVisibility(View.VISIBLE);
                } else if(chapterIndex != 0){
                    btnPreviousChapter.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnNextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadContent(chapters.get(chapterIndex + 1).getChapterTitle());
                chapterIndex += 1;
                if(chapterIndex == (chapters.size() - 1)) {
                    btnNextChapter.setVisibility(View.GONE);
                } else if(chapterIndex == 0) {
                    btnPreviousChapter.setVisibility(View.GONE);
                } else if(chapterIndex < (chapters.size() - 1)){
                    btnNextChapter.setVisibility(View.VISIBLE);
                } else if(chapterIndex != 0){
                    btnPreviousChapter.setVisibility(View.VISIBLE);
                }
            }
        });

        btnPreviousChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chapterIndex != 0) {
                    loadContent(chapters.get(chapterIndex - 1).getChapterTitle());
                    chapterIndex -= 1;
                }
                if(chapterIndex == (chapters.size() - 1)) {
                    btnNextChapter.setVisibility(View.GONE);
                } else if(chapterIndex == 0) {
                    btnPreviousChapter.setVisibility(View.GONE);
                } else if(chapterIndex < (chapters.size() - 1)){
                    btnNextChapter.setVisibility(View.VISIBLE);
                } else if(chapterIndex != 0){
                    btnPreviousChapter.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adsPainel = findViewById(R.id.adsPainel);
        AdRequest adRequest = new AdRequest.Builder().build();
        adsPainel.loadAd(adRequest);
    }
    private void closeAds() {
        btnCloseAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adsPainel.setVisibility(View.GONE);
                btnCloseAds.setVisibility(View.GONE);
            }
        });
    }
}