package br.com.reign.loftylibrary.activity.library;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupieAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.OnItemLongClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.account.LoginActivity;
import br.com.reign.loftylibrary.activity.catalog.CatalogActivity;
import br.com.reign.loftylibrary.activity.catalog.WorkActivity;
import br.com.reign.loftylibrary.activity.manga.MangaActivity;
import br.com.reign.loftylibrary.activity.manga.ReadMangaActivity;
import br.com.reign.loftylibrary.activity.novel.NovelActivity;
import br.com.reign.loftylibrary.activity.settings.SettingsActivity;
import br.com.reign.loftylibrary.model.Chapter;
import br.com.reign.loftylibrary.utils.MenuSelect;

public class LibraryActivity extends AppCompatActivity {
    // Library
    private List<Chapter> listChapters = new ArrayList<>();
    private RecyclerView rvLastChapters;
    private TextView txtLibraryEmpty;
    private TextView txtFollowing;
    GroupieAdapter adapter = new GroupieAdapter();
    // Menu
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
    MenuSelect menu = new MenuSelect();
    private List<TextView> components = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        verifyAuthentication();
        initializeComponents();
        fetchLibrary();
        openMangas();
        openNovels();
        openCatalog();
        openSettings();
        menu.selectMenu(txtLibraryIcon, components);
        readChapter();
    }

    private void readChapter() {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(LibraryActivity.this, ReadMangaActivity.class);
                LibraryItem libraryItem = (LibraryItem) item;
                intent.putExtra("WorkTitle", libraryItem.chapter.getWorkTitle());
                intent.putExtra("ChapterTitle", libraryItem.chapter.getChapterTitle());
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(LibraryActivity.this, WorkActivity.class);
                LibraryItem libraryItem = (LibraryItem) item;
                intent.putExtra("WorkTitle", libraryItem.chapter.getWorkTitle());
                intent.putExtra("Category", "mangas");
                startActivity(intent);
                return false;
            }
        });
    }

    private void fetchLibrary() {
        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("library")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> chapters = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot chapter : chapters) {
                            Chapter c = chapter.toObject(Chapter.class);
                            if(!c.equals(null)) {
                                txtLibraryEmpty.setVisibility(View.GONE);
                                txtFollowing.setVisibility(View.VISIBLE);
                                rvLastChapters.setVisibility(View.VISIBLE);
                                adapter.add(new LibraryItem(c));
                            } else {
                                txtLibraryEmpty.setVisibility(View.VISIBLE);
                                txtFollowing.setVisibility(View.GONE);
                                rvLastChapters.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void verifyAuthentication() { // metodo utilizado para verificar se esta logado e redirecionar pra determinada activity
        if(FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void initializeComponents() {
        // Text View
        components.add(txtMangasIcon = findViewById(R.id.txtMangasIcon));
        components.add(txtNovelsIcon = findViewById(R.id.txtNovelsIcon));
        components.add(txtCatalogIcon = findViewById(R.id.txtCatalogIcon));
        components.add(txtLibraryIcon = findViewById(R.id.txtLibraryIcon));
        components.add(txtSettingsIcon = findViewById(R.id.txtSettingsIcon));
        txtLibraryEmpty = findViewById(R.id.txtLibraryEmpty);
        txtFollowing = findViewById(R.id.txtFollowing);

        // Image View
        imgMangasIcon = findViewById(R.id.imgMangasIcon);
        imgNovelsIcon = findViewById(R.id.imgNovelsIcon);
        imgCatalogIcon = findViewById(R.id.imgCatalogIcon);
        imgLibraryIcon = findViewById(R.id.imgLibraryIcon);
        imgSettingsIcon = findViewById(R.id.imgSettingsIcon);

        // RecyclerView
        rvLastChapters = findViewById(R.id.rvLastChapters);
        rvLastChapters.setAdapter(adapter);
        rvLastChapters.setLayoutManager(new LinearLayoutManager(this));
    }

    private class LibraryItem extends Item<GroupieViewHolder> {

        private final Chapter chapter;

        private LibraryItem(Chapter chapter) {
            this.chapter = chapter;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            ImageView cover = viewHolder.itemView.findViewById(R.id.workCoverLibrary);
            TextView txtDate = viewHolder.itemView.findViewById(R.id.txtDateChapterLibrary);
            TextView txtWorkTitle = viewHolder.itemView.findViewById(R.id.txtPostTitleLibrary);
            TextView txtChapterTitle = viewHolder.itemView.findViewById(R.id.txtChapterTitleLibrary);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String d = sdf.format(chapter.getDate());
            txtWorkTitle.setText(chapter.getWorkTitle());
            txtChapterTitle.setText(chapter.getChapterTitle());
            txtDate.setText(d);

            Picasso.get()
                    .load(chapter.getCover())
                    .into(cover);
        }

        @Override
        public int getLayout() {
            return R.layout.library_work;
        }
    }

    private void openMangas() {
        imgMangasIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MangaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                menu.selectMenu(txtMangasIcon, components);
            }
        });
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