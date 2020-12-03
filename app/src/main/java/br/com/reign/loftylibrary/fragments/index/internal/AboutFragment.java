package br.com.reign.loftylibrary.fragments.index.internal;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.controller.Comunication;
import br.com.reign.loftylibrary.model.Work;

public class AboutFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Comunication comunication;

    private TextView txtWorkAboutType;
    private TextView txtWorkAboutGenre;
    private TextView txtWorkAboutDistributedBy;

    private String type;
    private String genre;
    private String distributedBy;

    public AboutFragment() {}

    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
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
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        initializeComponents(view);
        txtWorkAboutType.setText(type);
        txtWorkAboutGenre.setText(genre);
        txtWorkAboutDistributedBy.setText(distributedBy);
        return view;
    }

    public void initializeComponents(View view) {
        txtWorkAboutType = view.findViewById(R.id.txtWorkAboutType);
        txtWorkAboutGenre = view.findViewById(R.id.txtWorkAboutGenre);
        txtWorkAboutDistributedBy = view.findViewById(R.id.txtWorkAboutDistributedBy);
    }

    public void receiveInformations(String workType, String workGenre, String workDistributedBy) {
        type = workType;
        genre = workGenre;
        distributedBy = workDistributedBy;
    }
}