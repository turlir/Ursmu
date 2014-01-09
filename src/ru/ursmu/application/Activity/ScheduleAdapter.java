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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

public class ScheduleAdapter extends ArrayAdapter<EducationItem> {


    private Context mContext;
    private int mResID;
    private boolean isProfessor;
    private EducationItem[] mData;

    protected static final double[] start = new double[]{9.00, 10.50, 12.40, 14.30, 16.10, 17.40, 19.10, 20.40};
    protected static final double[] stop = new double[]{10.30, 12.20, 14.10, 16.00, 17.30, 19.00, 20.30, 22.10};

    private static int mCurrentIconPair = -1;

    private static Calendar mCalendar = Calendar.getInstance();
    private static final DecimalFormat mDecimalFormatter;

    static {
        mDecimalFormatter = new DecimalFormat("00.00");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(':');
        mDecimalFormatter.setDecimalFormatSymbols(custom);
        mDecimalFormatter.setGroupingSize(2);
    }

    public ScheduleAdapter(Context context, int layout, EducationItem[] data, boolean isProf) {
        super(context, layout, data);
        mContext = context;
        mResID = layout;
        isProfessor = isProf;
        mData = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //Log.d("URSMULOG", "ScheduleAdapter getView view == null");
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResID, parent, false);

            holder = new ViewHolder();
            holder.nametv = (TextView) convertView.findViewById(R.id.schedule_name);
            holder.teacher = (TextView) convertView.findViewById(R.id.schedule_professor);
            holder.room = (TextView) convertView.findViewById(R.id.schedule_aud);
            holder.timeStart = (TextView) convertView.findViewById(R.id.time_schedule_start);
            holder.timeStop = (TextView) convertView.findViewById(R.id.time_schedule_stop);
            holder.flag = convertView.findViewById(R.id.viewColor_sc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EducationItem item = mData[position];
        int current_pair = item.getNumberPar();

        holder.nametv.setText(item.getPredmet());
        if (!isProfessor) {
            holder.teacher.setText(item.getProfessor());
        } else {
            holder.teacher.setText(item.getGroupName());
        }
        holder.room.setText(item.getAud());
        holder.timeStart.setText(getTime(current_pair, true));
        holder.timeStop.setText(getTime(current_pair, false));

        if (mCalendar == null)
            mCalendar = Calendar.getInstance();

        if (getIcon(current_pair, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE))) {
            holder.flag.setVisibility(View.VISIBLE);
        } else {
            holder.flag.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private String getTime(int numberPair, boolean f) {
        //Log.d("URSMULOG", "getTime(" + numberPair + ")");
        int n = (numberPair == 0 ? 0 : numberPair - 1);
        String s;
        Double t;
        if (f) {
            if (n < start.length) {
                t = start[n];
                s = mDecimalFormatter.format(t);
                return s;
            }
        } else {
            if (n < stop.length) {
                t = stop[n];
                s = mDecimalFormatter.format(t);
                return s;
            }
        }
        s = "Surprise";
        return s;
    }

    protected static boolean getIcon(int numberPair, int current_hour, int current_min) {
        if (ScheduleAdapter.mCurrentIconPair != -1) {
            if (ScheduleAdapter.mCurrentIconPair == numberPair) {
                Log.d("URSMULOG", "getIcon shor " + (numberPair - 1));
                return true;
            } else {
                return false;
            }
        } else {


            int n = (numberPair == 0 ? 0 : numberPair - 1);
            if (n < start.length && n < stop.length) {
                int hour = current_hour;
                int minute = current_min;
                int i2 = hour * 60 + minute;   //current

                double begin = start[n];
                int[] x = timeArrayItemConvert(begin);
                int i1 = x[0] * 60 + x[1];      //begin

                double end = stop[n];
                x = timeArrayItemConvert(end);
                int i3 = x[0] * 60 + x[1];   //stop

                //current >= begin &&  current <= stop
                if (i2 >= i1 && i2 <= i3) {
                    Log.d("URSMULOG", "getIcon(" + numberPair + ")" + i2 + " >= " + i1 + " && " + i2 + " <= " + i3 + " n=" + n);
                    //mCurrentIconPair = new WeakReference<Integer>(numberPair);
                    ScheduleAdapter.mCurrentIconPair = numberPair;
                    return true;
                }
            }
        }

        return false;
    }

    protected static int[] timeArrayItemConvert(double item) { //item = x,y
        int x = (int) item;       //целая часть
        double z = (item - x); //дробная часть
        if (z < 10) {
            z = z * 100;
        }
        int y = (int) z;
        return new int[]{x, y};
    }

    protected static void clearIconPair() {
        ScheduleAdapter.mCurrentIconPair = -1;
        ScheduleAdapter.mCalendar = null;
    }

    public void setAlarm(int position) {
        double b = start[position];
        int hour = (int) b - 1;
        int minute = (int) ((b - (int) b) * 100); //0.4 * 100

        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Мне к " + (position + 1) + " паре");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
        mContext.startActivity(i);
    }

    public void setData(EducationItem[] newData) {
        mData = newData;
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView nametv;
        TextView teacher;
        TextView room;
        TextView timeStart;
        TextView timeStop;
        View flag;
    }
}