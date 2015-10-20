package com.example.rshah4.gridimagesearch.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.rshah4.gridimagesearch.R;
import com.example.rshah4.gridimagesearch.helpers.SpinnerHelper;

public class SelectFiltersDialog extends DialogFragment implements OnClickListener {
	private Spinner spFragImgSize;
	private Spinner spFragImgColor;
	private Spinner spFragImgType;
	private EditText etFragSite;
	private Button btnFragSave;
	
	public interface SelectFiltersDialogListener {
		void onFinishedSelectDialog(String size, String color, String type, String site);
	}
	
	public SelectFiltersDialog() {
		// Empty constructor required for DialogFragment
	}
	
	public static SelectFiltersDialog newInstance(String size, String color, String type, String site) {
		SelectFiltersDialog frag = new SelectFiltersDialog();
		Bundle args = new Bundle();
		args.putString("size", size);
		args.putString("build/intermediates/exploded-aar/com.android.support/appcompat-v7/23.0.1/res/color", color);
		args.putString("type", type);
		args.putString("site", site);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_select_filters, container);
		spFragImgSize = (Spinner) view.findViewById(R.id.spFragImgSize);
		spFragImgColor = (Spinner) view.findViewById(R.id.spFragImgColor);
		spFragImgType = (Spinner) view.findViewById(R.id.spFragImgType);
		etFragSite = (EditText) view.findViewById(R.id.etFragSite);
		btnFragSave = (Button) view.findViewById(R.id.btnFragSave);
		
		String size = getArguments().getString("size", "");
		String color = getArguments().getString("build/intermediates/exploded-aar/com.android.support/appcompat-v7/23.0.1/res/color", "");
		String type = getArguments().getString("type", "");
		String site = getArguments().getString("site", "");

		getDialog().setTitle("Advanced Fliters");
		
		SpinnerHelper.setSpinnerToValue(spFragImgSize, size);
		SpinnerHelper.setSpinnerToValue(spFragImgColor, color);
		SpinnerHelper.setSpinnerToValue(spFragImgType, type);
		etFragSite.setText(site);
		etFragSite.requestFocus();
		
		btnFragSave.setOnClickListener(this);
				
		// Show soft keyboard automatically
//		getDialog().getWindow().setSoftInputMode(
//		WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		String size = spFragImgSize.getSelectedItem().toString();
		String color = spFragImgColor.getSelectedItem().toString();
		String type = spFragImgType.getSelectedItem().toString();
		String site = etFragSite.getText().toString();
		
		SelectFiltersDialogListener listener = (SelectFiltersDialogListener) getActivity();
		listener.onFinishedSelectDialog(size, color, type, site);
		dismiss();	
	}
}
