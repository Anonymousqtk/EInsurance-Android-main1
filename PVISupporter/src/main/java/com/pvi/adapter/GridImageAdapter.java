package com.pvi.adapter;

import java.util.ArrayList;

import com.pvi.activities.R;
import com.pvi.helpers.GlobalData;
import com.pvi.objects.BeforeSurveyImage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class GridImageAdapter extends ArrayAdapter<BeforeSurveyImage> {

	private static final String IMAGE_UPLOADED 		= "1";
	private static final String IMAGE_NOT_UPLOADED 	= "0";
	private static final int IMAGE_THUMB = 100;
	
	private Context context;
	private int layoutResourceId;
	private ArrayList<BeforeSurveyImage> objects = new ArrayList<BeforeSurveyImage>();

	public GridImageAdapter(Context context, int layoutResourceId,
			ArrayList<BeforeSurveyImage> objects) {
		super(context, layoutResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();

			holder.imageView = (ImageView) row.findViewById(R.id.imageView);
			holder.stateImage = (ImageView) row.findViewById(R.id.stateImage);
			row.setTag(holder);

		} else {
			holder = (RecordHolder) row.getTag();
		}

		BeforeSurveyImage image = objects.get(position);
		new LoadImage(holder.imageView, image.getFileName()).execute();

		if (image.getUploadState().equals(IMAGE_UPLOADED)) {
			holder.stateImage.setVisibility(ImageView.VISIBLE);
			row.setAlpha(0.6f);
			row.setClickable(false);

		} else if (image.getUploadState().equals(IMAGE_NOT_UPLOADED)) {
			holder.stateImage.setVisibility(ImageView.GONE);
		}
		
		// check if clicked before, VISIBLE checked image
		for (Integer index : GlobalData.getInstance().uploadIndexes) {
			if (index == position) {
				holder.stateImage.setVisibility(ImageView.VISIBLE);
				break;
			}
		}

		return row;
	}

	static class RecordHolder {
		ImageView imageView;
		ImageView stateImage;
	}

	class LoadImage extends AsyncTask<Object, Void, Bitmap> {

		private ImageView imageView;
		private String path;
		private String mark;

		public LoadImage(ImageView imv, String path) {
			this.imageView = imv;
			this.path = path;
			this.mark = path;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			Bitmap bitmap = null;
			bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.path), IMAGE_THUMB, IMAGE_THUMB);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (!this.mark.equals(this.path)) {
				/*
				 * The path is not same. This means that this image view is
				 * handled by some other async task. We don't do anything and
				 * return.
				 */
				return;
			}

			if (result != null && this.imageView != null) {
				this.imageView.setVisibility(View.VISIBLE);
				this.imageView.setImageBitmap(result);
			} else {
				this.imageView.setVisibility(View.GONE);
			}
		}
	}
}


