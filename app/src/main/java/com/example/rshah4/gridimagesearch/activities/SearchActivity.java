package com.example.rshah4.gridimagesearch.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.example.rshah4.gridimagesearch.R;
import com.example.rshah4.gridimagesearch.adapters.ImageResultsAdapter;
import com.example.rshah4.gridimagesearch.fragments.SelectFiltersDialog;
import com.example.rshah4.gridimagesearch.helpers.EndlessScrollListener;
import com.example.rshah4.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends FragmentActivity implements SelectFiltersDialog.SelectFiltersDialogListener {
	private GridView gvResults;
	private SearchView svQuery;
	private AsyncHttpClient httpClient;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResult;

	private String _size, _color, _type, _site; // Filters which user selects
	private String _query;

	private static final String URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0";
	private static final String QUERY_PARAM = "&q=";
	private static final String SIZE_PARAM = "&imgsz=";
	private static final String START_INDEX_PARAM = "&start=";
	private static final String SITESEARCH_PARAM = "&as_sitesearch=";
	private static final String IMG_COLOR_PARAM = "&imgcolor=";
	private static final String COUNT_PARAM = "&rsz=";
	private static final String IMG_TYPE_PARAM = "&imgtype=";

	private static final int MAX_FETCH_COUNT = 8;
	private static final int MAX_RESULTS_COUNT = 64;

	private String getUrl(String query, int startIndex, int count) {

		if(isNetworkAvailable()) {
			String url = URL + QUERY_PARAM + query;
			url = (startIndex != 0) ? url + START_INDEX_PARAM + startIndex : url;
			url = (count != 0) ? url + COUNT_PARAM + count : url;
			url = (_size != null && !_size.isEmpty()) ? url + SIZE_PARAM + _size
					: url;
			url = (_color != null && !_color.isEmpty()) ? url + IMG_COLOR_PARAM
					+ _color : url;
			url = (_type != null && !_type.isEmpty()) ? url + IMG_TYPE_PARAM
					+ _type : url;
			url = (_site != null && !_site.isEmpty()) ? url + SITESEARCH_PARAM
					+ _site : url;

			System.out.println("inside geturl " + url);

			return url;
		}
		else {
			//display in short period of time
			Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
			return "";
		}
	}

	private Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	private void showImageResults(String url, final boolean newSearch) {
		httpClient.get(url, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				JSONArray imageResultsJson = null;
				try {
					imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
					if (newSearch) {
						aImageResult.clear(); // clear in case there is a new search
					}
					// When you make changes to the adapter, it modifies
					// the underlying data automatically
					aImageResult.addAll(ImageResult.fromJson(imageResultsJson));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Append more data into the adapter
	private void customLoadMoreDataFromApi(int offset) {
		// If the offset is equal to the maximum number of images allowed from
		// Google API
		// Set it back to 0 to allow unlimited scrolling without error
		System.out.println("offset "+offset);

		if(isNetworkAvailable() && offset < MAX_RESULTS_COUNT) {
			String url = getUrl(_query, offset, MAX_FETCH_COUNT);
			System.out.println("url in offset " + url);
			showImageResults(url, false);
		}
		else {
			//display in short period of time
			//Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
		}
	}

	private void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Create an intent
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
				// Get the image result to display
				ImageResult result = imageResults.get(position);
				// Pass the image result into the intent
				i.putExtra("result", result); // result needs to be serializable
												// or parcelable
				// Launch the new activity
				startActivity(i);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		httpClient = new AsyncHttpClient();
		// Creates the data source
		imageResults = new ArrayList<ImageResult>();
		// Attaches the data source to an adapter
		aImageResult = new ImageResultsAdapter(this, imageResults);
		// Link the adapter to the view (grid view in this case)
		gvResults.setAdapter(aImageResult);
		// Link the scroll listener to grid view
		gvResults.setOnScrollListener(new EndlessScrollListener(8) {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				customLoadMoreDataFromApi(totalItemsCount);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_filters_select, menu);
		getMenuInflater().inflate(R.menu.search, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		svQuery = (SearchView) searchItem.getActionView();

		svQuery.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// perform query here
				if (query != null && !query.isEmpty()) {
					_query = query;
					String url = getUrl(_query, 0, MAX_FETCH_COUNT);
					System.out.println("first query " + url);

					if (isNetworkAvailable())
						showImageResults(url, true);
					else {
						//display in short period of time
						Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.miSelectFilter:
			// Create the intent
			/*
			 * Intent i = new Intent(this, SelectFiltersActivity.class); //
			 * Encode data in the intent i.putExtra("size", _size);
			 * i.putExtra("color", _color); i.putExtra("type", _type);
			 * i.putExtra("site", _site); // start the activity
			 * startActivityForResult(i, FILTERS_REQUEST_CODE);
			 */
			FragmentManager fm = getSupportFragmentManager();
			SelectFiltersDialog alertDialog = SelectFiltersDialog.newInstance(
					_size, _color, _type, _site);
			alertDialog.show(fm, "fragment_select_filters");
			return true;
		case R.id.action_search:
			svQuery.setIconified(false);
            return true;
		}
		return false;
	}

	/*
	 * Called when activity returns
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FILTERS_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// All things good
				_size = data.getStringExtra("size");
				_color = data.getStringExtra("color");
				_type = data.getStringExtra("type");
				_site = data.getStringExtra("site");
				String url = getUrl(_query, 0, MAX_FETCH_COUNT);
				showImageResults(url, true);
			}
		}
	}
	*/

	@Override
	public void onFinishedSelectDialog(String size, String color, String type,
			String site) {
		// All things good
		_size = size;
		_color = color;
		_type = type;
		_site = site;
		if (_query != null && !_query.isEmpty()) {
			String url = getUrl(_query, 0, MAX_FETCH_COUNT);

			if (isNetworkAvailable())
				showImageResults(url, true);
			else {
				//display in short period of time
				Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
