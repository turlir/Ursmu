package ru.example.ursmu.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import ru.example.ursmu.JsonObject.ListItem;
import ru.example.ursmu.R;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<ListItem> {

    private final ImageLoader mImageLoader;
    //private GroupScheduleActivity mContext;
    //private ArrayList<ListItem> mData;
    private int mLayout;

    public NewsAdapter(Context context, int textViewResourceId, ArrayList<ListItem> objects) {
        super(context, textViewResourceId, objects);
        //mContext = context;
        mLayout = textViewResourceId;

        mImageLoader = ImageLoader.getInstance();

        int memory_cache_size = 10 * 1024 * 1024;    //10 mB

        DisplayImageOptions display_options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .showStubImage(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(400, 261) // default = device screen dimensions
                .discCacheExtraOptions(400, 261, Bitmap.CompressFormat.JPEG, 100, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .memoryCache(new UsingFreqLimitedMemoryCache(memory_cache_size)) // default
                .memoryCacheSize(memory_cache_size)
                .discCache(new UnlimitedDiscCache(context.getCacheDir()))
                .discCacheSize(memory_cache_size * 5)
                .discCacheFileCount(10)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(display_options)
                .build();

        mImageLoader.init(config);


    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(mLayout, null);

            holder = new ViewHolder();
            holder.mTitle = (TextView) view.findViewById(R.id.news_item_title);
            holder.mDesc = (TextView) view.findViewById(R.id.news_item_desc);
            holder.mImage = (ImageView) view.findViewById(R.id.news_item_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ListItem item = getItem(position);

        holder.mTitle.setText(item.getTitle());
        holder.mDesc.setText(item.getDesc());

        mImageLoader.displayImage(item.getImage(), holder.mImage);

        return view;
    }

    private static class ViewHolder {
        public TextView mTitle;
        public TextView mDesc;
        public ImageView mImage;
    }
}