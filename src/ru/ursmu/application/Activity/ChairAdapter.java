package ru.ursmu.application.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import ru.ursmu.application.JsonObject.ChairItem;
import ru.ursmu.application.JsonObject.Faculty;
import ru.ursmu.application.Realization.FacultyFactory;
import ru.ursmu.beta.application.R;


public class ChairAdapter extends ArrayAdapter<ChairItem> {

    Context mContext;
    int mResId;
    private final ImageLoader mImageLoader;

    private final Typeface mTypefaceDesc, mTypefaceTitle;


    public ChairAdapter(Context context, int textViewResourceId, ChairItem[] objects) {
        super(context, textViewResourceId, objects);

        mContext = context;
        mResId = textViewResourceId;

        mImageLoader = ImageLoader.getInstance();

        int memory_cache_size = 10 * 1024 * 1024;    //10 mB

        DisplayImageOptions display_options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .showStubImage(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(100, 140) // default = device screen dimensions
                .discCacheExtraOptions(140, 187, Bitmap.CompressFormat.JPEG, 100, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .memoryCache(new UsingFreqLimitedMemoryCache(memory_cache_size)) // default
                .memoryCacheSize(memory_cache_size)
                .discCache(new TotalSizeLimitedDiscCache(getContext().getCacheDir(), memory_cache_size * 5))
                .discCacheSize(memory_cache_size * 5)
                .discCacheFileCount(10)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(display_options)
                .build();

        mImageLoader.init(config);

        mTypefaceDesc = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Light.ttf");
        mTypefaceTitle = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(mResId, null);
        }

        ChairItem item = getItem(position);

        if (item != null) {
            TextView name = (TextView) v.findViewById(R.id.name_chair);
            name.setTypeface(mTypefaceTitle);
            name.setText(item.getName());

            TextView cover_title = (TextView) v.findViewById(R.id.cover_title_chair);
            cover_title.setTypeface(mTypefaceDesc);
            cover_title.setText(item.getCoverTitle());

            TextView phone = (TextView) v.findViewById(R.id.phone_chair);
            phone.setTypeface(mTypefaceDesc);
            phone.setText(item.getPhone());

            TextView mail = (TextView) v.findViewById(R.id.e_mail_chair);
            mail.setTypeface(mTypefaceDesc);
            mail.setText(item.getMail());

            TextView address = (TextView) v.findViewById(R.id.audition_chair);
            address.setTypeface(mTypefaceDesc);
            address.setText(item.getAddress());


          /*  TextView faculty = (TextView) v.findViewById(R.id.faculty_chair);
            Faculty fac_item = FacultyFactory.create(item.getFaculty());
            faculty.setText(fac_item.getOriginalName());
            faculty.setTypeface(mTypefaceDesc);
            faculty.setBackgroundColor(Color.parseColor(fac_item.getColor()));*/

            mImageLoader.displayImage(item.getCoverSrc(), (ImageView) v.findViewById(R.id.cover_src_chair));
        }

        return v;
    }


}
