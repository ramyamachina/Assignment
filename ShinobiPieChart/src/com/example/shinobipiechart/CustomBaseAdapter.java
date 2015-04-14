package com.example.shinobipiechart;

import java.util.ArrayList;

import com.example.shinobipiechart.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/* 
 * Description: Adapter class for list view 
 * 				for the customListViewActivity
 * 
 */

public class CustomBaseAdapter extends BaseAdapter {
	 private static ArrayList<SetterGetter> setGetList;
	 
	 private LayoutInflater mInflater;
	
	 public CustomBaseAdapter(Context context, ArrayList<SetterGetter> results) {
		 setGetList = results;
		 mInflater = LayoutInflater.from(context);
	 }
	
	 public int getCount() {
	  return setGetList.size();
	 }
	
	 public Object getItem(int position) {
	  return setGetList.get(position);
	 }
	
	 public long getItemId(int position) {
	  return position;
	 }
	
	 public View getView(int position, View convertView, ViewGroup parent) {
	  ViewHolder holder;
	  if (convertView == null) {
		   convertView = mInflater.inflate(R.layout.custom_list_view, null);
		   holder = new ViewHolder();
		   holder.txtName = (TextView) convertView.findViewById(R.id.name);
		   holder.txtWeek = (TextView) convertView.findViewById(R.id.week);
		   holder.txtCount = (TextView) convertView.findViewById(R.id.count);
		   holder.txtValue1 = (TextView) convertView.findViewById(R.id.value1);
		   holder.txtValue2 = (TextView) convertView.findViewById(R.id.value2);
		
		   convertView.setTag(holder);
	  } else {
		  holder = (ViewHolder) convertView.getTag();
	  }
	  
	  holder.txtName.setText(setGetList.get(position).getName());
	  holder.txtWeek.setText(setGetList.get(position).getWeek());
	  holder.txtCount.setText(setGetList.get(position).getCount());
	  holder.txtValue1.setText(setGetList.get(position).getEngValue());
	  holder.txtValue2.setText(setGetList.get(position).getuserValue());
	
	  return convertView;
	 }

	 static class ViewHolder {
	  TextView txtName;
	  TextView txtWeek;
	  TextView txtString;
	  TextView txtCount;
	  TextView txtValue1;
	  TextView txtValue2;
	 }
}