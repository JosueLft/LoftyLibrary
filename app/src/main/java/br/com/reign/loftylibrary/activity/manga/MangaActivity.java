package br.com.reign.loftylibrary.activity.manga;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.account.LoginActivity;
import br.com.reign.loftylibrary.activity.catalog.CatalogActivity;
import br.com.reign.loftylibrary.activity.catalog.WorkActivity;
import br.com.reign.loftylibrary.activity.library.LibraryActivity;
import br.com.reign.loftylibrary.activity.notifications.AppApplication;
import br.com.reign.loftylibrary.activity.notifications.Notification;
import br.com.reign.loftylibrary.activity.novel.NovelActivity;
import br.com.reign.loftylibrary.activity.settings.SettingsActivity;
import br.com.reign.loftylibrary.adapter.HomePostAdapter;
import br.com.reign.loftylibrary.model.Chapter;
import br.com.reign.loftylibrary.model.Manga;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.model.Post;
import br.com.reign.loftylibrary.model.User;
import br.com.reign.loftylibrary.utils.CompareChapterByDate;
import br.com.reign.loftylibrary.utils.MenuSelect;
import br.com.reign.loftylibrary.utils.RecyclerItemClickListener;

public class MangaActivity extends AppCompatActivity {

    private RecyclerView recyclerPost;
    private HomePostAdapter adapter;
    private ArrayList<Post> postItems = new ArrayList<>();
    private DatabaseReference dbProject;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listenerMangas;
    private String chapterTitle;
    private String workTitle;
    private String layout = "Card";
    MenuSelect menu = new MenuSelect();
    private TextView txtMangasIcon;
    private ImageView imgMangasIcon;
    private TextView txtNovelsIcon;
    private ImageView imgNovelsIcon;
    private TextView txtCatalogIcon;
    private ImageView imgCatalogIcon;
    private TextView txtLibraryIcon;
    private ImageView imgLibraryIcon;
    private TextView txtSettingsIcon;
    private ImageView imgSettingsIcon;
    private List<TextView> components = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        // Verificando se o usuario está online
        AppApplication application = (AppApplication) getApplication();
        getApplication().registerActivityLifecycleCallbacks(application);
        updateToken();

        initializeComponents();
        loadContent();
        openNovels();
        openCatalog();
        openLibrary();
        openSettings();
        menu.selectMenu(txtMangasIcon, components);
        readChapter();
    }
    private void updateToken() {
        User user = new User();
        user.setId(FirebaseAuth.getInstance().getUid());
        user.setToken(FirebaseInstanceId.getInstance().getToken());

        if(user.getId() != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getId())
                    .set(user);
        }
    }

    private void initializeComponents() {
        // Text View
        components.add(txtMangasIcon = findViewById(R.id.txtMangasIcon));
        components.add(txtNovelsIcon = findViewById(R.id.txtNovelsIcon));
        components.add(txtCatalogIcon = findViewById(R.id.txtCatalogIcon));
        components.add(txtLibraryIcon = findViewById(R.id.txtLibraryIcon));
        components.add(txtSettingsIcon = findViewById(R.id.txtSettingsIcon));

        // Image View
        imgMangasIcon = findViewById(R.id.imgMangasIcon);
        imgNovelsIcon = findViewById(R.id.imgNovelsIcon);
        imgCatalogIcon = findViewById(R.id.imgCatalogIcon);
        imgLibraryIcon = findViewById(R.id.imgLibraryIcon);
        imgSettingsIcon = findViewById(R.id.imgSettingsIcon);
        // Recycler Views
        recyclerPost = findViewById(R.id.rvPost);
        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // Adapters
        adapter = new HomePostAdapter(postItems, getApplicationContext(), layout);

        recyclerPost.setAdapter(adapter);
    }

    private void loadContent() {
        FirebaseDatabase.getInstance().getReference().child("recent").child("chapters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postItems.clear();
                CompareChapterByDate compare = new CompareChapterByDate();
                for(DataSnapshot mangas : dataSnapshot.getChildren()) {
                    MangaChapter manga = mangas.getValue(MangaChapter.class);
                    manga.setWorkTitle(mangas.getKey());
                    manga.setCover(String.valueOf(mangas.child("cover").getValue()));
                    for (DataSnapshot chapter : mangas.getChildren()) {
                        if(!chapter.getKey().equalsIgnoreCase("cover") && !chapter.getKey().equalsIgnoreCase("currentDate") && !chapter.getKey().equalsIgnoreCase("date")) {
                            manga.setChapterTitle(chapter.getKey());
                            if(!(chapter.child("currentDate").getValue() == null) && !(chapter.child("currentDate").getValue().equals(""))) {
                                manga.setDate(Long.parseLong(String.valueOf(chapter.child("currentDate").getValue())));
                            }
                            postItems.add(new Post(manga.getWorkTitle(), manga.getChapterTitle(), manga.getCover(), manga.getDate()));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                postItems.sort(compare);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void readChapter() {
        recyclerPost.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerPost,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        HomePostAdapter.ViewHolder holder = new HomePostAdapter.ViewHolder(view);
                        chapterTitle = String.valueOf(holder.getTxtChapterTitle().getText());
                        workTitle = String.valueOf(holder.getTxtPostTitle().getText());
                        Intent intent = new Intent(getApplicationContext(), ReadMangaActivity.class);
                        intent.putExtra("WorkTitle", workTitle);
                        intent.putExtra("ChapterTitle", chapterTitle);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        HomePostAdapter.ViewHolder holder = new HomePostAdapter.ViewHolder(view);
                        chapterTitle = String.valueOf(holder.getTxtChapterTitle().getText());
                        workTitle = String.valueOf(holder.getTxtPostTitle().getText());

                        Intent intent = new Intent(MangaActivity.this, WorkActivity.class);
                        intent.putExtra("WorkTitle", workTitle);
                        intent.putExtra("Category", "mangas");
                        startActivity(intent);
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

    private void openNovels() {
        imgNovelsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NovelActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtNovelsIcon, components);
            }
        });
    }
    private void openCatalog() {
        imgCatalogIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CatalogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtCatalogIcon, components);
            }
        });
    }
    private void openLibrary() {
        imgLibraryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtLibraryIcon, components);
            }
        });
    }
    private void openSettings(){
        imgSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtSettingsIcon, components);
            }
        });
    }
}