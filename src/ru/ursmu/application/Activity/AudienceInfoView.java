package ru.ursmu.application.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ru.ursmu.beta.application.R;

import java.util.Locale;

public class AudienceInfoView extends RelativeLayout {

    private static UrsmuBuilding z;
    private final Typeface mRobotoRegular;

    public AudienceInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.audience_info_adapter, this, true);
        mRobotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
    }


    public void setAudienceText(UrsmuBuilding s) {
        z = s;
        view();
    }

    private void view() {

        TextView building_number = (TextView) findViewById(R.id.building_number);
        building_number.setTypeface(mRobotoRegular);
        building_number.setText(z.getBuild());

        ((TextView) findViewById(R.id.building_address)).setText(z.getAddress());

        TextView floor_number = (TextView) findViewById(R.id.floor_number);
        floor_number.setTypeface(mRobotoRegular);
        floor_number.setText(z.getFloor());

        TextView audience_number = (TextView) findViewById(R.id.audience_number);
        floor_number.setTypeface(mRobotoRegular);
        audience_number.setText(z.getAudience());

        View mapIcon = findViewById(R.id.building_go_to_map_icon);
        mapIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("URSMULOG", "onClick");
                goToMap();
            }
        });
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.red_map_icon);
        mapIcon.startAnimation(animation);
    }

    public void goToMap() {
        String f = "geo:0,0?q=Екатеринбург, %s";
        String uri = String.format(new Locale("ru", "RU"), f, z.getMapFlagName());

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getContext().startActivity(i);
    }


}
