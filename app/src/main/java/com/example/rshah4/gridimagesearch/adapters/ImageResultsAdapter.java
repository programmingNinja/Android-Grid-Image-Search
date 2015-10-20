package com.example.rshah4.gridimagesearch.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.rshah4.gridimagesearch.R;
import com.example.rshah4.gridimagesearch.helpers.CropSquareTransformation;
import com.example.rshah4.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {
	private static class ViewHolder {
		ImageView ivImage;
	}

	public ImageResultsAdapter(Context context, List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		ImageResult imageResult = getItem(position);
		
		ViewHolder viewHolder; // View lookup cache
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_image_result, parent, false);
			viewHolder = new ViewHolder();
			// Lookup view for data population
			viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Clear out image from last time as this could be a recycled view
		viewHolder.ivImage.setImageResource(0);
		Picasso.with(getContext()).load(imageResult.thumbUrl)
			.transform(new CropSquareTransformation(parent.getWidth(), parent.getWidth()))
			.into(viewHolder.ivImage);

		// Return the completed view to render on screen
		return convertView;
	}
}
