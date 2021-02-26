package br.com.reign.loftylibrary.activity.novel;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.account.LoginActivity;
import br.com.reign.loftylibrary.activity.manga.ReadMangaActivity;
import br.com.reign.loftylibrary.adapter.MangaChapterAdapter;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.model.NovelChapter;
import br.com.reign.loftylibrary.utils.SortPages;

public class ReadNovelActivity extends AppCompatActivity {

    private String chapterTitle;
    private ArrayList<NovelChapter> chapters = new ArrayList<>();
    private String workTitle;
    private TextView txtNovelTitle;
    private TextView txtByTranslated;
    private TextView txtContentChapter;
    private NovelChapter novel = new NovelChapter();
    private Button btnNextChapter;
    private Button btnPreviousChapter;
    private Button btnAddLibrary;
    private int chapterIndex;

    // Firebase
    private DatabaseReference dbReference;

    // Google AdMob
    private Button btnCloseAds;
    private AdView adsPainel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_novel);

        initializeComponents();
        closeAds();
        receivingChapter();
        loadContent(chapterTitle);
        nextChapter();
        addLibrary();
    }

    private void initializeComponents() {
        // TextView
        txtNovelTitle = findViewById(R.id.txtNovelTitle);
        txtByTranslated = findViewById(R.id.txtByTranslated);
        txtContentChapter = findViewById(R.id.txtContentChapter);
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
    }

    private void loadContent(final String chapterTitle) {
        FirebaseDatabase.getInstance().getReference()
                .child("chapters")
                .child("novels")
                .child(workTitle)
                .child(chapterTitle).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtNovelTitle.setText(dataSnapshot.getKey());
                txtByTranslated.setText(String.valueOf(dataSnapshot.child("translatedBy").getValue()));
                txtContentChapter.setText(String.valueOf(dataSnapshot.child("contentChapter").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nextChapter() {
        FirebaseDatabase.getInstance().getReference()
                .child("chapters")
                .child("novels")
                .child(workTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chapters.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!(data.getKey().equalsIgnoreCase("cover")) && !(data.getKey().equalsIgnoreCase("currentDate"))) {
                        chapters.add(new NovelChapter(data.getKey()));
                    }
                }
                for(int i = 0; i < chapters.size(); i++) {
                    if(chapters.get(i).getChapterTitle().equals(chapterTitle)) {
                        chapterIndex = i;
                        Toast.makeText(ReadNovelActivity.this, "indice: " + chapterIndex, Toast.LENGTH_LONG).show();
                    }
                }
                if(chapterIndex == (chapters.size() + 1)) {
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
                chapterTitle = chapters.get(chapterIndex + 1).getChapterTitle();
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
                    chapterTitle = chapters.get(chapterIndex - 1).getChapterTitle();
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

    private void addLibrary() {
        btnAddLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getUid() == null) {
                    Intent intent = new Intent(ReadNovelActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    final String id = FirebaseAuth.getInstance().getUid();
                    dbReference = FirebaseDatabase.getInstance().getReference()
                            .child("chapters")
                            .child("novels")
                            .child(workTitle);
                    dbReference.addValueEventListener(new ValueEventListener() { // metodo utilizado para salvar um capitulo na biblioteca do usuario atual
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            novel.setCover(String.valueOf(dataSnapshot.child("cover").getValue()));
                            long currentDate = System.currentTimeMillis();
                            novel.setChapterTitle(chapterTitle);
                            novel.setDate(currentDate);
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(id)
                                    .collection("library")
                                    .document(workTitle)
                                    .set(novel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(
                                                    ReadNovelActivity.this,
                                                    "Adicionado a biblioteca com sucesso",
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

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