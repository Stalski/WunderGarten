package com.example.wundergarten.base;

/**
 * A callback interface that all activities containing this fragment must
 * implement. This mechanism allows activities to be notified of item
 * selections.
 */
public interface OnPlantSelected {
	public void onItemSelected(String id);
}
