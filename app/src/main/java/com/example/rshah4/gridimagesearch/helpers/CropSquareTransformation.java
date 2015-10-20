package com.example.rshah4.gridimagesearch.helpers;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class CropSquareTransformation implements Transformation {
	private final int width;
	private final int height; // dp

	// width is view width in dp
	// height is view height in dp
	public CropSquareTransformation(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Bitmap transform(Bitmap source) {
		Bitmap result = scaleToFill(source, width, height);
		if (result != source) {
			source.recycle();
		}
		return result;
	}

	@Override
	public String key() {
		return "square()";
	}
	
	// scale and keep aspect ratio
	private Bitmap scaleToFill(Bitmap b, int width, int height) {
		float factorH = height / (float) b.getWidth();
		float factorW = width / (float) b.getWidth();
		float factorToUse = (factorH > factorW) ? factorH : factorW;
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse),
				(int) (b.getHeight() * factorToUse), true);
	}

}
