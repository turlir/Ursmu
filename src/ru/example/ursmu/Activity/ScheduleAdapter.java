package ru.example.ursmu.Activity;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.example.ursmu.R;
import ru.example.ursmu.JsonObject.EducationItem;

import java.util.Calendar;

public class ScheduleAdapter extends ArrayAdapter<EducationItem> {


    private Context mContext;
    private int mResID;
    //private EducationItem[] mWeek;
    private boolean isProfessor;

    private static String[] mAlarm = new String[]
            {"9:00-10:30",
                    "10:50-12:20",
                    "12:40-14:10",
                    "14:30-16:00",
                    "16:10-17:30",
                    "17:40-19:00",
                    "19:10-20:30"};

    private static int[] mAlarmStartHour = new int[]{9, 10, 12, 14, 16, 17, 19};
    private static int[] mAlarmStartMin = new int[]{0, 50, 40, 30, 10, 40, 10};

    private static int[] mAlarmEndHour = new int[]{10, 12, 14, 16, 17, 19, 20};

    private static int[] mAlarmEndMin = new int[]{30, 20, 10, 0, 30, 0, 30};
    private static int mCurrentIconPair;

    private static final Calendar mCalendar = Calendar.getInstance();

    public ScheduleAdapter(Context context, int layout, EducationItem[] data, boolean isProf) {
        super(context, layout, data);
        //mWeek = data;
        mContext = context;
        mResID = layout;
        isProfessor = isProf;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(mResID, null);

            holder = new ViewHolder();
            holder.nametv = (TextView) view.findViewById(R.id.schedule_name);
            holder.teacher = (TextView) view.findViewById(R.id.schedule_professor);
            holder.room = (TextView) view.findViewById(R.id.schedule_aud);
            holder.time = (TextView) view.findViewById(R.id.time_schedule);
            //holder.flag = (ImageView) view.findViewById(R.id.icon_schedule);
            holder.flag = view.findViewById(R.id.viewColor_sc);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        EducationItem item = getItem(position);
        holder.nametv.setText(item.getmPredmet());
        if (!isProfessor) {
            holder.teacher.setText(item.getProfessor());
        } else {
            holder.teacher.setText(item.getGroupName());
        }
        holder.room.setText(item.getAud());
        holder.time.setText(getTime(item.getNumberPar()));
        if (getIcon(item.getNumberPar()) != 0) {
            holder.flag.setVisibility(View.VISIBLE);
            //holder.flag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.time));
        }

        return view;
    }

    private String getTime(int numberPair) {
        if (numberPair <= mAlarm.length) {
            return mAlarm[numberPair - 1];
        } else {
            return "Surprise!";
        }
    }

    private int getIcon(int numberPair) {
        //Log.d("URSMULOG", "getIcon()");
        if (mCurrentIconPair != 0) {
            if (mCurrentIconPair == numberPair) {
                return 1;
            } else {
                return 0;
            }
        }


        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        int i2 = hour * 60 + minute;
        int n = numberPair - 1;

        //crazy!

/*        if (hour == mAlarmStartHour[n]) {
            if (minute >= mAlarmStartMin[n]) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (hour > mAlarmStartHour[n]) {
                if (hour == mAlarmEndHour[n]) {
                    if (minute <= mAlarmEndMin[n]) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    if (hour < mAlarmEndHour[n]) {
                        return 1;
                    }
                }
            }
        }*/

        int i1 = mAlarmStartHour[n] * 60 + mAlarmStartMin[n];
        int i3 = mAlarmEndHour[n] * 60 + mAlarmEndMin[n];

        if (i2 >= i1 && i2 <= i3) {
            Log.d("URSMULOG", i2 + " >= " + i1 + " && " + i2 + " <= " + i3 + " n=" + n);
            mCurrentIconPair = numberPair;
            return 1;
        }

        return 0;
    }

    public void setAlarm(int position) {
        int hour = mAlarmStartHour[position] - 1;
        int minute = mAlarmStartMin[position];

        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Мне к " + position + " паре");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        mContext.startActivity(i);
    }

    private static class ViewHolder {
        public TextView nametv;
        public TextView teacher;
        public TextView room;
        public TextView time;
        //public ImageView flag;
        public View flag;
    }
}