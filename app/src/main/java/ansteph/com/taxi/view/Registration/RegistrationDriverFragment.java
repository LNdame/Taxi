package ansteph.com.taxi.view.Registration;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ansteph.com.taxi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationDriverFragment extends Fragment {


    public RegistrationDriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_driver, container, false);
    }


}
