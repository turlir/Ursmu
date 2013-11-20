package ru.ursmu.application.Activity;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.ursmu.application.JsonObject.EducationItem;
import ru.ursmu.beta.application.R;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

public class ScheduleAdapter extends ArrayAdapter<EducationItem> {


    private Context mContext;
    private int mResID;
    private boolean isProfessor;

    private static final double[] start = new double[]{9.00,  10.50, 12.40, 14.30, 16.10, 17.40, 19.10, 20.40};
    private static final double[] end = new double[]  {10.30, 12.20, 14.10, 16.00, 17.30, 19.00, 20.30, 22.10};

    private static WeakReference<Integer> mCurrentIconPair;

    private static final Calendar mCalendar;
    private static final DecimalFormat mDecimalFormatter;

    static {
        mDecimalFormatter = new DecimalFormat("00.00");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(':');
        mDecimalFormatter.setDecimalFormatSymbols(custom);
        mDecimalFormatter.setGroupingSize(2);

        mCalendar = Calendar.getInstance();
    }

    public ScheduleAdapter(Context context, int layout, EducationItem[] data, boolean isProf) {
        super(context, layout, data);
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
            holder.timeStart = (TextView) view.findViewById(R.id.time_schedule_start);
            holder.timeStop = (TextView) view.findViewById(R.id.time_schedule_stop);
            holder.flag = view.findViewById(R.id.viewColor_sc);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        EducationItem item = getItem(position);
        Log.d("URSMULOG", "item.getNumberPar() " + item.getNumberPar());
        holder.nametv.setText(item.getPredmet());
        if (!isProfessor) {
            holder.teacher.setText(item.getProfessor());
        } else {
            holder.teacher.setText(item.getGroupName());
        }
        holder.room.setText(item.getAud());
        holder.timeStart.setText(getTime(item.getNumberPar(), true));
        holder.timeStop.setText(getTime(item.getNumberPar(), false));
        if (getIcon(item.getNumberPar())) {
            holder.flag.setVisibility(View.VISIBLE);
        } else {
            holder.flag.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private String getTime(int numberPair, boolean f) {
        Log.d("URSMULOG", "getTime(" + numberPair + ")");
        int n = numberPair - 1;
        String s;
        Double t;
        if (f) {
            if (n < start.length) {
                t = start[n];
                s = mDecimalFormatter.format(t);
                return s;
            }
        } else {
            if (n < end.length) {
                t = end[n];
                s = mDecimalFormatter.format(t);
                return s;
            }
        }
        s = "Surprise";
        return s;
    }

    private boolean getIcon(int numberPair) {
        if (mCurrentIconPair != null)
            if (mCurrentIconPair.get() != 0) {
                if (mCurrentIconPair.get() == numberPair) {
                    return true;
                } else {
                    return false;
                }
            }

        int n = numberPair - 1;
        if (n < start.length && n < end.length) {
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);
            int i2 = hour * 60 + minute;
            double b = start[n];
            double e = end[n];
            int i1 = (int) b * 60 + (int) (b - (int) b);
            int i3 = (int) e * 60 + (int) (e - (int) e);

            if (i2 >= i1 && i2 <= i3) {
                Log.d("URSMULOG", i2 + " >= " + i1 + " && " + i2 + " <= " + i3 + " n=" + n);
                mCurrentIconPair = new WeakReference<Integer>(numberPair);
                return true;
            }
        }

        return false;
    }

    public void setAlarm(int position) {
        double b = start[position];
        int hour = (int) b - 1;
        int minute = (int) ((b - (int)b) * 100); //0.4 * 100

        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Мне к " + (position + 1) + " паре");
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
        public TextView timeStart;
        public TextView timeStop;
        public View flag;
    }
}