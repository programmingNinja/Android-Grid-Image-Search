package com.example.rshah4.gridimagesearch.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.rshah4.gridimagesearch.R;
import com.example.rshah4.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageDisplayActivity extends Activity {
	ImageResult result;
	ImageView ivImageResult;
	TextView tvImageTitle;
	ShareActionProvider miShareAction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		// Pull out the url from the intent
		result = (ImageResult) getIntent().getSerializableExtra("result");
		// Find the image view
		ivImageResult = (ImageView) findViewById(R.id.ivImageResult);
		tvImageTitle = (TextView) findViewById(R.id.tvImageTitle);
		tvImageTitle.setText(Html.fromHtml(result.title));
			
		// Load the image into the image view
		Picasso.with(this).load(result.fullUrl)
		//		.resize(ivImageResult.getWidth(), ivImageResult.getWidth())
				.into(ivImageResult, new Callback() {
					@Override
					public void onSuccess() {
						// Setup share intent now that image has loaded
						setupShareIntent();
					}

					@Override
					public void onError() {
						// ...
					}
				});
	}
	
	// Gets the image URI and setup the associated share intent to hook into the provider
	private void setupShareIntent() {
	    // Fetch Bitmap Uri locally
	    Uri bmpUri = getLocalBitmapUri(ivImageResult); // see previous remote images section
	    // Create share intent as described above
	    Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
	    shareIntent.setType("image/*");
	    // Attach share event to the menu item provider
	    miShareAction.setShareIntent(shareIntent);
	}
	
	// Returns the URI path to the Bitmap displayed in specified ImageView
	public Uri getLocalBitmapUri(ImageView imageView) {
	    // Extract Bitmap from ImageView drawable
	    Drawable drawable = imageView.getDrawable();
	    Bitmap bmp = null;
	    if (drawable instanceof BitmapDrawable){
	       bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
	    } else {
	       return null;
	    }
	    // Store image to default external storage directory
	    Uri bmpUri = null;
	    try {
	        File file =  new File(Environment.getExternalStoragePublicDirectory(  
		        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
	        file.getParentFile().mkdirs();
	        FileOutputStream out = new FileOutputStream(file);
	        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
	        out.close();
	        bmpUri = Uri.fromFile(file);
	        Log.d("DEBUG", bmpUri.toString());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return bmpUri;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		MenuItem item = menu.findItem(R.id.miShareImage);
		miShareAction = (ShareActionProvider) item.getActionProvider();
		return true;
	}
	
}
