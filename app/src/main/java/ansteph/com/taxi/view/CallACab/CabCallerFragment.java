package ansteph.com.taxi.view.CallACab;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import ansteph.com.taxi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CabCallerFragment extends Fragment {

 static String TAG = CabCallerFragment.class.getSimpleName();

    public CabCallerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_cab_caller, container, false);



        //set the time of the pisker to now
        Calendar c = Calendar.getInstance();
        c.setTime(c.getTime());

        TimePicker picker = (TimePicker) rootView.findViewById(R.id.timePicker);
        picker.setIs24HourView(Boolean.TRUE);
        picker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        picker.setCurrentMinute(c.get(Calendar.MINUTE));




        ImageButton imgbtnPick = (ImageButton) rootView.findViewById(R.id.imgbtnPick);

        imgbtnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Pick_DesMapActivity.class);
                startActivity(i);
            }
        });




        return rootView;



    }

}
