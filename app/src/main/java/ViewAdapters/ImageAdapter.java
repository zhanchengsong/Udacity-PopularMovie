package ViewAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.gson.JsonArray;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by zhanchengsong on 2018-03-28.
 * This part of the code reuse some of the ideas from
 * https://developer.android.com/guide/topics/ui/layout/gridview.html#java
 */

public class ImageAdapter extends BaseAdapter {
   private JsonArray movieData;
   private Context context;
   private List<Bitmap> photos;
    public ImageAdapter (JsonArray data, Context context, List<Bitmap> photos) {
        this.movieData = data;
        this.context = context;
        this.photos = photos;
    }
    @Override
    public int getCount() {
        return movieData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        }else {
            imageView = (ImageView) convertView;
        }

        //Populate the view by downloading data

        try {

            imageView.setImageBitmap(this.photos.get(position));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageView;
    }

    //Network Task to get image

}
