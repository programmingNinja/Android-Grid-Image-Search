package com.example.rshah4.gridimagesearch.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResult implements Serializable {
	private static final long serialVersionUID = 4952883702071572127L;
	public String fullUrl;
	public String thumbUrl;
	public String title;
	public int height;
	public int width;
	
	public ImageResult(JSONObject json) {
		try {
			this.fullUrl = json.getString("url");
			this.thumbUrl = json.getString("tbUrl");
			this.title = json.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ImageResult> fromJson(JSONArray jsonArray) {
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				results.add(new ImageResult(jsonArray.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
}
