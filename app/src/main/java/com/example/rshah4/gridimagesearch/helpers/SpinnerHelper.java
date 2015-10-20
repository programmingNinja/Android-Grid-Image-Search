package com.example.rshah4.gridimagesearch.helpers;

import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class SpinnerHelper {
	public static void setSpinnerToValue(Spinner spinner, String value) {
		if (value == null || value.isEmpty()) {
			return; // No selection was made
		}
		
		int index = 0;
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i).equals(value)) {
				index = i;
				break; // terminate loop
			}
		}
		spinner.setSelection(index);
	}
}
