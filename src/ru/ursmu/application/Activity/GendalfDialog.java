package ru.ursmu.application.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import ru.ursmu.beta.application.R;

import java.util.Locale;

public class GendalfDialog extends DialogFragment {
    private static final String INFO = "info";
    private static Typeface mRobotoRegular;
    private static UrsmuBuilding mInfo;

    public static GendalfDialog newInstance(UrsmuBuilding i) {
        GendalfDialog d = new GendalfDialog();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(INFO, i);
        d.setArguments(bundle);
        return d;
    }

    private GendalfDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mInfo = (UrsmuBuilding) getArguments().getSerializable(INFO);
        mRobotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        getDialog().setTitle("Аудитория " + mInfo.getOriginal());
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // street view fragment del background dialog shadow
        return inflater.inflate(R.layout.gendalf_dialog, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mInfo != null) {
            initLocal();

            if (isOnline())
                initStreetView();
        }
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean result = networkInfo != null && networkInfo.isConnected();
        return result;
    }

    private void initLocal() {
        //getDialog().setTitle("Аудитория " + mInfo.getOriginal());

        TextView building_number = (TextView) getDialog().findViewById(R.id.building_number);
        building_number.setTypeface(mRobotoRegular);
        building_number.setText(mInfo.getBuild());

        ((TextView) getDialog().findViewById(R.id.building_address)).setText(mInfo.getAddress());

        TextView floor_number = (TextView) getDialog().findViewById(R.id.floor_number);
        floor_number.setTypeface(mRobotoRegular);
        floor_number.setText(mInfo.getFloor());

        TextView audience_number = (TextView) getDialog().findViewById(R.id.audience_number);
        floor_number.setTypeface(mRobotoRegular);
        audience_number.setText(mInfo.getAudience());

        getDialog().findViewById(R.id.building_go_to_map_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("URSMULOG", "onClick");
                goToMap();
            }
        });
    }

    private void goToMap() {
        String f = "geo:0,0?q=Екатеринбург, %s";
        String uri = String.format(new Locale("ru", "RU"), f, mInfo.getMapFlagName());

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getActivity().getBaseContext().startActivity(i);
    }

    private void initStreetView() {
        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions()
                .position(new LatLng(mInfo.getLatitude(), mInfo.getLongitude()))
                .userNavigationEnabled(false);

        Fragment instance_street_view = SupportStreetViewPanoramaFragment.newInstance(options);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.parent_empty_street_view, instance_street_view)
                .commit();
        getChildFragmentManager().executePendingTransactions();
    }
}
