package com.example.connexus2;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class StreamAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Stream>  streamArray;
    private Bitmap[] myBitmap;
    private int indexList[];

    public StreamAdapter(Context c, ArrayList<Stream> s, Bitmap[] b, int[] a) {
        mContext = c;
        streamArray = s;
        // coverImage table
        myBitmap = b;
        indexList = a;
    }

    public int getCount() {
        return indexList.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        Bitmap bm = null;
        //set format
        try{
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }
        //set view
		if (position < indexList.length){
        bm = myBitmap[indexList[position]];
		}
        } catch (Exception e){
        	e.printStackTrace();
        }
		 if (bm == null) {  
	            bm=BitmapFactory.decodeResource(mContext.getResources(),  
	                    R.drawable.defaultimage);  
	            Log.i("BitmapPicture", "picture is null!!");  
	        }  

        imageView.setImageBitmap(bm);
        bm = null;
        return imageView;
    }
}
