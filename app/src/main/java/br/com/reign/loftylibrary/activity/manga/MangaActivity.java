package br.com.reign.loftylibrary.activity.manga;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.adapter.HomePostAdapter;
import br.com.reign.loftylibrary.fragments.catalog.CatalogFragment;
import br.com.reign.loftylibrary.fragments.config.SettingsActivity;
import br.com.reign.loftylibrary.fragments.index.IndexFragment;
import br.com.reign.loftylibrary.fragments.index.internal.AboutFragment;
import br.com.reign.loftylibrary.fragments.index.internal.ChaptersFragment;
import br.com.reign.loftylibrary.fragments.library.LibraryFragment;
import br.com.reign.loftylibrary.fragments.mangas.MangasFragment;
import br.com.reign.loftylibrary.fragments.mangas.ReadMangaChapterFragment;
import br.com.reign.loftylibrary.fragments.novels.NovelsFragment;
import br.com.reign.loftylibrary.fragments.novels.ReadNovelFragment;
import br.com.reign.loftylibrary.model.MangaChapter;
import br.com.reign.loftylibrary.model.Post;
import br.com.reign.loftylibrary.utils.CompareChapterByDate;
import br.com.reign.loftylibrary.utils.MenuSelect;

public class MangaActivity<CatalogFragment> extends AppCompatActivity {

    private RecyclerView recyclerPost;
    private HomePostAdapter adapter;
    private ArrayList<Post> postItems = new ArrayList<>();
    private DatabaseReference dbProject;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listenerMangas;
    MenuSelect menu = new MenuSelect();

    private String cover;
    private String chapterTitle;
    private String workTitle;
    private String dateChapter;
    private String layout = "Card";
    private Switch switchLayout;

    // mangas section
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

        initializeComponents();
        loadContent();
        openMangas();
        openNovels();
        openCatalog();
        openLibrary();
        openSettings();
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
        // Switch
        switchLayout = findViewById(R.id.switchLayout);

        // Adapters
        adapter = new HomePostAdapter(postItems, getApplicationContext(), layout);

        recyclerPost.setAdapter(adapter);
    }

    public void loadContent() {
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

    public void openMangas() {
        imgMangasIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.selectMenu(txtMangasIcon, components);
            }
        });
    }

    public void openNovels() {
        imgNovelsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.selectMenu(txtNovelsIcon, components);
            }
        });
    }

    public void openCatalog() {
        imgCatalogIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.selectMenu(txtCatalogIcon, components);
            }
        });
    }

    public void openLibrary() {
        imgLibraryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.selectMenu(txtLibraryIcon, components);
            }
        });
    }

    public void openSettings(){
        imgSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
//                startActivity(intent);
                menu.selectMenu(txtSettingsIcon, components);
            }
        });
    }
}