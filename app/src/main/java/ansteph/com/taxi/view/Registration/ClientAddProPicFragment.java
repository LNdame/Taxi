package ansteph.com.taxi.view.Registration;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ansteph.com.taxi.R;
import ansteph.com.taxi.view.CallACab.Requester;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientAddProPicFragment extends Fragment {


    public ClientAddProPicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View rootView =inflater.inflate(R.layout.fragment_client_add_pro_pic, container, false);

        TextView txtskip = (TextView)rootView.findViewById(R.id.txtSkip);
        txtskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Requester.class);
                startActivity(i);
            }
        });

        return rootView;
    }


}
