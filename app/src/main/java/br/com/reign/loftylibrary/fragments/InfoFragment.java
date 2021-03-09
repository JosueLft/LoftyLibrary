package br.com.reign.loftylibrary.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import br.com.reign.loftylibrary.R;

public class InfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TextView txtApplyZoom;
    private TextView txtApply;
    private TextView txtApplyDescription;
    private TextView txtRemove;
    private TextView txtRemoveDescription;

    public InfoFragment() {}

    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        initializeComponents(view);
        openInfo();
        return view;
    }

    private void openInfo() {
        txtApplyZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtApply.setVisibility(View.VISIBLE);
                txtApplyDescription.setVisibility(View.VISIBLE);
                txtRemove.setVisibility(View.VISIBLE);
                txtRemoveDescription.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeComponents(View view) {
        txtApplyZoom = view.findViewById(R.id.txtApplyZoom);
        txtApply = view.findViewById(R.id.txtApply);
        txtApplyDescription = view.findViewById(R.id.txtApplyDescription);
        txtRemove = view.findViewById(R.id.txtRemove);
        txtRemoveDescription = view.findViewById(R.id.txtRemoveDescription);
    }
}