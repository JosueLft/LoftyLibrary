package br.com.reign.loftylibrary.fragments.index;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.fragments.index.internal.AboutFragment;
import br.com.reign.loftylibrary.fragments.index.internal.ChaptersFragment;
import br.com.reign.loftylibrary.model.Work;

public class IndexFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView txtIndexWorkTitle;
    private TextView txtWorkSynopsis;
    private ImageView imgIndexWorkCover;

    private Button btnIndexSynopsis;
    private Button btnIndexChaptersList;
    private Button btnIndexAbout;

    private FrameLayout frameIndexContent;

    private ChaptersFragment chaptersFragment;
    private AboutFragment aboutFragment;

    private Comunication comunication;

    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

    private String workTitle = null;
    private String workType = null;
    private String workSynopsis = null;
    private String workdistributedBy = null;
    private String workGenre = null;


    public IndexFragment() {}

    public static IndexFragment newInstance(String param1, String param2) {
        IndexFragment fragment = new IndexFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        if(!(activity instanceof Comunication)) {
            throw new RuntimeException("A activity deve implementar a interface Comunication!");
        }
        comunication = (Comunication) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);

        initializeComponents(view);
        recoverWork();
        openTab(view);
        txtWorkSynopsis.setVisibility(View.VISIBLE);
        txtWorkSynopsis.setText(workSynopsis);

        return view;
    }

    public void initializeComponents(View view) {
        // TextView
        txtIndexWorkTitle = view.findViewById(R.id.txtPostTitle);
        txtWorkSynopsis = view.findViewById(R.id.txtWorkSynopsis);

        // ImageView
        imgIndexWorkCover = view.findViewById(R.id.imgIndexWorkCover);

        // Button
        btnIndexSynopsis = view.findViewById(R.id.btnIndexSynopsis);
        btnIndexChaptersList = view.findViewById(R.id.btnIndexChaptersList);
        btnIndexAbout = view.findViewById(R.id.btnIndexAbout);

        // frame
        frameIndexContent = view.findViewById(R.id.frameIndexContent);

        // Fragments
        chaptersFragment = new ChaptersFragment();
        aboutFragment = new AboutFragment();
    }

    public void recoverWork() {
        Query query;
        query = dbReference.child("works")
                .child(workType)
                .child(workTitle);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtIndexWorkTitle.setText(dataSnapshot.getKey());
                Picasso.get()
                        .load(String.valueOf(dataSnapshot.child("cover").getValue()))
                        .into(imgIndexWorkCover);
                workSynopsis = String.valueOf(dataSnapshot.child("description").getValue());
                workdistributedBy = String.valueOf(dataSnapshot.child("distributedBy").getValue());
                workGenre = String.valueOf(dataSnapshot.child("genre").getValue());
                workType = String.valueOf(dataSnapshot.child("type").getValue());
                txtWorkSynopsis.setText(workSynopsis);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void receiveTitle(String title, String type) {
        workTitle = title;
        workType = type;
    }

    public void openTab(View view) {
        txtWorkSynopsis.setVisibility(View.VISIBLE);
        txtWorkSynopsis.setText(workSynopsis);
        frameIndexContent.setVisibility(View.GONE);
        btnIndexSynopsis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameIndexContent.setVisibility(View.GONE);
                txtWorkSynopsis.setVisibility(View.VISIBLE);
                txtWorkSynopsis.setText(workSynopsis);
            }
        });

        btnIndexChaptersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comunication != null) {
                    comunication.captureChapters(workTitle, workType);
                    txtWorkSynopsis.setVisibility(View.GONE);
                    frameIndexContent.setVisibility(View.VISIBLE);
                }
            }
        });

        btnIndexAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comunication != null) {
                    comunication.captureInformations(workType, workGenre, workdistributedBy);
                    txtWorkSynopsis.setVisibility(View.GONE);
                    frameIndexContent.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}