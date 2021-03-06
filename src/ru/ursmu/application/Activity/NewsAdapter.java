package ru.ursmu.application.Activity;

import android.content.Context;
import android.graphics.Bitmap;
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
import ru.ursmu.application.JsonObject.ListItem;
import ru.ursmu.application.R;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<ListItem> {

    private final ImageLoader mImageLoader;
    private final Typeface mTypefaceDesc;
    private final Typeface mTypefaceTitle;
    private int mLayout;

    public NewsAdapter(Context context, int textViewResourceId, ArrayList<ListItem> objects) {
        super(context, textViewResourceId, objects);

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
                .discCache(new TotalSizeLimitedDiscCache(getContext().getCacheDir(), memory_cache_size * 5))
                .discCacheSize(memory_cache_size * 5)
                .discCacheFileCount(10)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .defaultDisplayImageOptions(display_options)
                .build();

        mImageLoader.init(config);

        mTypefaceDesc = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        mTypefaceTitle = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");

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
        holder.mTitle.setTypeface(mTypefaceTitle);

        holder.mDesc.setText(item.getDesc());
        holder.mDesc.setTypeface(mTypefaceDesc);

        mImageLoader.displayImage(item.getImage(), holder.mImage);

        return view;
    }

    private static class ViewHolder {
        public TextView mTitle;
        public TextView mDesc;
        public ImageView mImage;
    }
}