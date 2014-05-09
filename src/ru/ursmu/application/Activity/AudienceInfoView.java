package ru.ursmu.application.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ru.ursmu.beta.application.R;

import java.util.Locale;

public class AudienceInfoView extends RelativeLayout {

    private static UrsmuBuilding z;

    public AudienceInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.audience_info_adapter, this, true);
    }


    public void setAudienceText(UrsmuBuilding s) {
        z = s;
        view();
    }

    private void view() {

        ((TextView) findViewById(R.id.building_number)).setText(z.getBuild());
        ((TextView) findViewById(R.id.building_address)).setText(z.getAddress());
        //findViewById(R.id.building_face_image).setBackgroundDrawable(z.getFaceImage());

        ((TextView) findViewById(R.id.floor_number)).setText(z.getFloor());
        ((TextView) findViewById(R.id.audience_number)).setText(z.getAudience());
        findViewById(R.id.building_go_to_map_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("URSMULOG", "onClick");
                goToMap();
            }
        });
    }

    public void goToMap() {
        String f = "geo:0,0?q=Екатеринбург, %s";
        String uri = String.format(new Locale("ru", "RU"), f, z.getMapFlagName());

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getContext().startActivity(i);
    }


}
